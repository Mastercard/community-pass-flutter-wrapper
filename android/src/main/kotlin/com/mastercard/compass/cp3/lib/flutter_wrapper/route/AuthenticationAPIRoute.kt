package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import com.mastercard.compass.cp3.lib.flutter_wrapper.CompassKernelUIController
import com.mastercard.compass.cp3.lib.flutter_wrapper.FlutterError
import com.mastercard.compass.cp3.lib.flutter_wrapper.FormFactor
import com.mastercard.compass.cp3.lib.flutter_wrapper.Match
import com.mastercard.compass.cp3.lib.flutter_wrapper.RegistrationDataResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.UserVerificationResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.VerifyPasscodeResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.GetRegistrationDataCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.UserVerificationCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.VerifyPasscodeCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.DATA
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.ERROR_CODE
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.ERROR_MESSAGE
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.FORM_FACTOR
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.MODALITIES
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.PASSCODE
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.PROGRAM_GUID
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.QR
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.RELIANT_APP_GUID
import com.mastercard.compass.model.biometric.BiometricMatchResult
import com.mastercard.compass.model.card.RegistrationStatusData
import com.mastercard.compass.model.card.VerifyPasscodeResponse

class AuthenticationAPIRoute(
    private val activity: Activity,
    private val helper: CompassKernelUIController.CompassHelper
    ) {
    private lateinit var registrationDataResultCallback: (Result<RegistrationDataResult>) -> Unit
    private lateinit var verifyPasscodeDataResultCallback: (Result<VerifyPasscodeResult>) -> Unit
    private lateinit var userVerificationResultCallback: (Result<UserVerificationResult>) -> Unit

    companion object {
        const val REGISTRATION_DATA_REQUEST_CODE = 300
        const val VERIFY_PASSCODE_REQUEST_CODE = 301
        const val USER_VERIFICATION_REQUEST_CODE = 302
    }

    fun startGetRegistrationDataIntent(reliantGUID: String, programGUID: String, callback: (Result<RegistrationDataResult>) -> Unit){
        registrationDataResultCallback = callback
        val intent = Intent(activity, GetRegistrationDataCompassApiHandlerActivity::class.java).apply {
            putExtra(RELIANT_APP_GUID, reliantGUID)
            putExtra(PROGRAM_GUID, programGUID)
        }

        activity.startActivityForResult(intent, REGISTRATION_DATA_REQUEST_CODE)
    }

    fun startVerifyPasscodeIntent(
        reliantGUID: String,
        programGUID: String,
        passcode: String,
        formFactor: FormFactor,
        qrCpUserProfile: String?,
        callback: (Result<VerifyPasscodeResult>) -> Unit
    ) {
        verifyPasscodeDataResultCallback = callback

        val intent = Intent(activity, VerifyPasscodeCompassApiHandlerActivity::class.java).apply {
            putExtra(RELIANT_APP_GUID, reliantGUID)
            putExtra(PROGRAM_GUID, programGUID)
            putExtra(QR, qrCpUserProfile)
            putExtra(FORM_FACTOR, formFactor.toString())
            putExtra(PASSCODE, passcode)
        }

        activity.startActivityForResult(intent, VERIFY_PASSCODE_REQUEST_CODE)
    }

    fun startUserVerificationIntent(
        reliantGUID: String,
        programGUID: String,
        formFactor: FormFactor,
        qrBase64: String?,
        modalities: List<String>,
        callback: (Result<UserVerificationResult>) -> Unit
    ) {
        userVerificationResultCallback = callback

        val intent = Intent(activity, UserVerificationCompassApiHandlerActivity::class.java).apply {
            putExtra(RELIANT_APP_GUID, reliantGUID)
            putExtra(PROGRAM_GUID, programGUID)
            putExtra(QR, qrBase64)
            putExtra(FORM_FACTOR, formFactor.toString())
            putExtra(MODALITIES, modalities as ArrayList)
        }

        activity.startActivityForResult(intent, USER_VERIFICATION_REQUEST_CODE)
    }

    fun handleGetRegistrationDataIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.get(DATA) as RegistrationStatusData
                val result = RegistrationDataResult.fromList(listOf(response.isRegisteredInProgram,  parseAuthMethods(response.authMethods.authType), parseAuthMethods(response.authMethods.modalityType), response.rId))

                registrationDataResultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.extras?.get(ERROR_CODE) ?: 0
                val message =  data?.extras?.get(ERROR_MESSAGE) ?: "Something went wrong."
                registrationDataResultCallback(Result.failure(FlutterError(code.toString(), message.toString(), null)))
            }
        }
    }

    fun handleVerifyPasscodeDataResponse(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.get(DATA) as VerifyPasscodeResponse

                val result = VerifyPasscodeResult.fromList(listOf(response.rid, response.status, response.counter?.retryCount))
                verifyPasscodeDataResultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.extras?.get(ERROR_CODE) ?: 0
                val message =  data?.extras?.get(ERROR_MESSAGE) ?: "Something went wrong."
                verifyPasscodeDataResultCallback(Result.failure(FlutterError(code.toString(), message.toString(), null)))
            }
        }
    }

    fun handleUserVerificationResponse(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val jwt = data?.extras?.get(DATA).toString()
                val response = helper.parseJWT(jwt) as CompassKernelUIController.CompassHelper.CompassJWTResponse.Success

                val result = UserVerificationResult.fromList(listOf(response.isMatchFound, response.rId, parseBiometricMatchList(response.biometricMatchList)))
                userVerificationResultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.extras?.get(ERROR_CODE) ?: 0
                val message =  data?.extras?.get(ERROR_MESSAGE) ?: "Something went wrong."
                userVerificationResultCallback(Result.failure(FlutterError(code.toString(), message.toString(), null)))
            }
        }
    }
}

fun parseAuthMethods(items: List<Any>?): List<String?> {
    return items?.map { it.toString() } ?: listOf()
}

fun parseBiometricMatchList(items: List<BiometricMatchResult>?): List<Match?> {
    val newListOfItems = mutableListOf<Match>()
     items?.forEach { newListOfItems.add(Match(modality = it.modality, normalizedScore = it.normalizedScore.toString(), distance = it.distance.toString())) }
    return  newListOfItems
}
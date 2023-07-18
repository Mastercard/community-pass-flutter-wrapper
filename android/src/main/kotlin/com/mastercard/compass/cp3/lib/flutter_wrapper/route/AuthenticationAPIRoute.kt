package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import com.mastercard.compass.cp3.lib.flutter_wrapper.CompassKernelUIController
import com.mastercard.compass.cp3.lib.flutter_wrapper.FlutterError
import com.mastercard.compass.cp3.lib.flutter_wrapper.FormFactor
import com.mastercard.compass.cp3.lib.flutter_wrapper.RegistrationDataResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.UserVerificationResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.VerifyPasscodeResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.GetRegistrationDataCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.UserVerificationCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.VerifyPasscodeCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.jwt.RegisterUserForBioTokenResponse
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
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
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
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.QR, qrCpUserProfile)
            putExtra(Key.FORM_FACTOR, formFactor)
            putExtra(Key.PASSCODE, passcode)
        }

        activity.startActivityForResult(intent, VERIFY_PASSCODE_REQUEST_CODE)
    }


    fun startUserVerificationIntent(
        reliantGUID: String,
        programGUID: String,
        formFactor: String,
        qrBase64: String?,
        modalities: List<String>,
        callback: (Result<UserVerificationResult>) -> Unit
    ) {
        userVerificationResultCallback = callback

        val intent = Intent(activity, UserVerificationCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.QR, qrBase64)
            putExtra(Key.FORM_FACTOR, formFactor)
            putExtra(Key.MODALITIES, modalities as ArrayList)
        }

        activity.startActivityForResult(intent, USER_VERIFICATION_REQUEST_CODE)
    }

    fun handleGetRegistrationDataIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.get(Key.DATA) as RegistrationStatusData
                val result = RegistrationDataResult.fromList(listOf(response.rId, response.authMethods, response.isRegisteredInProgram))
                registrationDataResultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                registrationDataResultCallback(Result.failure(FlutterError(code.toString(), message, null)))
            }
        }
    }

    fun handleVerifyPasscodeDataResponse(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.get(Key.DATA) as VerifyPasscodeResponse

                val result = VerifyPasscodeResult.fromList(listOf(response.rid, response.counter?.retryCount, response.status))
                verifyPasscodeDataResultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                verifyPasscodeDataResultCallback(Result.failure(FlutterError(code.toString(), message, null)))
            }
        }
    }

    fun handleUserVerificationResponse(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val jwt = data?.extras?.get(Key.DATA).toString()
                val response = helper.parseJWT(jwt) as CompassKernelUIController.CompassHelper.CompassJWTResponse.Success

                val result = UserVerificationResult.fromList(listOf(response.rId, response.rId, response.isMatchFound, response.biometricMatchList))
                userVerificationResultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                userVerificationResultCallback(Result.failure(FlutterError(code.toString(), message, null)))
            }
        }
    }
}
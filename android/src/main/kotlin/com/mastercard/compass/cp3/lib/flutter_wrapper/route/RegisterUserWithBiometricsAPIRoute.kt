package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.mastercard.compass.jwt.RegisterUserForBioTokenResponse
import com.mastercard.compass.cp3.lib.flutter_wrapper.CompassKernelUIController
import com.mastercard.compass.base.EnrolmentStatus
import com.mastercard.compass.cp3.lib.flutter_wrapper.FlutterError
import com.mastercard.compass.cp3.lib.flutter_wrapper.OperationMode
import com.mastercard.compass.cp3.lib.flutter_wrapper.RegisterUserWithBiometricsResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.RegisterUserForBioTokenCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.model.biometrictoken.Modality

class RegisterUserWithBiometricsAPIRoute(private val activity: Activity) {
    private lateinit var resultCallback: (Result<RegisterUserWithBiometricsResult>) -> Unit

    companion object {
        val REQUEST_CODE_RANGE = 300 until 400
        const val TAG = "REGISTER_USER_WITH_BIOMETRICS"
        const val REGISTER_BIOMETRICS_REQUEST_CODE = 300
    }

    fun startRegisterUserWithBiometricsIntent(
        reliantGUID: String,
        programGUID: String,
        consentId: String,
        modalities: List<String>,
        operationMode: OperationMode,
        callback: (Result<RegisterUserWithBiometricsResult>) -> Unit
    ){
        val intent = Intent(activity, RegisterUserForBioTokenCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.CONSENT_ID, consentId)
            putExtra(Key.MODALITIES, modalities as ArrayList<String>)
            putExtra(Key.OPERATION_MODE, operationMode.toString())
        }

        resultCallback = callback
        activity.startActivityForResult(intent, REGISTER_BIOMETRICS_REQUEST_CODE)
    }

    fun handleRegisterUserWithBiometricsIntentResponse(
        resultCode: Int,
        data: Intent?,
        helperObject: CompassKernelUIController.CompassHelper
    ) {

        when (resultCode) {
            Activity.RESULT_OK -> {
                val jwt = data?.extras?.getString(Key.DATA).toString()
                val response: RegisterUserForBioTokenResponse = helperObject.parseBioTokenJWT(jwt)
                val result = RegisterUserWithBiometricsResult.fromList(listOf(response.bioToken, response.programGUID, response.rId, parseEnrolmentStatus(response.enrolmentStatus!!)))
                resultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE) ?: "Unknown error"
                resultCallback(Result.failure(FlutterError(code.toString(), message, null)))
            }
        }
    }
}

private fun parseEnrolmentStatus(enrolmentStatus: EnrolmentStatus): Int {
   return when(enrolmentStatus){
        EnrolmentStatus.EXISTING -> 0
        EnrolmentStatus.NEW -> 1
    }
}
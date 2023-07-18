package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.mastercard.compass.base.ResponseStatus
import com.mastercard.compass.cp3.lib.flutter_wrapper.FlutterError
import com.mastercard.compass.cp3.lib.flutter_wrapper.SaveBiometricConsentResult
import com.mastercard.compass.model.consent.ConsentResponse
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.BiometricConsentCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key

class BiometricConsentAPIRoute(
    private val activity: Activity
    ) {
     private lateinit var resultCallback: (Result<SaveBiometricConsentResult>) -> Unit
    companion object {
        val REQUEST_CODE_RANGE = 600 until 700

        const val BIOMETRIC_CONSENT_REQUEST_CODE = 600
    }

    fun startBiometricConsentIntent(reliantGUID: String, programGUID: String, consumerConsentValue: Boolean, callback: (Result<SaveBiometricConsentResult>) -> Unit){
        val intent = Intent(activity, BiometricConsentCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RELIANT_APP_GUID, reliantGUID )
            putExtra(Key.CONSUMER_CONSENT_VALUE, consumerConsentValue)
        }

        resultCallback = callback
        activity.startActivityForResult(intent, BIOMETRIC_CONSENT_REQUEST_CODE)
    }

    fun handleBiometricConsentIntentResponse(
        resultCode: Int,
        data: Intent?
    ) {

        when (resultCode) {
            Activity.RESULT_OK -> {
                val response: ConsentResponse = data?.extras?.get(Key.DATA) as ConsentResponse
                val result = SaveBiometricConsentResult.fromList(listOf(response.consentId, parseResponseStatus(response.responseStatus)))
                resultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE) ?: "Unknown error"
                Log.e("COMPASS_ERROR_FOUND", "$code $message")
                resultCallback(Result.failure(FlutterError(code.toString(), message, null)))
            }
        }
    }
}

private fun parseResponseStatus(responseStatus: ResponseStatus): Int {
    return when(responseStatus){
        ResponseStatus.SUCCESS -> 0
        ResponseStatus.ERROR -> 1
        ResponseStatus.UNDEFINED -> 2
    }
}
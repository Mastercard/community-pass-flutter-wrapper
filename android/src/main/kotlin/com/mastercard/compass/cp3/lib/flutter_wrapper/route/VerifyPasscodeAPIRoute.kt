package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import com.mastercard.compass.cp3.java_flutter_wrapper.CompassApiFlutter
import com.mastercard.compass.cp3.java_flutter_wrapper.CompassApiFlutter.VerifyPasscodeResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.VerifyPasscodeCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.ErrorCode
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key

class GetVerifyPasscodeAPIRoute(private val activity: Activity) {
    private lateinit var verifyPasscodeResult: CompassApiFlutter.Result<VerifyPasscodeResult>

     companion object {
    val REQUEST_CODE_RANGE = 800 until 900

    const val GET_VERIFY_PASSCODE_REQUEST_CODE = 800
    private const val TAG = "GetVerifyPasscodeAPIRoute"
  }

    fun startVerifyPasscodeIntent(reliantGUID: String, programGUID: String, passcode: String, result: CompassApiFlutter.Result<VerifyPasscodeResult>?){
        val intent = Intent(activity, VerifyPasscodeCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.PASSCODE, passcode)
        }

        verifyPasscodeResult = result!!
        activity.startActivityForResult(intent, GET_VERIFY_PASSCODE_REQUEST_CODE)
    }

    fun handleVerifyPasscodeIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response: VerifyPasscodeResponse  = data?.extras?.get(Key.DATA) as VerifyPasscodeResponse
                val result = VerifyPasscodeResult.Builder()
                    .setStatus(response.status)
                    .setRID(response.rId)
                    .setRetryCount(response.retryCount)
                    .build()
                    verifyPasscodeResult.success(result)
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, ErrorCode.UNKNOWN)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                verifyPasscodeResult.error(CompassThrowable(code, message))
            }
        }
    }
}
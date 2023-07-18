package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.mastercard.compass.base.ResponseStatus
import com.mastercard.compass.cp3.lib.flutter_wrapper.FlutterError
import com.mastercard.compass.cp3.lib.flutter_wrapper.WritePasscodeResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.WritePasscodeCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key

class ConsumerDevicePasscodeAPIRoute(private val activity: Activity) {

    private lateinit var resultCallback: (Result<WritePasscodeResult>) -> Unit

    companion object {
        val REQUEST_CODE_RANGE = 500 until 600

        const val WRITE_PASSCODE_REQUEST_CODE = 500
    }

    fun startWritePasscodeIntent(reliantGUID: String, programGUID: String, rId: String, passcode: String, callback: (Result<WritePasscodeResult>) -> Unit){
        val intent = Intent(activity, WritePasscodeCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RID, rId)
            putExtra(Key.PASSCODE, passcode)
        }

        resultCallback = callback
        activity.startActivityForResult(intent, WRITE_PASSCODE_REQUEST_CODE)
    }

    fun handleWritePasscodeIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.getString(Key.DATA).toString()
                val result = WritePasscodeResult.fromList(listOf(parseResponseStatus(response)))
                resultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                resultCallback(Result.failure(FlutterError(code.toString(), message, null)))
            }
        }
    }
}
private fun parseResponseStatus(responseStatus: String): Int {
    return when(responseStatus){
        "Success" -> 0
       else -> 1
    }
}
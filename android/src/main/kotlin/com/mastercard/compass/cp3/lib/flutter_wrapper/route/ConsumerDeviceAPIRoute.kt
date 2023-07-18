package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import com.mastercard.compass.cp3.lib.flutter_wrapper.FlutterError
import com.mastercard.compass.cp3.lib.flutter_wrapper.WriteProfileResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.WriteProfileCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key

class ConsumerDeviceAPIRoute( private val activity: Activity ) {
    private lateinit var resultCallback:  (Result<WriteProfileResult>) -> Unit

    companion object {
        val REQUEST_CODE_RANGE = 200 until 300

        const val WRITE_PROFILE_REQUEST_CODE = 200
    }

    fun startWriteProfileIntent(reliantGUID: String, programGUID: String, rId: String, overwriteCard: Boolean, result: (Result<WriteProfileResult>) -> Unit){
        val intent = Intent(activity, WriteProfileCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RID, rId)
            putExtra(Key.OVERWRITE_CARD, overwriteCard)
        }

        resultCallback = result
        activity.startActivityForResult(intent, WRITE_PROFILE_REQUEST_CODE)
    }

    fun handleWriteProfileIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.getString(Key.DATA).toString()
                val result = WriteProfileResult.fromList(listOf(response))
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
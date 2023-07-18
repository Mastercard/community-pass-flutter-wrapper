package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import com.mastercard.compass.cp3.lib.flutter_wrapper.FlutterError
import com.mastercard.compass.cp3.lib.flutter_wrapper.RegisterBasicUserResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.WritePasscodeResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.RegisterBasicUserCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key

class RegisterBasicUserAPIRoute(private val activity: Activity) {
    private lateinit var resultCallback: (Result<RegisterBasicUserResult>) -> Unit

    companion object {
        val REQUEST_CODE_RANGE = 400 until 500

        const val REGISTER_BASIC_USER_REQUEST_CODE = 400
    }

    fun startRegisterBasicUserIntent(reliantGUID: String, programGUID: String, callback: (Result<RegisterBasicUserResult>) -> Unit){

        val intent = Intent(activity, RegisterBasicUserCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
        }

        resultCallback = callback
        activity.startActivityForResult(intent, REGISTER_BASIC_USER_REQUEST_CODE)
    }

    fun handleRegisterBasicUserIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.getString(Key.DATA).toString()
                val result = RegisterBasicUserResult.fromList(listOf(response))
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
package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import com.mastercard.compass.cp3.lib.flutter_wrapper.BlacklistFormFactorResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.FlutterError
import com.mastercard.compass.cp3.lib.flutter_wrapper.FormFactor
import com.mastercard.compass.cp3.lib.flutter_wrapper.WritePasscodeResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.WriteProfileResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.BlacklistFormFactorApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.WritePasscodeCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.WriteProfileCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.model.blacklist.BlacklistFormFactorResponse

class ConsumerDeviceAPIRoute( private val activity: Activity ) {
    private lateinit var writeProfileResultCallback: (Result<WriteProfileResult>) -> Unit
    private lateinit var writePasscodeResultCallback: (Result<WritePasscodeResult>) -> Unit
    private lateinit var getBlacklistFormFactorResultCallback: (Result<BlacklistFormFactorResult>) -> Unit

    companion object {
        const val WRITE_PROFILE_REQUEST_CODE = 200
        const val WRITE_PASSCODE_REQUEST_CODE = 201
        const val BLACKLIST_FORM_FACTOR_REQUEST_CODE = 202
    }

    fun startWriteProfileIntent(
        reliantGUID: String,
        programGUID: String,
        rID: String,
        overwriteCard: Boolean,
        callback: (Result<WriteProfileResult>) -> Unit
    ) {
        writeProfileResultCallback = callback
        val intent = Intent(activity, WriteProfileCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RID, rID)
            putExtra(Key.OVERWRITE_CARD, overwriteCard)
        }

        activity.startActivityForResult(intent, WRITE_PROFILE_REQUEST_CODE)
    }

    fun startWritePasscodeIntent(
        reliantGUID: String,
        programGUID: String,
        rID: String,
        passcode: String,
        callback: (Result<WritePasscodeResult>) -> Unit
    ) {
        writePasscodeResultCallback = callback
        val intent = Intent(activity, WritePasscodeCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RID, rID)
            putExtra(Key.PASSCODE, passcode)
        }

        activity.startActivityForResult(intent, WRITE_PASSCODE_REQUEST_CODE)
    }

    fun startBlacklistFormFactorIntent(
        reliantGUID: String,
        programGUID: String,
        rID: String,
        consumerDeviceNumber: String,
        type: FormFactor,
        callback: (Result<BlacklistFormFactorResult>) -> Unit
    ) {
        getBlacklistFormFactorResultCallback = callback

        val intent = Intent(activity, BlacklistFormFactorApiHandlerActivity::class.java).apply {
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RID, rID)
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.CONSUMER_DEVICE_NUMBER, consumerDeviceNumber)
            putExtra(Key.FORM_FACTOR, type.name)
        }

        activity.startActivityForResult(intent, BLACKLIST_FORM_FACTOR_REQUEST_CODE)
    }


    fun handleWriteProfileIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.getString(Key.DATA).toString()
                val result = WriteProfileResult.fromList(listOf(response))
                writeProfileResultCallback(Result.success(result))
            }

            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                writeProfileResultCallback(
                    Result.failure(
                        FlutterError(
                            code.toString(),
                            message,
                            null
                        )
                    )
                )
            }
        }
    }

    fun handleWritePasscodeIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.getString(Key.DATA).toString()
                val result = WritePasscodeResult.fromList(listOf(parseResponseStatus(response)))
                writePasscodeResultCallback(Result.success(result))
            }

            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                writePasscodeResultCallback(
                    Result.failure(
                        FlutterError(
                            code.toString(),
                            message,
                            null
                        )
                    )
                )
            }
        }
    }


    fun handleBlacklistFormFactorIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.get(Key.DATA) as BlacklistFormFactorResponse
                val result =
                    BlacklistFormFactorResult.fromList(listOf(parseResponseStatus(responseStatus = response.status.name)))
                getBlacklistFormFactorResultCallback(Result.success(result))
            }

            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                getBlacklistFormFactorResultCallback(
                    Result.failure(
                        FlutterError(
                            code.toString(),
                            message,
                            null
                        )
                    )
                )
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
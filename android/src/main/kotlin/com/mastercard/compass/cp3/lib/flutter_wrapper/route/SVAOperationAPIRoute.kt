package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.mastercard.compass.cp3.lib.flutter_wrapper.CreateSVAResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.FlutterError
import com.mastercard.compass.cp3.lib.flutter_wrapper.ReadSVAResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.SVA
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.CreateSVACompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.ReadSVACompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.model.sva.SVARecord

class SVAOperationAPIRoute(
    private val activity: Activity
    ) {
    private lateinit var createSVAResultCallback: (Result<CreateSVAResult>) -> Unit
    private lateinit var readSVAResultCallback: (Result<ReadSVAResult>) -> Unit

    companion object {
        const val CREATE_SVA_REQUEST_CODE = 600
        const val READ_SVA_REQUEST_CODE = 601
    }

    lateinit var eVoucherType: String

    fun startCreateSvaIntent(
        reliantGUID: String,
        programGUID: String,
        rID: String?,
        sva: SVA,
        callback: (Result<CreateSVAResult>) -> Unit
    ) {
        createSVAResultCallback = callback

        val intent = Intent(activity, CreateSVACompassApiHandlerActivity::class.java).apply {
            putExtra(Key.UNIT, sva.unit)
            putExtra(Key.RID, rID)
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.TYPE, type)
            if (type.equals("EVoucherSVA", true)) {
                putExtra(Key.E_VOUCHER_TYPE, eVoucherType)
            }
        }

        activity.startActivityForResult(intent, CREATE_SVA_REQUEST_CODE)
    }

    fun startReadSvaIntent(
        reliantGUID: String,
        programGUID: String,
        rID: String,
        svaUnit: String,
        callback: (Result<ReadSVAResult>) -> Unit
    ) {
        readSVAResultCallback = callback
        val intent = Intent(activity, ReadSVACompassApiHandlerActivity::class.java).apply {
            putExtra(Key.UNIT, svaUnit)
            putExtra(Key.RID, rID)
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
        }

        activity.startActivityForResult(intent, READ_SVA_REQUEST_CODE)
    }


    fun handleCreateSvaIntentResponse(
        resultCode: Int,
        data: Intent?
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.get(Key.DATA)
                val result = CreateSVAResult.fromList(listOf(response))
                createSVAResultCallback(Result.success(result))
            }

            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE) ?: "Unknown error"
                Log.e("COMPASS_ERROR_FOUND", "$code $message")
                createSVAResultCallback(
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

    fun handleReadSvaIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response: SVARecord = data?.extras?.get(Key.DATA) as SVARecord
                val result = ReadSVAResult.fromList(listOf(response))
                readSVAResultCallback(Result.success(result))
            }

            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE) ?: "Unknown error"
                Log.e("COMPASS_ERROR_FOUND", "$code $message")
                readSVAResultCallback(
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
package com.mastercard.compass.cp3.lib.flutter_wrapper.route


import android.app.Activity
import android.content.Intent
import android.util.Log
import com.mastercard.compass.cp3.java_flutter_wrapper.CompassApiFlutter
import com.mastercard.compass.cp3.java_flutter_wrapper.CompassApiFlutter.WriteProgramSpaceResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.WriteProgramSpaceCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.ErrorCode
import com.mastercard.compass.model.programspace.WriteProgramSpaceDataResponse
//import timber.log.Timber

class WriteProgramSpaceAPIRoute( private val activity: Activity ) {

    private lateinit var writeProgramSpaceAPIRouteResult: CompassApiFlutter.Result<WriteProgramSpaceResult>

    companion object {
        val REQUEST_CODE_RANGE = 1000 until 1100
        const val WRITE_PROGRAM_SPACE_REQUEST_CODE = 901
        const val TAG = "WriteProgramSpaceAPIRoute"
    }

    fun startWriteProgramSpaceIntent(reliantGUID: String, programGUID: String, rId: String, programSpaceData: String, encryptData: Boolean, result: CompassApiFlutter.Result<CompassApiFlutter.WriteProgramSpaceResult>?){
        val intent = Intent(activity, WriteProgramSpaceCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RID, rId)
            putExtra(Key.PROGRAM_SPACE_DATA, programSpaceData)
            putExtra(Key.ENCRYPT_DATA, encryptData)
        }

        writeProgramSpaceAPIRouteResult = result!!;
        activity.startActivityForResult(intent, WRITE_PROGRAM_SPACE_REQUEST_CODE)
    }

    fun handleWriteProgramSpaceIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response: WriteProgramSpaceDataResponse = data?.extras?.get(Key.DATA) as WriteProgramSpaceDataResponse
                val result = WriteProgramSpaceResult.Builder()
                    .setIsSuccess(response.isSuccess).build()

                Log.d(TAG, response.toString())
                if(response.isSuccess){
                    writeProgramSpaceAPIRouteResult.success(result);
                } else {
                    writeProgramSpaceAPIRouteResult.error(CompassThrowable(ErrorCode.UNKNOWN, "Unknown error" ));
                }
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, ErrorCode.UNKNOWN)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!

                Log.e(TAG,"Error $code Message $message")
                writeProgramSpaceAPIRouteResult.error(CompassThrowable(code, message ))
            }
        }
    }
}
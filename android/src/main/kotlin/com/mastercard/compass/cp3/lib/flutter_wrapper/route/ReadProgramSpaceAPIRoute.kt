package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import com.mastercard.compass.cp3.java_flutter_wrapper.CompassApiFlutter
import com.mastercard.compass.cp3.lib.flutter_wrapper.CompassKernelUIController
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.ReadProgramSpaceCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util.DefaultCryptoService
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.ErrorCode
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.jwt.JwtConstants
import com.mastercard.compass.model.programspace.ReadProgramSpaceDataResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
//import timber.log.Timber
import java.security.PublicKey
import java.security.SignatureException

class ReadProgramSpaceAPIRoute( private val activity: Activity,   private val helperObject: CompassKernelUIController.CompassHelper,
                                private val cryptoService: DefaultCryptoService?) {

    private lateinit var readProgramSpaceAPIRouteResult: CompassApiFlutter.Result<CompassApiFlutter.ReadProgramSpaceResult>

    private var decryptData: Boolean = false
    val kernelPublicKey: PublicKey? = helperObject.getKernelJWTPublicKey()

    companion object {
        val REQUEST_CODE_RANGE = 900 until 1000
        const val READ_PROGRAM_SPACE_REQUEST_CODE = 900
        const val TAG = "ReadProgramSpaceAPIRoute"
    }

    fun startReadProgramSpaceIntent(reliantGUID: String, programGUID: String, rID: String, decryptData: Boolean, result: CompassApiFlutter.Result<CompassApiFlutter.ReadProgramSpaceResult>?){

        //Timber.d("reliantGUID: $reliantGUID")
        //Timber.d("programGUID: $programGUID")
        //Timber.d("rID: $rID")
        //Timber.d("decryptData: $decryptData")

        val intent = Intent(activity, ReadProgramSpaceCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RID, rID)
        }

        readProgramSpaceAPIRouteResult = result!!;

        activity.startActivityForResult(intent, READ_PROGRAM_SPACE_REQUEST_CODE)
    }

    private fun parseJWT(jwt: String): String? {
        try {
            val data =
                Jwts.parserBuilder().setSigningKey(kernelPublicKey).build().parseClaimsJws(jwt).body
            return data[JwtConstants.JWT_PAYLOAD].toString()
        } catch (e: SignatureException) {
            //Timber.tag(TAG).e(e, "parseJWT: Failed to validate JWT")
        } catch (e: ExpiredJwtException) {
            //Timber.tag(TAG).e(e, "parseJWT: JWT expired")
        } catch (e: Exception) {
            //Timber.tag(TAG).e(e, "parseJWT: Claims passed from Kernel are empty, null or invalid")
        }
        return  ""
    }

    fun handleReadProgramSPaceIntentResponse(
        resultCode: Int,
        data: Intent?
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                //val resultMap = Arguments.createMap()
                val response: ReadProgramSpaceDataResponse = data?.extras?.get(Key.DATA) as ReadProgramSpaceDataResponse
                var extractedData: String = parseJWT(response.jwt).toString()

                if(decryptData){
                    extractedData = String(cryptoService!!.decrypt(extractedData))
                }

                val result = CompassApiFlutter.ReadProgramSpaceResult.Builder()
                    .setProgramSpaceData(extractedData).build()

                //Timber.tag(TAG).d(extractedData)
                //resultMap.putString("programSpaceData", extractedData)
                readProgramSpaceAPIRouteResult.success(result)
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, ErrorCode.UNKNOWN)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!

                //Timber.e("Error $code Message $message")
                readProgramSpaceAPIRouteResult.error(CompassThrowable(code, message ))
            }
        }
    }
}
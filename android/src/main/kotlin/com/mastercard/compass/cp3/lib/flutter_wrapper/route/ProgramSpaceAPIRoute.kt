package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.mastercard.compass.cp3.lib.flutter_wrapper.CompassKernelUIController
import com.mastercard.compass.cp3.lib.flutter_wrapper.FlutterError
import com.mastercard.compass.cp3.lib.flutter_wrapper.ReadProgramSpaceResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.WriteProgramSpaceResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.BiometricConsentCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.ReadProgramSpaceCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.WriteProgramSpaceCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util.DefaultCryptoService
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util.DefaultCryptoService.Companion.TAG
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.jwt.JwtConstants
import com.mastercard.compass.model.programspace.ReadProgramSpaceDataResponse
import com.mastercard.compass.model.programspace.WriteProgramSpaceDataResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import java.security.PublicKey
import java.security.SignatureException

class ProgramSpaceAPIRoute(
    private val activity: Activity,
    helperObject: CompassKernelUIController.CompassHelper,
    private val cryptoService: DefaultCryptoService?
    ) {

    private lateinit var readProgramResultCallback:  (Result<ReadProgramSpaceResult>) -> Unit
    private lateinit var writeProgramResultCallback:  (Result<WriteProgramSpaceResult>) -> Unit
    private var decryptProgramData: Boolean = false
    private val kernelPublicKey: PublicKey? = helperObject.getKernelJWTPublicKey()

    companion object {
        const val READ_PROGRAM_SPACE_REQUEST_CODE = 500
        const val WRITE_PROGRAM_SPACE_REQUEST_CODE = 501
    }

    fun startReadProgramSpaceIntent(
        reliantGUID: String,
        programGUID: String,
        rID: String,
        decryptData: Boolean,
        callback: (Result<ReadProgramSpaceResult>) -> Unit
    ){
        decryptProgramData = decryptData
        readProgramResultCallback = callback

        val intent = Intent(activity, ReadProgramSpaceCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.RID, rID)
        }

        activity.startActivityForResult(intent, READ_PROGRAM_SPACE_REQUEST_CODE)
    }

    fun startWriteProgramSpaceIntent(
        reliantGUID: String,
        programGUID: String,
        rID: String,
        programSpaceData: String,
        encryptData: Boolean,
        callback: (Result<WriteProgramSpaceResult>) -> Unit
    ) {
        writeProgramResultCallback = callback
        val intent = Intent(activity, WriteProgramSpaceCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RID, rID)
            putExtra(Key.PROGRAM_SPACE_DATA, programSpaceData)
            putExtra(Key.ENCRYPT_DATA, encryptData)
        }

        activity.startActivityForResult(intent, WRITE_PROGRAM_SPACE_REQUEST_CODE)
    }


    private fun parseJWT(jwt: String): String {
        try {
            val data =
                Jwts.parserBuilder().setSigningKey(kernelPublicKey).build().parseClaimsJws(jwt).body
            return data[JwtConstants.JWT_PAYLOAD].toString()
        } catch (e: SignatureException) {
            Log.e(TAG, "parseJWT: Failed to validate JWT")
        } catch (e: ExpiredJwtException) {
            Log.e(TAG, "parseJWT: JWT expired")
        } catch (e: Exception) {
            Log.e(TAG, "parseJWT: Claims passed from Kernel are empty, null or invalid")
        }
        return  ""
    }

    fun handleReadProgramSPaceIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {

        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.get(Key.DATA) as ReadProgramSpaceDataResponse
                var extractedData: String = parseJWT(response.jwt)

                if(decryptProgramData){
                    extractedData = String(cryptoService!!.decrypt(extractedData))
                }

                val result = ReadProgramSpaceResult.fromList(listOf(extractedData))
                readProgramResultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                readProgramResultCallback(Result.failure(FlutterError(code.toString(), message, null)))
            }
        }
    }

    fun handleWriteProgramSpaceIntentResponse(
        resultCode: Int,
        data: Intent?,
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response: WriteProgramSpaceDataResponse = data?.extras?.get(Key.DATA) as WriteProgramSpaceDataResponse

                val result = WriteProgramSpaceResult.fromList(listOf(response.isSuccess))
                writeProgramResultCallback(Result.success(result))
            }

            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                writeProgramResultCallback(Result.failure(FlutterError(code.toString(), message, null)))
            }
        }
    }
}
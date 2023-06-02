package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import android.util.Log
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util.*
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.model.programspace.WriteProgramSpaceDataRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


//import timber.log.Timber

class WriteProgramSpaceCompassApiHandlerActivity: CompassApiHandlerActivity<String>() {
    companion object {
        const val TAG = "WriteProgramSpaceCompassApiHandlerActivity"
    }

    override suspend fun callCompassApi() {
        val programGUID: String = intent.getStringExtra(Key.PROGRAM_GUID)!!
        val reliantGUID: String = intent.getStringExtra(Key.RELIANT_APP_GUID)!!
        val rID: String = intent.getStringExtra(Key.RID)!!
        val programSpaceData: String = intent.getStringExtra(Key.PROGRAM_SPACE_DATA)!!
        val encryptData: Boolean = intent.getBooleanExtra(Key.PROGRAM_SPACE_DATA, false)

        val sharedSpaceApi = SharedSpaceApi(
            compassKernelServiceInstance,
            reliantGUID,
            programGUID,
            DefaultCryptoService(helper),
            DefaultTokenService(
                helper.getKernelGuid()!!,
                helper.getKernelJWTPublicKey()!!,
                helper.getReliantAppJWTKeyPair().private,
                reliantGUID
            )
        )

        suspend fun performSharedSpaceKeyExchange() = withContext(Dispatchers.IO) {
            val keyPair = helper.getSharedSpaceKeyPair()
            val keyExchangeResponse =
                sharedSpaceApi.performKeyExchange(helper.getInstanceId()!!, keyPair.public)
            when (keyExchangeResponse.kernelEncPublicKey == null) {
                true -> {
                    Log.d(TAG, keyExchangeResponse.errorCode.toString())
                }
                false -> helper.saveKernelSharedSpaceKey(keyExchangeResponse.kernelEncPublicKey!!)
            }
        }

        suspend fun getSharedSpaceValidate(json: String) = withContext(Dispatchers.IO) {
            sharedSpaceApi.validateEncryptData(json)
        }

        if(encryptData){
            performSharedSpaceKeyExchange()
        }

        val response = getSharedSpaceValidate(programSpaceData)
        if (response is SharedSpaceValidationEncryptionResponse.Success) {

            // Write the Data on the Card
            val intent = compassKernelServiceInstance.getWriteProgramSpaceActivityIntent(
                WriteProgramSpaceDataRequest(
                    programGUID,
                    reliantGUID,
                    rID,
                    response.data
                )
            )

            compassApiActivityResult.launch(intent)
        } else if (response is SharedSpaceValidationEncryptionResponse.Error) {
            val errorMessage = when (response.error) {
                SharedSpaceValidationEncryptionError.ERROR_FETCHING_SCHEMA -> "Error fetching schema"
                SharedSpaceValidationEncryptionError.EMPTY_SCHEMA_CONFIG -> "Error empty schema"
                SharedSpaceValidationEncryptionError.ENCRYPTION_SERVICE_REQUIRED -> "Error encryption service required"
                SharedSpaceValidationEncryptionError.SCHEMA_PROCESSOR_ERROR -> "Error schema processor"
            }
            Log.d(TAG, errorMessage)
        }
    }
}
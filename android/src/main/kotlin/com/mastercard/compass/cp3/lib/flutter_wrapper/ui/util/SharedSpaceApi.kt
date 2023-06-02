package com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util

import com.google.gson.Gson
import com.mastercard.compass.base.ResponseStatus
import com.mastercard.compass.kernel.client.schemavalidator.SchemaData
import com.mastercard.compass.kernel.client.schemavalidator.SchemaProcessor
import com.mastercard.compass.kernel.client.schemavalidator.SchemaValidationInput
import com.mastercard.compass.kernel.client.service.KernelServiceConsumer
import com.mastercard.compass.model.KeyExchange
import com.mastercard.compass.model.KeyExchangeRequest
import com.mastercard.compass.model.KeyExchangeResponse
import com.mastercard.compass.model.programspace.DataSchemaRequest
import com.mastercard.compass.model.programspace.DataSchemaResponse
import com.mastercard.compass.model.programspace.ReadProgramSpaceDataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
//import timber.log.Timber
import java.security.PublicKey
import java.security.SignatureException

class SharedSpaceApi(
    private val kernelConnection: KernelServiceConsumer,
    private val reliantGUID: String,
    private val programGUID: String,
    private val cryptoService: DefaultCryptoService?,
    private val integrityService: DefaultTokenService
) {
    companion object {
        const val TAG = "SharedSpaceApi"
    }

    private lateinit var dataSchema: DataSchemaResponse

    fun performKeyExchange(instanceID: String, clientPublicKey: PublicKey): KeyExchangeResponse =
        kernelConnection.exchangeKeys(KeyExchangeRequest(
            reliantAppGuid = reliantGUID,
            reliantAppInstanceId = instanceID,
            clientPublicKey = clientPublicKey,
            type = KeyExchange.PROGRAM_SPACE
        ))

    suspend fun validateEncryptData(input: String): SharedSpaceValidationEncryptionResponse {
        dataSchema = fetchDataSchemaResponse()
        if(dataSchema?.responseStatus != ResponseStatus.SUCCESS){
            return SharedSpaceValidationEncryptionResponse.Error(SharedSpaceValidationEncryptionError.ERROR_FETCHING_SCHEMA, dataSchema?.message)
        }
        if(dataSchema?.schemaConfig == null){
            return SharedSpaceValidationEncryptionResponse.Error(SharedSpaceValidationEncryptionError.EMPTY_SCHEMA_CONFIG)
        }
        if(dataSchema?.schemaConfig?.isDataEncrypted == true && cryptoService == null){
            return SharedSpaceValidationEncryptionResponse.Error(SharedSpaceValidationEncryptionError.ENCRYPTION_SERVICE_REQUIRED)
        }

        val schemaValidationInput = when(dataSchema?.schemaConfig!!.isDataValidationRequired){
            true -> SchemaValidationInput(jsonSchema = dataSchema?.schemaJson, jsonInput = input)
            false -> SchemaValidationInput(jsonInput = input)
        }

        val schemaProcessor = SchemaProcessor.Builder(
            schemaConfigurations = dataSchema!!.schemaConfig,
            schemaValidationInput = schemaValidationInput,
            cryptoService = cryptoService,
            tokenService = integrityService
        ).build()

        val process: SchemaData = schemaProcessor.process()

        return when(process.isProcessSuccess){
            true -> SharedSpaceValidationEncryptionResponse.Success(process.output!!)
            false -> SharedSpaceValidationEncryptionResponse.Error(SharedSpaceValidationEncryptionError.SCHEMA_PROCESSOR_ERROR, process.errorMessage?.joinToString())
        }
    }

    suspend fun validateDecryptData(response: ReadProgramSpaceDataResponse) : SharedSpaceValidationDecryptionResponse{
        if(dataSchema == null) dataSchema = fetchDataSchemaResponse()

        try {
            if (dataSchema?.responseStatus != ResponseStatus.SUCCESS) {
                return SharedSpaceValidationDecryptionResponse.Error(
                    SharedSpaceValidationDecryptionError.ERROR_FETCHING_SCHEMA,
                    dataSchema?.message
                )
            }
            var data: String = integrityService.parseJWT(response.jwt)
            when {
                dataSchema?.schemaConfig?.isDataEncrypted == true && cryptoService == null -> {
                    return SharedSpaceValidationDecryptionResponse.Error(SharedSpaceValidationDecryptionError.ERROR_DECRYPTION_SERVICE_REQUIRED)
                }
                dataSchema?.schemaConfig?.isDataEncrypted == true -> {
                    data = String(cryptoService!!.decrypt(data))
                }
            }
            //Timber.tag(TAG).d("validateDecryptData: $data")
            val sharedSpace = Gson().fromJson(data, SharedSpace::class.java)
            return SharedSpaceValidationDecryptionResponse.Success(sharedSpace)
        } catch (e: SignatureException){
            return SharedSpaceValidationDecryptionResponse.Error(SharedSpaceValidationDecryptionError.ERROR_SIGNATURE_VALIDATION_FAILED)
        } catch (e: InvalidJWTException){
            return SharedSpaceValidationDecryptionResponse.Error(SharedSpaceValidationDecryptionError.ERROR_INVALID_JWT)
        } catch (e: Exception) {
            //Timber.tag(TAG).e(e, "validateDecryptData: process failed")
            return SharedSpaceValidationDecryptionResponse.Error(SharedSpaceValidationDecryptionError.ERROR_VALIDATION_DECRYPTION_SERIALIZATION_FAILED)
        }
    }

    private suspend fun fetchDataSchemaResponse() = withContext(Dispatchers.IO){
        kernelConnection.getDataSchema(
            DataSchemaRequest(
                programGUID,
                reliantGUID
            )
        )
    }

}

sealed class SharedSpaceValidationEncryptionResponse {
    data class Success(val data: String): SharedSpaceValidationEncryptionResponse()
    data class Error(val error: SharedSpaceValidationEncryptionError, val message: String? = null): SharedSpaceValidationEncryptionResponse()
}

enum class SharedSpaceValidationEncryptionError {
    ERROR_FETCHING_SCHEMA, EMPTY_SCHEMA_CONFIG, ENCRYPTION_SERVICE_REQUIRED, SCHEMA_PROCESSOR_ERROR
}

sealed class SharedSpaceValidationDecryptionResponse {
    data class Success(val data: SharedSpace): SharedSpaceValidationDecryptionResponse()
    data class Error(val error: SharedSpaceValidationDecryptionError, val message: String? = null): SharedSpaceValidationDecryptionResponse()
}

enum class SharedSpaceValidationDecryptionError {
    ERROR_FETCHING_SCHEMA, ERROR_DECRYPTION_SERVICE_REQUIRED, ERROR_SIGNATURE_VALIDATION_FAILED, ERROR_INVALID_JWT, ERROR_VALIDATION_DECRYPTION_SERIALIZATION_FAILED
}
package com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util


import android.util.Base64
import android.util.Log
import com.mastercard.compass.kernel.client.schemavalidator.CryptoService
import com.mastercard.compass.cp3.lib.flutter_wrapper.CompassKernelUIController
import java.lang.Exception
import java.security.InvalidKeyException
import java.security.Key
import javax.crypto.Cipher

class DefaultCryptoService(private val helper: CompassKernelUIController.CompassHelper): CryptoService {
    companion object {
        const val TAG = "DefaultCryptoService"
        const val ALGORITHM = "RSA/ECB/PKCS1Padding"
    }

    @Throws(InvalidKeyException::class, Exception::class)
    override fun encrypt(data: ByteArray): ByteArray = performCipherOperation(data, 235, Cipher.ENCRYPT_MODE, helper.getKernelSharedSpaceKey()!!)

    @Throws(InvalidKeyException::class, Exception::class)
    fun decrypt(data: String): ByteArray = performCipherOperation(decodeBase64(data), 256, Cipher.DECRYPT_MODE, helper.getSharedSpaceKeyPair().private)

    private fun performCipherOperation(data: ByteArray, blockSize: Int, mode: Int, key: Key): ByteArray {
        try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(mode, key)

            val inputLength = data.size
            var offset = 0
            var cache: ByteArray
            var resultBytes = byteArrayOf()

            while (inputLength - offset > 0) {
                if (inputLength - offset > blockSize) {
                    cache = cipher.doFinal(data, offset, blockSize)
                    offset += blockSize
                } else {
                    cache = cipher.doFinal(data, offset, inputLength - offset)
                    offset = inputLength
                }
                resultBytes += cache
            }
            return resultBytes
        } catch (e: InvalidKeyException){
            Log.e(TAG, "cipherOperations: Public Key passed does not match algorithm or parameters required for the operation", e)
            throw e
        }  catch (e: Exception){
            Log.e(TAG, "cipherOperations: Exception when encrypting value with Key", e)
            throw e
        }
    }

    private fun decodeBase64(encodedString: String): ByteArray {
        return Base64.decode(encodedString, Base64.NO_WRAP)
    }
}
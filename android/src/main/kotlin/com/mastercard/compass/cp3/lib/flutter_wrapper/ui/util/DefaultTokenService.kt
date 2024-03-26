package com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util

import android.util.Log
import com.mastercard.compass.jwt.JWTRequestModel
import com.mastercard.compass.jwt.JwtConstants
import com.mastercard.compass.kernel.client.schemavalidator.TokenService
import com.mastercard.compass.model.programspace.TokenData
import io.jsonwebtoken.Jwts
import java.lang.Exception
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SignatureException
import java.util.*

class DefaultTokenService(
    private val compassGuid: String,
    private val kernelPublicKey: PublicKey,
    private val clientPrivateKey: PrivateKey,
    private val reliantAppGuid: String
) : TokenService {
    companion object {
        const val TAG = "DefaultTokenService"
    }

    override fun signJWT(data: TokenData): String {
        val iat: Date
        val exp: Date

        Calendar.getInstance().apply {
            iat = time
            exp = this.apply { set(Calendar.MINUTE, get(Calendar.MINUTE) + 3) }.time
        }

        val jwtRequestModel = JWTRequestModel(
            payload = data,
            iss = reliantAppGuid,
            aud = compassGuid,
            iat = iat,
            jti = UUID.randomUUID().toString(),
            exp = exp
        )

        return jwtRequestModel.compact(clientPrivateKey)
    }

    @Throws(SignatureException::class, InvalidJWTException::class)
    fun parseJWT(jwt: String): String {
        try {
            val data =
                Jwts.parserBuilder().setSigningKey(kernelPublicKey).build().parseClaimsJws(jwt).body

            return data[JwtConstants.JWT_PAYLOAD].toString()
        } catch (e: SignatureException){
            Log.e(TAG, "parseJWT: Failed to validate JWT", e)
            throw e
        } catch (e: Exception){
            Log.e(TAG, "parseJWT: Claims passed from Kernel are empty, null or invalid or the JWT is expired", e)
            throw InvalidJWTException(e.message, e)
        }
    }
}

class InvalidJWTException(message: String?, throwable: Throwable): Exception(message, throwable)
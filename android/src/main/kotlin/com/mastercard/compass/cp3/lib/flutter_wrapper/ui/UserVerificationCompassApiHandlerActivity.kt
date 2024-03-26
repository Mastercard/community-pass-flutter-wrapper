package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import android.util.Log
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.populateModalityList
import java.util.Base64
import com.mastercard.compass.model.biometrictoken.FormFactor

class UserVerificationCompassApiHandlerActivity: CompassApiHandlerActivity<String>() {
    override suspend fun callCompassApi() {
        val reliantGUID: String = intent.getStringExtra(Key.RELIANT_APP_GUID)!!
        val programGUID: String = intent.getStringExtra(Key.PROGRAM_GUID)!!
        val formFactor: String = intent.getStringExtra(Key.FORM_FACTOR)!!
        val qrBase64: String? = intent.getStringExtra(Key.QR)
        val modalities: ArrayList<String> = intent.getStringArrayListExtra(Key.MODALITIES)!!

        val requestJwt = if (formFactor == Key.QR) {
            helper.generateJWT(
                reliantGUID,
                programGUID,
                populateModalityList(modalities),
                FormFactor.QR,
                Base64.getDecoder().decode(qrBase64)
            )
        } else {
            helper.generateJWT(
                reliantGUID,
                programGUID,
                populateModalityList(modalities)
            )
        }


        val intent = compassKernelServiceInstance.getUserVerificationActivityIntent(
            requestJwt,
            reliantGUID
        )

        compassApiActivityResult.launch(intent)
    }
}
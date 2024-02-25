package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import android.util.Log
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.getFormFactor
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.populateModalityList
import com.mastercard.compass.model.biometrictoken.FormFactor
import java.util.Base64

class UserIdentificationCompassApiHandlerActivity: CompassApiHandlerActivity<String>()  {
    override suspend fun callCompassApi() {
        val reliantGUID: String = intent.getStringExtra(Key.RELIANT_APP_GUID)!!
        val programGUID: String = intent.getStringExtra(Key.PROGRAM_GUID)!!
        val modalities: ArrayList<String> = intent.getStringArrayListExtra(Key.MODALITIES)!!
        val cacheHashesIfIdentified: Boolean = intent.getBooleanExtra(Key.CACHE_HASHES_IF_IDENTIFIED, true)
        val formFactor: String = intent.getStringExtra(Key.FORM_FACTOR)!!
        val qrBase64: String? = intent.getStringExtra(Key.QR)

        Log.d("TEST_TEST","$modalities")
        Log.d("TEST_TEST","$formFactor")
        Log.d("TEST_TEST","$cacheHashesIfIdentified")
        Log.d("TEST_TEST","$qrBase64")

        val requestJwt = if (formFactor == Key.QR) {
            helper.generateJWT(
                reliantAppGUID = reliantGUID,
                programGuid = programGUID,
                modalities = populateModalityList(modalities),
                formFactor = getFormFactor(formFactor),
                mwqr = Base64.getDecoder().decode(qrBase64)
            )
        } else {
            helper.generateJWT(
                reliantAppGUID = reliantGUID,
                programGuid = programGUID,
                modalities = populateModalityList(modalities),
                formFactor = getFormFactor(formFactor)
            )
        }

        val intent = compassKernelServiceInstance.getUserIdentificationActivityIntent(
            reliantAppGuid = reliantGUID,
            jwt = requestJwt,
            cacheHashesIfIdentified = cacheHashesIfIdentified
        )

        compassApiActivityResult.launch(intent)
    }
}
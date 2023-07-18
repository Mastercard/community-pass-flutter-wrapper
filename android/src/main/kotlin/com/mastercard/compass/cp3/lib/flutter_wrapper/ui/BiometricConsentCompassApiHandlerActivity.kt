package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import com.mastercard.compass.base.ConsentValue
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.model.consent.Consent
import com.mastercard.compass.model.consent.ConsentResponse

class BiometricConsentCompassApiHandlerActivity: CompassApiHandlerActivity<ConsentResponse>() {
    override suspend fun callCompassApi() {
        val programGUID: String = intent.getStringExtra(Key.PROGRAM_GUID)!!
        val consumerConsentValue = intent.getBooleanExtra(Key.CONSUMER_CONSENT_VALUE, false)

        val consent =  if(consumerConsentValue) {
            Consent(ConsentValue.ACCEPT, programGUID)
        } else {
            Consent(ConsentValue.DECLINE, programGUID)
        }

        val response = compassKernelServiceInstance.saveBiometricConsent(consent)

        getNonIntentCompassApiResults(response)
    }
}
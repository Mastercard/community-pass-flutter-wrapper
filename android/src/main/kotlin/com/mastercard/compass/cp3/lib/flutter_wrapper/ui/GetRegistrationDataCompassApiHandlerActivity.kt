package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.model.card.RegistrationStatusData

class GetRegistrationDataCompassApiHandlerActivity: CompassApiHandlerActivity<RegistrationStatusData>() {
    override suspend fun callCompassApi() {
        val reliantGUID: String = intent.getStringExtra(Key.RELIANT_APP_GUID)!!
        val programGUID: String = intent.getStringExtra(Key.PROGRAM_GUID)!!

        val intent = compassKernelServiceInstance.getRegistrationDataActivityIntent(
            programGUID,
            reliantGUID
        )

        compassApiActivityResult.launch(intent)
    }
}
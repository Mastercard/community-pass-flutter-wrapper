package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.getFormFactor
import com.mastercard.compass.model.passcode.RegisterBasicUserRequestV2

class RegisterBasicUserCompassApiHandlerActivity: CompassApiHandlerActivity<String>() {
    override suspend fun callCompassApi() {
        val programGUID: String = intent.getStringExtra(Key.PROGRAM_GUID)!!
        val formFactor: String = intent.getStringExtra(Key.FORM_FACTOR)!!

        val intent = compassKernelServiceInstance.getRegisterBasicUserActivityIntent(
            RegisterBasicUserRequestV2(programGUID, getFormFactor(formFactor))
        )

        compassApiActivityResult.launch(intent)
    }
}
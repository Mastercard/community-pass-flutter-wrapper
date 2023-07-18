package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.model.biometrictoken.FormFactor
import com.mastercard.compass.model.blacklist.BlacklistFormFactorRequest

class BlacklistFormFactorApiHandlerActivity: CompassApiHandlerActivity<String>() {

    private lateinit var formFactor: FormFactor

    override suspend fun callCompassApi() {
        val reliantGUID: String = intent.getStringExtra(Key.RELIANT_APP_GUID)!!
        val programGUID: String = intent.getStringExtra(Key.PROGRAM_GUID)!!
        val rId: String = intent.getStringExtra(Key.RID)!!
        val consumerDeviceNumber: String = intent.getStringExtra(Key.CONSUMER_DEVICE_NUMBER)!!
        when (intent.getStringExtra(Key.FORM_FACTOR)!!){
            "CARD" ->  {
                formFactor = FormFactor.CARD
            }
            "QR" ->  {
                formFactor = FormFactor.QR
            }
            "NONE" ->  {
                formFactor = FormFactor.NONE
            }
        }
        val intent = compassKernelServiceInstance.getBlacklistFormFactorActivityIntent(
            BlacklistFormFactorRequest(
                programGUID,
                rId,
                reliantGUID,
                consumerDeviceNumber,
                formFactor
            )
        )
        compassApiActivityResult.launch(intent)
    }
}
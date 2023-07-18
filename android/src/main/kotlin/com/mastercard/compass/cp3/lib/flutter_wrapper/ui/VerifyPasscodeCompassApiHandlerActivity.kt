package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.getFormFactor
import com.mastercard.compass.model.biometrictoken.FormFactor
import com.mastercard.compass.model.card.VerifyPasscodeRequest
import java.util.Base64

class VerifyPasscodeCompassApiHandlerActivity: CompassApiHandlerActivity<String>()  {
    override suspend fun callCompassApi() {
        val programGUID: String = intent.getStringExtra(Key.PROGRAM_GUID)!!
        val rID: String = intent.getStringExtra(Key.RID)!!
        val passcode: String = intent.getStringExtra(Key.PASSCODE)!!
        val formFactor: String = intent.getStringExtra(Key.FORM_FACTOR)!!
        val qrBase64: String? = intent.getStringExtra(Key.QR)

        val request = if(getFormFactor(formFactor)  == FormFactor.QR) {
            VerifyPasscodeRequest(
                passcode,
                programGUID,
                FormFactor.QR,
                Base64.getDecoder().decode(qrBase64)
            )
        } else {
            VerifyPasscodeRequest(
                passcode,
                programGUID
            )
        }

        val intent = compassKernelServiceInstance.getVerifyPasscodeActivityIntent(request)
        compassApiActivityResult.launch(intent)
    }
}
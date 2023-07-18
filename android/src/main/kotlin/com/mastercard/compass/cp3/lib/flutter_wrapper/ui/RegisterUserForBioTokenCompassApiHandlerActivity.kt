package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import com.mastercard.compass.base.OperationMode
import com.mastercard.compass.model.biometrictoken.Modality
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key

class RegisterUserForBioTokenCompassApiHandlerActivity: CompassApiHandlerActivity<String>() {
    override suspend fun callCompassApi() {
        val reliantAppGuid: String = intent.getStringExtra(Key.RELIANT_APP_GUID)!!
        val programGUID: String = intent.getStringExtra(Key.PROGRAM_GUID)!!
        val consentId: String = intent.getStringExtra(Key.CONSENT_ID)!!
        val modalities: ArrayList<String> = intent.getStringArrayListExtra(Key.MODALITIES)!!
        val operationMode: String = intent.getStringExtra(Key.OPERATION_MODE)!!

        val listOfModalities = mutableListOf<Modality>().apply {
            if(modalities.contains(Key.FACE)) add(Modality.FACE)
            if(modalities.contains(Key.LEFT_PALM)) add(Modality.LEFT_PALM)
            if(modalities.contains(Key.RIGHT_PALM)) add(Modality.RIGHT_PALM)
        }

        val jwt = helper.generateBioTokenJWT(
            reliantAppGuid, programGUID, consentId, listOfModalities)

        val intent = compassKernelServiceInstance.getRegisterUserForBioTokenActivityIntent(
            jwt,
            reliantAppGuid,
            if(operationMode == Key.BEST_AVAILABLE) OperationMode.BEST_AVAILABLE else OperationMode.FULL
        )

        compassApiActivityResult.launch(intent)
    }
}

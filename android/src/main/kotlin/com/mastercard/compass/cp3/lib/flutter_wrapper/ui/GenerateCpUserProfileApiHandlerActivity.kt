package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.model.cpuserprofile.GenerateCpUserProfileRequest

class GenerateCpUserProfileApiHandlerActivity: CompassApiHandlerActivity<String>()  {
    override suspend fun callCompassApi() {
        val programGUID: String = intent.getStringExtra(Key.PROGRAM_GUID)!!
        val rID: String = intent.getStringExtra(Key.RID)!!
        val passcode: String? = intent.getStringExtra(Key.PASSCODE)

        val intent = compassKernelServiceInstance.getGenerateCpUserProfileActivityIntent(
            cpUserProfileRequest = GenerateCpUserProfileRequest(rID, programGUID, passcode)
        )

        compassApiActivityResult.launch(intent)
    }
}
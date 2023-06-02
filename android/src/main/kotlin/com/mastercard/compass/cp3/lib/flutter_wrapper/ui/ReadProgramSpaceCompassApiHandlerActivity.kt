package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.model.programspace.ReadProgramSpaceDataResponse

class ReadProgramSpaceCompassApiHandlerActivity: CompassApiHandlerActivity<ReadProgramSpaceDataResponse>() {

    companion object {
        const val TAG = "ReadProgramSpaceCompassApiHandlerActivity"
    }

    override suspend fun callCompassApi() {
        val programGUID: String = intent.getStringExtra(Key.PROGRAM_GUID)!!
        val reliantGUID: String = intent.getStringExtra(Key.RELIANT_APP_GUID)!!
        val rID: String = intent.getStringExtra(Key.RID)!!

        val intent = compassKernelServiceInstance.getReadProgramSpaceActivityIntent(programGUID, rID, reliantGUID)

        compassApiActivityResult.launch(intent)
    }
}


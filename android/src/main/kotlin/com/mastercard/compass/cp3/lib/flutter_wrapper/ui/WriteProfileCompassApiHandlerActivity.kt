package com.mastercard.compass.cp3.lib.flutter_wrapper.ui

import CompassApiHandlerActivity
import com.mastercard.compass.base.Constants
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.OVERWRITE_CARD
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.PROGRAM_GUID
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key.RID

class WriteProfileCompassApiHandlerActivity : CompassApiHandlerActivity<String>() {
    override suspend fun callCompassApi() {
        val programGUID: String = intent.getStringExtra(PROGRAM_GUID)!!
        val rId: String = intent.getStringExtra(RID)!!
        val overwriteCard: Boolean = intent.getBooleanExtra(OVERWRITE_CARD, false)

        val intent = compassKernelServiceInstance.getWriteProfileActivityIntent(
            programGUID, rId
        )

        intent?.putExtra(Constants.EXTRA_OVERWRITE_CARD, overwriteCard)
        compassApiActivityResult.launch(intent)
    }
}
//package com.mastercard.compass.cp3.lib.flutter_wrapper.route
//
//import android.app.Activity
//import android.content.Intent
//import com.mastercard.compass.cp3.lib.flutter_wrapper.CompassApiFlutter
//import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.WriteProfileCompassApiHandlerActivity
//import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
//
//class GetRegistrationDataAPIRoute(private val activity: Activity) {
//    private lateinit var resultCallback: (Result<CompassApiFlutter.RegistrationDataResult>) -> Unit
//
//companion object {
//    val REQUEST_CODE_RANGE = 800 until 900
//    const val GET_REGISTRATION_DATA_REQUEST_CODE = 800
//}
//
//fun startGetRegistrationDataProfileIntent(reliantGUID: String, programGUID: String, result: (Result<CompassApiFlutter.RegistrationDataResult>) -> Unit){
//    val intent = Intent(activity, WriteProfileCompassApiHandlerActivity::class.java).apply {
//        putExtra(Key.RELIANT_APP_GUID, reliantGUID)
//        putExtra(Key.PROGRAM_GUID, programGUID)
//    }
//
//    resultCallback = result
//    activity.startActivityForResult(intent, GET_REGISTRATION_DATA_REQUEST_CODE)
//}
//
//fun handleGetRegistrationDataIntentResponse(
//    resultCode: Int,
//    data: Intent?,
//) {
//    when (resultCode) {
//        Activity.RESULT_OK -> {
//
////            val a: VerifyBioTokenResponse
////
////            val result = CompassApiFlutter.WriteProfileResult.Builder()
////                .setConsumerDeviceNumber(data?.extras?.get(Key.DATA).toString())
////                .build()
////            getRegistrationDataResult.success(result)
//        }
//        Activity.RESULT_CANCELED -> {
//            val code = data?.getIntExtra(Key.ERROR_CODE, 0)
//            val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
////            getRegistrationDataResult.error(CompassThrowable(code, message))
//        }
//    }
//}
//}
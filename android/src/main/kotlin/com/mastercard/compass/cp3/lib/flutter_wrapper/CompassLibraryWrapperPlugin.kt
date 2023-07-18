package com.mastercard.compass.cp3.lib.flutter_wrapper

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.mastercard.compass.cp3.lib.flutter_wrapper.route.*
import com.mastercard.compass.kernel.client.service.KernelServiceConsumer
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry

class CompassLibraryWrapperPlugin: FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener, CommunityPassApi {
  private lateinit var context: Context
  private lateinit var activity: Activity
  private lateinit var kernelServiceConsumer: KernelServiceConsumer

  private val consumerDeviceApiRoute: ConsumerDeviceAPIRoute by lazy {
    ConsumerDeviceAPIRoute(activity)
  }
  private val registerUserWithBiometricsAPIRoute: RegisterUserWithBiometricsAPIRoute by lazy {
    RegisterUserWithBiometricsAPIRoute(activity)
  }
  private val registerBasicUserAPIRoute: RegisterBasicUserAPIRoute by lazy {
    RegisterBasicUserAPIRoute(activity)
  }
  private val consumerDevicePasscodeAPIRoute: ConsumerDevicePasscodeAPIRoute by lazy {
    ConsumerDevicePasscodeAPIRoute(activity)
  }
  private val biometricConsentAPIRoute: BiometricConsentAPIRoute by lazy {
    BiometricConsentAPIRoute(activity)
  }

  private lateinit var helperObject: CompassKernelUIController.CompassHelper

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    CommunityPassApi.setUp(binding.binaryMessenger, this)
    context = binding.applicationContext
    helperObject = CompassKernelUIController.CompassHelper(context);
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    CommunityPassApi.setUp(binding.binaryMessenger, null)
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    TODO("Not yet implemented")
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    when(requestCode){
      in BiometricConsentAPIRoute.REQUEST_CODE_RANGE -> handleApiRouteResponse(requestCode, resultCode, data)
      in ConsumerDeviceAPIRoute.REQUEST_CODE_RANGE -> handleApiRouteResponse(requestCode, resultCode, data)
      in ConsumerDevicePasscodeAPIRoute.REQUEST_CODE_RANGE -> handleApiRouteResponse(requestCode, resultCode, data)
      in RegisterUserWithBiometricsAPIRoute.REQUEST_CODE_RANGE -> handleApiRouteResponse(requestCode, resultCode, data)
      in RegisterBasicUserAPIRoute.REQUEST_CODE_RANGE -> handleApiRouteResponse(requestCode, resultCode, data)
    }
    return true;
  }

  override fun saveBiometricConsent(
    reliantGUID: String,
    programGUID: String,
    consumerConsentValue: Boolean,
    callback: (Result<SaveBiometricConsentResult>) -> Unit
  ) {
    biometricConsentAPIRoute.startBiometricConsentIntent(reliantGUID, programGUID, consumerConsentValue, callback);
  }

  override fun getRegisterUserWithBiometrics(
    reliantGUID: String,
    programGUID: String,
    consentID: String,
    modalities: List<String>,
    operationMode: OperationMode,
    callback: (Result<RegisterUserWithBiometricsResult>) -> Unit
  ) {
    registerUserWithBiometricsAPIRoute.startRegisterUserWithBiometricsIntent(reliantGUID, programGUID, consentID, modalities, operationMode, callback)
  }

  override fun getRegisterBasicUser(
    reliantGUID: String,
    programGUID: String,
    callback: (Result<RegisterBasicUserResult>) -> Unit
  ) {
    registerBasicUserAPIRoute.startRegisterBasicUserIntent(reliantGUID, programGUID, callback)
  }

  override fun getWriteProfile(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    overwriteCard: Boolean,
    callback: (Result<WriteProfileResult>) -> Unit
  ) {
    consumerDeviceApiRoute.startWriteProfileIntent(reliantGUID, programGUID, rID, overwriteCard, callback)
  }

  override fun getWritePasscode(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    passcode: String,
    callback: (Result<WritePasscodeResult>) -> Unit
  ) {
    consumerDevicePasscodeAPIRoute.startWritePasscodeIntent(reliantGUID, programGUID, rID, passcode, callback)
  }

  override fun getVerifyPasscode(
    reliantGUID: String,
    programGUID: String,
    passcode: String,
    formFactor: FormFactor,
    qrCpUserProfile: String?,
    callback: (Result<VerifyPasscodeResult>) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun getUserVerification(
    reliantGUID: String,
    programGUID: String,
    token: String,
    modalities: List<String>,
    callback: (Result<UserVerificationResult>) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun getRegistrationData(
    reliantGUID: String,
    programGUID: String,
    callback: (Result<RegistrationDataResult>) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun getWriteProgramSpace(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    programSpaceData: String,
    encryptData: Boolean,
    callback: (Result<WriteProgramSpaceResult>) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun getReadProgramSpace(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    decryptData: Boolean,
    callback: (Result<ReadProgramSpaceResult>) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun getBlacklistFormFactor(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    consumerDeviceNumber: String,
    type: FormFactor,
    callback: (Result<BlacklistFormFactorResult>) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun getReadSVA(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    svaUnit: String,
    callback: (Result<ReadSVAResult>) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun getCreateSVA(
    reliantGUID: String,
    programGUID: String,
    rID: String?,
    sva: SVA,
    callback: (Result<CreateSVAResult>) -> Unit
  ) {
    TODO("Not yet implemented")
  }

  override fun getGenerateCpUserProfile(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    passcode: String?,
    callback: (Result<GenerateCpUserProfileResult>) -> Unit
  ) {
    TODO("Not yet implemented")
  }


  private fun handleApiRouteResponse(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    when (requestCode) {
      BiometricConsentAPIRoute.BIOMETRIC_CONSENT_REQUEST_CODE -> biometricConsentAPIRoute.handleBiometricConsentIntentResponse(resultCode, data)
      ConsumerDeviceAPIRoute.WRITE_PROFILE_REQUEST_CODE -> consumerDeviceApiRoute.handleWriteProfileIntentResponse(resultCode, data)
      ConsumerDevicePasscodeAPIRoute.WRITE_PASSCODE_REQUEST_CODE -> consumerDevicePasscodeAPIRoute.handleWritePasscodeIntentResponse(resultCode, data)
      RegisterUserWithBiometricsAPIRoute.REGISTER_BIOMETRICS_REQUEST_CODE -> registerUserWithBiometricsAPIRoute.handleRegisterUserWithBiometricsIntentResponse(resultCode, data, helperObject)
      RegisterBasicUserAPIRoute.REGISTER_BASIC_USER_REQUEST_CODE -> registerBasicUserAPIRoute.handleRegisterBasicUserIntentResponse(resultCode, data,)
    }
  }
}

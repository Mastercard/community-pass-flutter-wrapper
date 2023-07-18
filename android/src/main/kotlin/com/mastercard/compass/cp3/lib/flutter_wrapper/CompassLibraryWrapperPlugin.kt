package com.mastercard.compass.cp3.lib.flutter_wrapper

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.mastercard.compass.cp3.lib.flutter_wrapper.route.AuthenticationAPIRoute
import com.mastercard.compass.cp3.lib.flutter_wrapper.route.ConsumerDeviceAPIRoute
import com.mastercard.compass.cp3.lib.flutter_wrapper.route.ProgramSpaceAPIRoute
import com.mastercard.compass.cp3.lib.flutter_wrapper.route.SVAOperationAPIRoute
import com.mastercard.compass.cp3.lib.flutter_wrapper.route.UserRegistrationAPIRoute
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util.DefaultCryptoService
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry

class CompassLibraryWrapperPlugin: FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener, CommunityPassApi {
  private lateinit var context: Context
  private lateinit var activity: Activity
  private lateinit var defaultCryptoService: DefaultCryptoService

  private val consumerDeviceApiRoute: ConsumerDeviceAPIRoute by lazy {
    ConsumerDeviceAPIRoute(activity)
  }
  private val userRegistrationAPIRoute: UserRegistrationAPIRoute by lazy {
    UserRegistrationAPIRoute(activity, helperObject)
  }
  private val authenticationAPIRoute: AuthenticationAPIRoute by lazy {
    AuthenticationAPIRoute(activity, helperObject)
  }

  private val programSpaceAPIRoute: ProgramSpaceAPIRoute by lazy {
    ProgramSpaceAPIRoute(activity, helperObject, defaultCryptoService)
  }

  private val svaOperationAPIRoute: SVAOperationAPIRoute by lazy {
    SVAOperationAPIRoute(activity)
  }


  private lateinit var helperObject: CompassKernelUIController.CompassHelper

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    CommunityPassApi.setUp(binding.binaryMessenger, this)
    context = binding.applicationContext
    helperObject = CompassKernelUIController.CompassHelper(context);
    defaultCryptoService = DefaultCryptoService(helperObject)
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
      UserRegistrationAPIRoute.SAVE_BIOMETRIC_CONSENT_REQUEST_CODE -> userRegistrationAPIRoute.handleSaveBiometricConsentResponse(resultCode, data)
      UserRegistrationAPIRoute.BIOMETRIC_USER_REGISTRATION_REQUEST_CODE -> userRegistrationAPIRoute.handleBiometricRegistrationResponse(resultCode, data)
      UserRegistrationAPIRoute.BASIC_USER_REGISTRATION_REQUEST_CODE -> userRegistrationAPIRoute.handleRegisterBasicUserResponse(resultCode, data)
      UserRegistrationAPIRoute.GENERATE_CP_USER_PROFILE_REQUEST_CODE -> userRegistrationAPIRoute.handleGenerateCpUserProfileResponse(resultCode, data)
      ConsumerDeviceAPIRoute.WRITE_PROFILE_REQUEST_CODE -> consumerDeviceApiRoute.handleWriteProfileIntentResponse(resultCode, data)
      ConsumerDeviceAPIRoute.WRITE_PASSCODE_REQUEST_CODE -> consumerDeviceApiRoute.handleWritePasscodeIntentResponse(resultCode, data)
      ConsumerDeviceAPIRoute.BLACKLIST_FORM_FACTOR_REQUEST_CODE -> consumerDeviceApiRoute.handleBlacklistFormFactorIntentResponse(resultCode, data)
      AuthenticationAPIRoute.REGISTRATION_DATA_REQUEST_CODE -> authenticationAPIRoute.handleGetRegistrationDataIntentResponse(resultCode, data)
      AuthenticationAPIRoute.VERIFY_PASSCODE_REQUEST_CODE -> authenticationAPIRoute.handleVerifyPasscodeDataResponse(resultCode, data)
      AuthenticationAPIRoute.USER_VERIFICATION_REQUEST_CODE -> authenticationAPIRoute.handleUserVerificationResponse(resultCode, data)
      ProgramSpaceAPIRoute.READ_PROGRAM_SPACE_REQUEST_CODE -> programSpaceAPIRoute.handleReadProgramSPaceIntentResponse(resultCode, data)
      ProgramSpaceAPIRoute.WRITE_PROGRAM_SPACE_REQUEST_CODE -> programSpaceAPIRoute.handleWriteProgramSpaceIntentResponse(resultCode, data)
      SVAOperationAPIRoute.CREATE_SVA_REQUEST_CODE -> svaOperationAPIRoute.handleCreateSvaIntentResponse(resultCode, data)
      SVAOperationAPIRoute.READ_SVA_REQUEST_CODE -> svaOperationAPIRoute.handleReadSvaIntentResponse(resultCode, data)
    }
    return true;
  }

  override fun saveBiometricConsent(
    reliantGUID: String,
    programGUID: String,
    consumerConsentValue: Boolean,
    callback: (Result<SaveBiometricConsentResult>) -> Unit
  ) {
    userRegistrationAPIRoute.startSaveBiometricConsentIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      consumerConsentValue = consumerConsentValue,
      callback = callback
    )
  }

  override fun getRegisterUserWithBiometrics(
    reliantGUID: String,
    programGUID: String,
    consentID: String,
    modalities: List<String>,
    operationMode: OperationMode,
    callback: (Result<RegisterUserWithBiometricsResult>) -> Unit
  ) {
    userRegistrationAPIRoute.startBiometricRegistrationIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      consentID = consentID,
      modalities = modalities,
      operationMode = operationMode,
      callback = callback
    )
  }

  override fun getRegisterBasicUser(
    reliantGUID: String,
    programGUID: String,
    formFactor: String,
    callback: (Result<RegisterBasicUserResult>) -> Unit
  ) {
    userRegistrationAPIRoute.startRegisterBasicUserIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      formFactor = formFactor,
      callback = callback)
  }

  override fun getWriteProfile(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    overwriteCard: Boolean,
    callback: (Result<WriteProfileResult>) -> Unit
  ) {
    consumerDeviceApiRoute.startWriteProfileIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      rID = rID,
      overwriteCard = overwriteCard,
      callback = callback)
  }

  override fun getWritePasscode(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    passcode: String,
    callback: (Result<WritePasscodeResult>) -> Unit
  ) {
    consumerDeviceApiRoute.startWritePasscodeIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      rID = rID,
      passcode = passcode,
      callback = callback)
  }

  override fun getVerifyPasscode(
    reliantGUID: String,
    programGUID: String,
    passcode: String,
    formFactor: FormFactor,
    qrCpUserProfile: String?,
    callback: (Result<VerifyPasscodeResult>) -> Unit
  ) {
    authenticationAPIRoute.startVerifyPasscodeIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      passcode = passcode,
      formFactor = formFactor,
      qrCpUserProfile = qrCpUserProfile,
      callback = callback
    )
  }

  override fun getUserVerification(
    reliantGUID: String,
    programGUID: String,
    formFactor: String,
    qrBase64: String?,
    modalities: List<String>,
    callback: (Result<UserVerificationResult>) -> Unit
  ) {
    authenticationAPIRoute.startUserVerificationIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      modalities = modalities,
      formFactor = formFactor,
      qrBase64 = qrBase64,
      callback = callback
    )
  }

  override fun getRegistrationData(
    reliantGUID: String,
    programGUID: String,
    callback: (Result<RegistrationDataResult>) -> Unit
  ) {
    authenticationAPIRoute.startGetRegistrationDataIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      callback = callback
    )
  }

  override fun getWriteProgramSpace(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    programSpaceData: String,
    encryptData: Boolean,
    callback: (Result<WriteProgramSpaceResult>) -> Unit
  ) {
    programSpaceAPIRoute.startWriteProgramSpaceIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      rID = rID,
      encryptData = encryptData,
      programSpaceData = programSpaceData,
      callback = callback
    )
  }

  override fun getReadProgramSpace(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    decryptData: Boolean,
    callback: (Result<ReadProgramSpaceResult>) -> Unit
  ) {
    programSpaceAPIRoute.startReadProgramSpaceIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      rID = rID,
      decryptData = decryptData,
      callback = callback
    )
  }

  override fun getBlacklistFormFactor(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    consumerDeviceNumber: String,
    type: FormFactor,
    callback: (Result<BlacklistFormFactorResult>) -> Unit
  ) {
    consumerDeviceApiRoute.startBlacklistFormFactorIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      rID = rID,
      type = type,
      consumerDeviceNumber = consumerDeviceNumber,
      callback = callback
    )
  }

  override fun getReadSVA(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    svaUnit: String,
    callback: (Result<ReadSVAResult>) -> Unit
  ) {
    svaOperationAPIRoute.startReadSvaIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      rID = rID,
      svaUnit = svaUnit,
      callback = callback
    )
  }

  override fun getCreateSVA(
    reliantGUID: String,
    programGUID: String,
    rID: String?,
    sva: SVA,
    callback: (Result<CreateSVAResult>) -> Unit
  ) {
    svaOperationAPIRoute.startCreateSvaIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      rID = rID,
      sva = sva,
      callback = callback
    )
  }

  override fun getGenerateCpUserProfile(
    reliantGUID: String,
    programGUID: String,
    rID: String,
    passcode: String?,
    callback: (Result<GenerateCpUserProfileResult>) -> Unit
  ) {
    userRegistrationAPIRoute.startGenerateCpUserProfileIntent(
      reliantGUID = reliantGUID,
      programGUID = programGUID,
      rID = rID,
      passcode = passcode,
      callback = callback
    )
  }
}

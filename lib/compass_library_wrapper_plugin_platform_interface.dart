import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'compass_library_wrapper_plugin_method_channel.dart';

abstract class CompassLibraryWrapperPluginPlatform extends PlatformInterface {
  /// Constructs a CompassLibraryWrapperPluginPlatform.
  CompassLibraryWrapperPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static CompassLibraryWrapperPluginPlatform _instance =
      PigeonCompassLibraryWrapperPlugin();

  /// The default instance of [CompassLibraryWrapperPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelCompassLibraryWrapperPlugin].
  static CompassLibraryWrapperPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [CompassLibraryWrapperPluginPlatform] when
  /// they register themselves.
  static set instance(CompassLibraryWrapperPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<SaveBiometricConsentResult> saveBiometricConsent(
      String reliantGUID, String programGUID, bool consumerConsentValue) async {
    throw UnimplementedError(
        'saveBiometricConsent() has not been implemented.');
  }

  Future<CommunityPassConsentScreenResult> communityPassConsentWithPreBuiltUI(
      String reliantGUID,
      String programGUID,
      ConsentScreenConfig consentScreenConfig) async {
    throw UnimplementedError(
        'communityPassConsentWithPreBuiltUI() has not been implemented.');
  }

  Future<RegisterUserWithBiometricsResult> getRegisterUserWithBiometrics(
      String reliantGUID,
      String programGUID,
      String consentID,
      List<String> modalities,
      OperationMode operationMode) async {
    throw UnimplementedError(
        'getRegisterUserWithBiometrics() has not been implemented.');
  }

  Future<RegisterBasicUserResult> getRegisterBasicUser(
      String reliantGUID, String programGUID, String formFactor) async {
    throw UnimplementedError(
        'getRegisterBasicUser() has not been implemented.');
  }

  Future<WriteProfileResult> getWriteProfile(String reliantGUID,
      String programGUID, String rID, bool overwriteCard) async {
    throw UnimplementedError('getWriteProfile() has not been implemented.');
  }

  Future<WritePasscodeResult> getWritePasscode(String reliantGUID,
      String programGUID, String rID, String passcode) async {
    throw UnimplementedError('getWritePasscode() has not been implemented.');
  }

  Future<VerifyPasscodeResult> getVerifyPasscode(
      String reliantGUID,
      String programGUID,
      String passcode,
      FormFactor formFactor,
      String? qrCpUserProfile) async {
    throw UnimplementedError('getVerifyPasscode() has not been implemented.');
  }

  Future<UserVerificationResult> getUserVerification(
      String reliantGUID,
      String programGUID,
      FormFactor formFactor,
      String? qrBase64,
      List<String> modalities) async {
    throw UnimplementedError('getUserVerification() has not been implemented.');
  }

  Future<RegistrationDataResult> getRegistrationData(
      String reliantGUID, String programGUID) async {
    throw UnimplementedError('getRegistrationData() has not been implemented.');
  }

  Future<WriteProgramSpaceResult> getWriteProgramSpace(
      String reliantGUID,
      String programGUID,
      String rID,
      String programSpaceData,
      bool encryptData) async {
    throw UnimplementedError(
        'getWriteProgramSpace() has not been implemented.');
  }

  Future<ReadProgramSpaceResult> getReadProgramSpace(
    String reliantGUID,
    String programGUID,
    String rID,
    bool decryptData,
  ) async {
    throw UnimplementedError('getReadProgramSpace() has not been implemented.');
  }

  Future<BlacklistFormFactorResult> getBlacklistFormFactor(
      String reliantGUID,
      String programGUID,
      String rID,
      String consumerDeviceNumber,
      FormFactor type) async {
    throw UnimplementedError(
        'getBlacklistFormFactor() has not been implemented.');
  }

  Future<ReadSVAResult> getReadSVA(String reliantGUID, String programGUID,
      String rID, String svaUnit) async {
    throw UnimplementedError('getReadSVA() has not been implemented.');
  }

  Future<CreateSVAResult> getCreateSVA(
      String reliantGUID, String programGUID, String? rID, SVA sva) async {
    throw UnimplementedError('getCreateSVA() has not been implemented.');
  }

  Future<UserIdentificationResult> getUserIdentification(
    String reliantGUID,
    String programGUID,
    List<String> modalities,
    bool cacheHashesIfIDentified,
    String? qrBase64,
    FormFactor formFactor,
  ) async {
    throw UnimplementedError(
        'getUserIdentification() has not been implemented.');
  }

  Future<GenerateCpUserProfileResult> getGenerateCpUserProfile(
      String reliantGUID,
      String programGUID,
      String rID,
      String? passcode) async {
    throw UnimplementedError(
        'getGenerateCpUserProfile() has not been implemented.');
  }
}

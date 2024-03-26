import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';

import 'compass_library_wrapper_plugin_platform_interface.dart';

/// An implementation of [CompassLibraryWrapperPluginPlatform] that uses method channels.
class PigeonCompassLibraryWrapperPlugin
    extends CompassLibraryWrapperPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('compass_library_wrapper_plugin');
  final CommunityPassApi _api = CommunityPassApi();

  @override
  Future<SaveBiometricConsentResult> saveBiometricConsent(
      String reliantGUID, String programGUID, bool consumerConsentValue) async {
    return _api.saveBiometricConsent(
        reliantGUID, programGUID, consumerConsentValue);
  }

  @override
  Future<CommunityPassConsentScreenResult> communityPassConsentWithPreBuiltUI(
      String reliantGUID,
      String programGUID,
      ConsentScreenConfig consentScreenConfig) async {
    return _api.communityPassConsentWithPreBuiltUI(
        reliantGUID, programGUID, consentScreenConfig);
  }

  @override
  Future<RegisterUserWithBiometricsResult> getRegisterUserWithBiometrics(
      String reliantGUID,
      String programGUID,
      String consentID,
      List<String> modalities,
      OperationMode operationMode) async {
    return _api.getRegisterUserWithBiometrics(
        reliantGUID, programGUID, consentID, modalities, operationMode);
  }

  @override
  Future<RegisterBasicUserResult> getRegisterBasicUser(
      String reliantGUID, String programGUID, String formFactor) async {
    return _api.getRegisterBasicUser(reliantGUID, programGUID, formFactor);
  }

  @override
  Future<WriteProfileResult> getWriteProfile(String reliantGUID,
      String programGUID, String rID, bool overwriteCard) async {
    return _api.getWriteProfile(reliantGUID, programGUID, rID, overwriteCard);
  }

  @override
  Future<WritePasscodeResult> getWritePasscode(String reliantGUID,
      String programGUID, String rID, String passcode) async {
    return _api.getWritePasscode(reliantGUID, programGUID, rID, passcode);
  }

  @override
  Future<VerifyPasscodeResult> getVerifyPasscode(
      String reliantGUID,
      String programGUID,
      String passcode,
      FormFactor formFactor,
      String? qrCpUserProfile) async {
    return _api.getVerifyPasscode(
        reliantGUID, programGUID, passcode, formFactor, qrCpUserProfile);
  }

  @override
  Future<UserVerificationResult> getUserVerification(
      String reliantGUID,
      String programGUID,
      FormFactor formFactor,
      String? qrBase64,
      List<String> modalities) async {
    return _api.getUserVerification(
        reliantGUID, programGUID, formFactor, qrBase64, modalities);
  }

  @override
  Future<RegistrationDataResult> getRegistrationData(
      String reliantGUID, String programGUID) async {
    return _api.getRegistrationData(reliantGUID, programGUID);
  }

  @override
  Future<WriteProgramSpaceResult> getWriteProgramSpace(
      String reliantGUID,
      String programGUID,
      String rID,
      String programSpaceData,
      bool encryptData) async {
    return _api.getWriteProgramSpace(
        reliantGUID, programGUID, rID, programSpaceData, encryptData);
  }

  @override
  Future<ReadProgramSpaceResult> getReadProgramSpace(
    String reliantGUID,
    String programGUID,
    String rID,
    bool decryptData,
  ) async {
    return _api.getReadProgramSpace(
      reliantGUID,
      programGUID,
      rID,
      decryptData,
    );
  }

  @override
  Future<BlacklistFormFactorResult> getBlacklistFormFactor(
      String reliantGUID,
      String programGUID,
      String rID,
      String consumerDeviceNumber,
      FormFactor type) async {
    return _api.getBlacklistFormFactor(
        reliantGUID, programGUID, rID, consumerDeviceNumber, type);
  }

  @override
  Future<ReadSVAResult> getReadSVA(String reliantGUID, String programGUID,
      String rID, String svaUnit) async {
    return _api.getReadSVA(reliantGUID, programGUID, rID, svaUnit);
  }

  @override
  Future<CreateSVAResult> getCreateSVA(
      String reliantGUID, String programGUID, String? rID, SVA sva) async {
    return _api.getCreateSVA(reliantGUID, programGUID, rID, sva);
  }

  @override
  Future<UserIdentificationResult> getUserIdentification(
    String reliantGUID,
    String programGUID,
    List<String> modalities,
    bool cacheHashesIfIDentified,
    String? qrBase64,
    FormFactor formFactor,
  ) async {
    return _api.getUserIdentification(reliantGUID, programGUID, modalities,
        cacheHashesIfIDentified, qrBase64, formFactor);
  }

  @override
  Future<GenerateCpUserProfileResult> getGenerateCpUserProfile(
      String reliantGUID,
      String programGUID,
      String rID,
      String? passcode) async {
    return _api.getGenerateCpUserProfile(
        reliantGUID, programGUID, rID, passcode);
  }
}

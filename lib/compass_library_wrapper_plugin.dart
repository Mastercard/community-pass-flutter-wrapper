import 'package:compass_library_wrapper_plugin/compassapi.dart';

import 'compass_library_wrapper_plugin_platform_interface.dart';

class CompassLibraryWrapperPlugin {
  Future<SaveBiometricConsentResult> saveBiometricConsent(
      String reliantGUID, String programGUID, bool consumerConsentValue) {
    return CompassLibraryWrapperPluginPlatform.instance
        .saveBiometricConsent(reliantGUID, programGUID, consumerConsentValue);
  }

  Future<RegisterUserWithBiometricsResult> getRegisterUserWithBiometrics(
      String reliantGUID,
      String programGUID,
      String consentID,
      List<String> modalities,
      OperationMode operationMode) {
    return CompassLibraryWrapperPluginPlatform.instance
        .getRegisterUserWithBiometrics(
            reliantGUID, programGUID, consentID, modalities, operationMode);
  }

  Future<RegisterBasicUserResult> getRegisterBasicUser(
      String reliantGUID, String programGUID, String formFactor) {
    return CompassLibraryWrapperPluginPlatform.instance
        .getRegisterBasicUser(reliantGUID, programGUID, formFactor);
  }

  Future<WriteProfileResult> getWriteProfile(
      String reliantGUID, String programGUID, String rID, bool overwriteCard) {
    return CompassLibraryWrapperPluginPlatform.instance
        .getWriteProfile(reliantGUID, programGUID, rID, overwriteCard);
  }

  Future<WritePasscodeResult> getWritePasscode(
      String reliantGUID, String programGUID, String rID, String passcode) {
    return CompassLibraryWrapperPluginPlatform.instance
        .getWritePasscode(reliantGUID, programGUID, rID, passcode);
  }

  Future<VerifyPasscodeResult> getVerifyPasscode(
      String reliantGUID,
      String programGUID,
      String passcode,
      FormFactor formFactor,
      String? qrCpUserProfile) async {
    return CompassLibraryWrapperPluginPlatform.instance.getVerifyPasscode(
        reliantGUID, programGUID, passcode, formFactor, qrCpUserProfile);
  }

  Future<UserVerificationResult> getUserVerification(
      String reliantGUID,
      String programGUID,
      FormFactor formFactor,
      String? qrBase64,
      List<String> modalities) async {
    return CompassLibraryWrapperPluginPlatform.instance.getUserVerification(
        reliantGUID, programGUID, formFactor, qrBase64, modalities);
  }

  Future<RegistrationDataResult> getRegistrationData(
      String reliantGUID, String programGUID) async {
    return CompassLibraryWrapperPluginPlatform.instance
        .getRegistrationData(reliantGUID, programGUID);
  }

  Future<WriteProgramSpaceResult> getWriteProgramSpace(
      String reliantGUID,
      String programGUID,
      String rID,
      String programSpaceData,
      bool encryptData) async {
    return CompassLibraryWrapperPluginPlatform.instance.getWriteProgramSpace(
        reliantGUID, programGUID, rID, programSpaceData, encryptData);
  }

  Future<ReadProgramSpaceResult> getReadProgramSpace(
    String reliantGUID,
    String programGUID,
    String rID,
    bool decryptData,
  ) async {
    return CompassLibraryWrapperPluginPlatform.instance.getReadProgramSpace(
      reliantGUID,
      programGUID,
      rID,
      decryptData,
    );
  }

  Future<BlacklistFormFactorResult> getBlacklistFormFactor(
      String reliantGUID,
      String programGUID,
      String rID,
      String consumerDeviceNumber,
      FormFactor type) async {
    return CompassLibraryWrapperPluginPlatform.instance.getBlacklistFormFactor(
        reliantGUID, programGUID, rID, consumerDeviceNumber, type);
  }

  Future<ReadSVAResult> getReadSVA(String reliantGUID, String programGUID,
      String rID, String svaUnit) async {
    return CompassLibraryWrapperPluginPlatform.instance
        .getReadSVA(reliantGUID, programGUID, rID, svaUnit);
  }

  Future<CreateSVAResult> getCreateSVA(
      String reliantGUID, String programGUID, String? rID, SVA sva) async {
    return CompassLibraryWrapperPluginPlatform.instance
        .getCreateSVA(reliantGUID, programGUID, rID, sva);
  }

  Future<GenerateCpUserProfileResult> getGenerateCpUserProfile(
      String reliantGUID,
      String programGUID,
      String rID,
      String? passcode) async {
    return CompassLibraryWrapperPluginPlatform.instance
        .getGenerateCpUserProfile(reliantGUID, programGUID, rID, passcode);
  }
}

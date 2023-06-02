import 'package:compass_library_wrapper_plugin/compassapi.dart';

import 'compass_library_wrapper_plugin_platform_interface.dart';

class CompassLibraryWrapperPlugin {
  Future<SaveBiometricConsentResult> saveBiometricConsent(
      String reliantGUID, String programGUID, bool consumerConsentValue) {
    return CompassLibraryWrapperPluginPlatform.instance
        .saveBiometricConsent(reliantGUID, programGUID, consumerConsentValue);
  }

  Future<RegisterUserWithBiometricsResult> getRegisterUserWithBiometrics(
      String reliantGUID, String programGUID, String consentID) {
    return CompassLibraryWrapperPluginPlatform.instance
        .getRegisterUserWithBiometrics(reliantGUID, programGUID, consentID);
  }

  Future<RegisterBasicUserResult> getRegisterBasicUser(
      String reliantGUID, String programGUID) {
    return CompassLibraryWrapperPluginPlatform.instance
        .getRegisterBasicUser(reliantGUID, programGUID);
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

  Future<WriteProgramSpaceResult> getWriteProgramSpace(
      String reliantGUID,
      String programGUID,
      String rID,
      String programSpaceData,
      bool encryptData) {
    return CompassLibraryWrapperPluginPlatform.instance
        .getWriteProgramSpace(reliantGUID, programGUID, rID, programSpaceData, encryptData);
  }

  Future<ReadProgramSpaceResult> getReadProgramSpace(String reliantGUID,
      String programGUID, String rID, bool decryptData) async {
    return CompassLibraryWrapperPluginPlatform.instance.getReadProgramSpace(reliantGUID, programGUID, rID, decryptData);
  }

  Future<VerifyPasscodeResult> getVerifyPasscode(String reliantGUID,
      String programGUID, String passcode) async {
    return CompassLibraryWrapperPluginPlatform.instance.getVerifyPasscode(reliantGUID, programGUID, passcode);
  }
}

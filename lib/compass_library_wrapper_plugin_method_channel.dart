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
  Future<RegisterUserWithBiometricsResult> getRegisterUserWithBiometrics(
      String reliantGUID, String programGUID, String consentID) async {
    return _api.getRegisterUserWithBiometrics(
        reliantGUID, programGUID, consentID);
  }

  @override
  Future<RegisterBasicUserResult> getRegisterBasicUser(
      String reliantGUID, String programGUID) async {
    return _api.getRegisterBasicUser(reliantGUID, programGUID);
  }

  @override
  Future<WriteProfileResult> getWriteProfile(String reliantGUID,
      String programGUID, String rID, bool overwriteCard) async {
    return _api.getWriteProfile(reliantGUID, programGUID, rID, overwriteCard);
  }

  @override
  Future<WriteProgramSpaceResult> getWriteProgramSpace(
      String reliantGUID,
      String programGUID,
      String rID,
      String programSpaceData,
      bool encryptData) async {
    return _api.getWriteProgramSpace(reliantGUID, programGUID, rID, programSpaceData, encryptData);
  }

  @override
  Future<ReadProgramSpaceResult> getReadProgramSpace(String reliantGUID,
      String programGUID, String rID, bool decryptData) async {
    return _api.getReadProgramSpace(reliantGUID, programGUID, rID, decryptData);
  }

  @override
  Future<VerifyPasscodeResult> getVerifyPasscode(String reliantGUID,
      String programGUID, String passcode) async {
    return _api.getVerifyPasscode(reliantGUID, programGUID, passcode);
  }
}

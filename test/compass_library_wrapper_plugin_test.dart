import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:compass_library_wrapper_plugin/compass_library_wrapper_plugin.dart';
import 'package:compass_library_wrapper_plugin/compass_library_wrapper_plugin_platform_interface.dart';
import 'package:compass_library_wrapper_plugin/compass_library_wrapper_plugin_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockCompassLibraryWrapperPluginPlatform
    with MockPlatformInterfaceMixin
    implements CompassLibraryWrapperPluginPlatform {
  @override
  Future<SaveBiometricConsentResult> saveBiometricConsent(
          String reliantGUID, String programGuid, bool consumerConsentValue) =>
      Future.value(SaveBiometricConsentResult(
          consentID: '', responseStatus: ResponseStatus.SUCCESS));

  @override
  Future<RegisterUserWithBiometricsResult> getRegisterUserWithBiometrics(
          String reliantGUID, String programGuid, String consentID) =>
      Future.value(RegisterUserWithBiometricsResult(
          bioToken: '',
          enrolmentStatus: EnrolmentStatus.EXISTING,
          programGUID: '',
          rID: ''));

  @override
  Future<RegisterBasicUserResult> getRegisterBasicUser(
          String reliantGUID, String programGuid) =>
      Future.value(RegisterBasicUserResult(rID: ''));

  @override
  Future<WriteProfileResult> getWriteProfile(String reliantGUID,
          String programGuid, String rID, bool overwriteCard) =>
      Future.value(WriteProfileResult(consumerDeviceNumber: ''));

  @override
  Future<WritePasscodeResult> getWritePasscode(String reliantGUID,
          String programGuid, String rID, String passcode) =>
      Future.value(WritePasscodeResult(responseStatus: ResponseStatus.SUCCESS));

  @override
  Future<WriteProgramSpaceResult> getWriteProgramSpace(String reliantGUID, String programGUID, String rID, String programSpaceData, bool encryptData) =>
      Future.value(WriteProgramSpaceResult(isSuccess: true));

  @override
  Future<ReadProgramSpaceResult> getReadProgramSpace(
      String reliantGUID, String programGUID, String rID, bool decryptData) => Future.value(ReadProgramSpaceResult(programSpaceData: ""));
}

void main() {
  final CompassLibraryWrapperPluginPlatform initialPlatform =
      CompassLibraryWrapperPluginPlatform.instance;

  test('$PigeonCompassLibraryWrapperPlugin is the default instance', () {
    expect(initialPlatform, isInstanceOf<PigeonCompassLibraryWrapperPlugin>());
  });

  test('saveBiometricConsent', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.saveBiometricConsent(
            '', '', true),
        SaveBiometricConsentResult(
            consentID: '', responseStatus: ResponseStatus.SUCCESS));
  });

  test('getRegisterUserWithBiometrics', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getRegisterUserWithBiometrics(
            '', '', ''),
        RegisterUserWithBiometricsResult(
            bioToken: '',
            programGUID: '',
            rID: '',
            enrolmentStatus: EnrolmentStatus.EXISTING));
  });

  test('getRegisterBasicUser', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getRegisterBasicUser('', ''),
        RegisterBasicUserResult(rID: ''));
  });

  test('getWriteProfile', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getWriteProfile(
            '', '', '', false),
        WriteProfileResult(consumerDeviceNumber: ''));
  });

  test('getWritePasscode', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getWritePasscode(
            '', '', '', ''),
        WritePasscodeResult(responseStatus: ResponseStatus.SUCCESS));
  });
}

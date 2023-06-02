import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:compass_library_wrapper_plugin/compass_library_wrapper_plugin_method_channel.dart';

void main() {
  PigeonCompassLibraryWrapperPlugin platform =
      PigeonCompassLibraryWrapperPlugin();
  const MethodChannel channel = MethodChannel('compass_library_wrapper_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('saveBiometricConsent', () async {
    expect(
        await platform.saveBiometricConsent('', '', true),
        SaveBiometricConsentResult(
            consentID: '', responseStatus: ResponseStatus.SUCCESS));
  });

  test('getRegisterUserWithBiometrics', () async {
    expect(
        await platform.getRegisterUserWithBiometrics('', '', ''),
        RegisterUserWithBiometricsResult(
            bioToken: '',
            programGUID: '',
            rID: '',
            enrolmentStatus: EnrolmentStatus.EXISTING));
  });

  test('getRegisterBasicUser', () async {
    expect(await platform.getRegisterBasicUser('', ''),
        RegisterBasicUserResult(rID: ''));
  });

  test('getWriteProfile', () async {
    expect(await platform.getWriteProfile('', '', '', false),
        WriteProfileResult(consumerDeviceNumber: ''));
  });

  test('getWritePasscode', () async {
    expect(await platform.getWritePasscode('', '', '', ''),
        WritePasscodeResult(responseStatus: ResponseStatus.SUCCESS));
  });

  test('getWriteProgramSpace', () async {
    expect(await platform.getWriteProgramSpace('', '', '', '', false),
        WriteProgramSpaceResult(isSuccess: true));
  });

  test('getReadProgramSpace', () async {
    expect(await platform.getReadProgramSpace('', '', '', false),
        ReadProgramSpaceResult(programSpaceData: ""));
  });
}

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
          String reliantGUID,
          String programGuid,
          String consentID,
          List<String> modalities,
          OperationMode operationMode) =>
      Future.value(RegisterUserWithBiometricsResult(
          bioToken: '',
          enrolmentStatus: EnrolmentStatus.EXISTING,
          programGUID: '',
          rID: ''));

  @override
  Future<RegisterBasicUserResult> getRegisterBasicUser(
          String reliantGUID, String programGuid, String formFactor) =>
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
  Future<BlacklistFormFactorResult> getBlacklistFormFactor(
          String reliantGUID,
          String programGUID,
          String rID,
          String consumerDeviceNumber,
          FormFactor type) =>
      Future.value(BlacklistFormFactorResult(
          type: '', status: FormFactorStatus.ACTIVE, consumerDeviceNumber: ''));

  @override
  Future<CreateSVAResult> getCreateSVA(
          String reliantGUID, String programGUID, String? rID, SVA sva) =>
      Future.value(CreateSVAResult(response: ''));

  @override
  Future<ReadProgramSpaceResult> getReadProgramSpace(String reliantGUID,
          String programGUID, String rID, bool decryptData) =>
      Future.value(ReadProgramSpaceResult(programSpaceData: ''));

  @override
  Future<ReadSVAResult> getReadSVA(
          String reliantGUID, String programGUID, String rID, String svaUnit) =>
      Future.value(ReadSVAResult(
          currentBalance: 0,
          transactionCount: 0,
          purseType: '',
          unit: '',
          lastTransaction: Transaction(amount: 0, balance: 0)));

  @override
  Future<RegistrationDataResult> getRegistrationData(
          String reliantGUID, String programGUID) =>
      Future.value(RegistrationDataResult(
          isRegisteredInProgram: false,
          authType: [],
          modalityType: [],
          rID: ''));

  @override
  Future<UserVerificationResult> getUserVerification(
          String reliantGUID,
          String programGUID,
          FormFactor formFactor,
          String? qrBase64,
          List<String> modalities) =>
      Future.value(UserVerificationResult(
          isMatchFound: false, rID: '', biometricMatchList: []));

  @override
  Future<VerifyPasscodeResult> getVerifyPasscode(
          String reliantGUID,
          String programGUID,
          String passcode,
          FormFactor formFactor,
          String? qrCpUserProfile) =>
      Future.value(
          VerifyPasscodeResult(rID: '', status: false, retryCount: null));

  @override
  Future<WriteProgramSpaceResult> getWriteProgramSpace(
          String reliantGUID,
          String programGUID,
          String rID,
          String programSpaceData,
          bool encryptData) =>
      Future.value(WriteProgramSpaceResult(
        isSuccess: false,
      ));

  @override
  Future<GenerateCpUserProfileResult> getGenerateCpUserProfile(
          String reliantGUID,
          String programGUID,
          String rID,
          String? passcode) =>
      Future.value(GenerateCpUserProfileResult(
          token: '', consumerDeviceNumber: '', message: ''));
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
            '', '', '', List<String>.empty(), OperationMode.BEST_AVAILABLE),
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
        await compassLibraryWrapperPluginInstance.getRegisterBasicUser(
            '', '', ''),
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

  test('getVerifyPasscode', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getVerifyPasscode(
            '', '', '', FormFactor.CARD, ''),
        VerifyPasscodeResult(rID: '', status: false, retryCount: null));
  });

  test('getUserVerification', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance
            .getUserVerification('', '', FormFactor.CARD, '', ['']),
        UserVerificationResult(
            isMatchFound: false, rID: '', biometricMatchList: []));
  });

  test('getRegistrationData', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getRegistrationData('', ''),
        RegistrationDataResult(
            isRegisteredInProgram: false,
            authType: [],
            modalityType: [],
            rID: ''));
  });

  test('getWriteProgramSpace', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getWriteProgramSpace(
            '', '', '', '', false),
        WriteProgramSpaceResult(isSuccess: false));
  });

  test('getReadProgramSpace', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getReadProgramSpace(
            '', '', '', false),
        ReadProgramSpaceResult(programSpaceData: ''));
  });

  test('getBlacklistFormFactor', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getBlacklistFormFactor(
            '', '', '', '', FormFactor.CARD),
        BlacklistFormFactorResult(
            type: '',
            status: FormFactorStatus.ACTIVE,
            consumerDeviceNumber: ''));
  });

  test('getReadSVA', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getReadSVA('', '', '', ''),
        ReadSVAResult(
            currentBalance: 0,
            transactionCount: 0,
            purseType: '',
            unit: '',
            lastTransaction: Transaction(amount: 0, balance: 0)));
  });
  test('getCreateSVA', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getCreateSVA(
            '',
            '',
            '',
            SVA(
                type: SVAType.EVoucherSVA,
                unit: '',
                eVoucherType: EVoucherType.COMMODITY)),
        CreateSVAResult(response: ''));
  });

  test('getGenerateCpUserProfile', () async {
    CompassLibraryWrapperPlugin compassLibraryWrapperPluginInstance =
        CompassLibraryWrapperPlugin();
    MockCompassLibraryWrapperPluginPlatform fakePlatform =
        MockCompassLibraryWrapperPluginPlatform();
    CompassLibraryWrapperPluginPlatform.instance = fakePlatform;

    expect(
        await compassLibraryWrapperPluginInstance.getGenerateCpUserProfile(
            '', '', '', ''),
        GenerateCpUserProfileResult(
            token: '', consumerDeviceNumber: '', message: ''));
  });
}

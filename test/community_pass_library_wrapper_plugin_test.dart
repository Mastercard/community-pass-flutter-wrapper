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

  test('communityPassConsentWithPreBuiltUI', () async {
    expect(
        await platform.communityPassConsentWithPreBuiltUI(
            '',
            '',
            ConsentScreenConfig(
                partnerPrivacyPolicyTitle: "partnerPrivacyPolicyTitle",
                partnerPrivacyPolicyContent: "partnerPrivacyPolicyContent",
                partnerPrivacyPolicyExcerptTitle:
                    "partnerPrivacyPolicyExcerptTitle",
                partnerPrivacyPolicyExcerptContent:
                    "partnerPrivacyPolicyExcerptContent",
                acceptConsentButtonLabel: "acceptConsentButtonLabel",
                declineConsentButtonLabel: "declineConsentButtonLabel",
                enableCommunityPassPrivacyPolicy: true,
                enableBiometricNotice: true,
                enablePartnerPrivacyPolicy: true,
                beforeYouProceedText: "beforeYouProceedText",
                beforeYouProceedFontSize: 14,
                consentTitleFontSize: 14,
                consentContentFontSize: 14,
                switchLabelFontSize: 14,
                buttonLabelFontSize: 14,
                buttonBorderRadius: 50,
                buttonHeight: 50,
                darkThemeColorScheme: DarkThemeColorScheme(
                    primary: "#000000",
                    onPrimary: "#000000",
                    primaryContainer: "#000000",
                    onPrimaryContainer: "#000000",
                    background: "#000000",
                    onBackground: "#000000",
                    tertiaryContainer: "#000000"),
                lightThemeColorScheme: LightThemeColorScheme(
                    primary: "#ffffff",
                    onPrimary: "#ffffff",
                    primaryContainer: "#ffffff",
                    onPrimaryContainer: "#ffffff",
                    background: "#ffffff",
                    onBackground: "#ffffff",
                    tertiaryContainer: "#ffffff"))),
        CommunityPassConsentScreenResult(
            status: ConsentStatus.CONSENT_DENIED, result: null));
  });

  test('getRegisterUserWithBiometrics', () async {
    expect(
        await platform.getRegisterUserWithBiometrics(
            '', '', '', List<String>.empty(), OperationMode.BEST_AVAILABLE),
        RegisterUserWithBiometricsResult(
            bioToken: '',
            programGUID: '',
            rID: '',
            enrolmentStatus: EnrolmentStatus.EXISTING));
  });

  test('getRegisterBasicUser', () async {
    expect(await platform.getRegisterBasicUser('', '', ''),
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

  test('getVerifyPasscode', () async {
    expect(await platform.getVerifyPasscode('', '', '', FormFactor.CARD, ''),
        VerifyPasscodeResult(rID: '', status: false, retryCount: null));
  });

  test('getUserVerification', () async {
    expect(
        await platform.getUserVerification('', '', FormFactor.CARD, '', ['']),
        UserVerificationResult(
            isMatchFound: false, rID: '', biometricMatchList: []));
  });

  test('getUserIdentification', () async {
    expect(
        await platform.getUserIdentification(
            '', '', [], true, '', FormFactor.CARD),
        UserVerificationResult(
            isMatchFound: false, rID: '', biometricMatchList: []));
  });

  test('getRegistrationData', () async {
    expect(
        await platform.getRegistrationData('', ''),
        RegistrationDataResult(
            isRegisteredInProgram: false,
            authType: [],
            modalityType: [],
            rID: ''));
  });

  test('getWriteProgramSpace', () async {
    expect(await platform.getWriteProgramSpace('', '', '', '', false),
        WriteProgramSpaceResult(isSuccess: false));
  });

  test('getReadProgramSpace', () async {
    expect(await platform.getReadProgramSpace('', '', '', false),
        ReadProgramSpaceResult(programSpaceData: ''));
  });
  test('getBlacklistFormFactor', () async {
    expect(
        await platform.getBlacklistFormFactor('', '', '', '', FormFactor.CARD),
        BlacklistFormFactorResult(
            type: '',
            status: FormFactorStatus.ACTIVE,
            consumerDeviceNumber: ''));
  });
  test('getReadSVA', () async {
    expect(
        await platform.getReadSVA('', '', '', ''),
        ReadSVAResult(
            currentBalance: 0,
            transactionCount: 0,
            purseType: '',
            unit: '',
            lastTransaction: Transaction(amount: 0, balance: 0)));
  });
  test('getCreateSVA', () async {
    expect(
        await platform.getCreateSVA(
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
    expect(
        await platform.getGenerateCpUserProfile('', '', '', ''),
        GenerateCpUserProfileResult(
            token: '', consumerDeviceNumber: '', message: ''));
  });
}

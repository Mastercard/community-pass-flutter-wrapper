import 'package:flutter/material.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/registerBasicUserScreen.dart';
import 'package:compass_library_wrapper_plugin_example/registerUserWithBiometricsScreen.dart';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class CompassConsentScreenScreen extends StatefulWidget {
  const CompassConsentScreenScreen({super.key});

  @override
  State<CompassConsentScreenScreen> createState() =>
      _CompassConsentScreenScreen();
}

class _CompassConsentScreenScreen extends State<CompassConsentScreenScreen>
    with TickerProviderStateMixin {
  final _communityPassFlutterplugin = CommunityPassApi();
  static final String _reliantAppGuid = dotenv.env['RELIANT_APP_GUID'] ?? '';
  static final String _programGuid = dotenv.env['PROGRAM_GUID'] ?? '';

  String globalError = '';
  bool globalLoading = false;

  late AnimationController controller;

  @override
  void initState() {
    controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 1),
    )..addListener(() {
        setState(() {});
      });
    controller.repeat(reverse: true);
    super.initState();
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  Future<void> requestUserForConsent(
      String reliantGUID, String programGUID) async {
    if (mounted) {
      globalLoading = true;
    }

    CommunityPassConsentScreenResult result;
    try {
      result = await _communityPassFlutterplugin.communityPassConsentWithPreBuiltUI(
          reliantGUID,
          programGUID,
          ConsentScreenConfig(
              partnerPrivacyPolicyTitle: "Partner Privacy Policy.",
              partnerPrivacyPolicyContent:
                  "The following is placeholder text that should be replaced with an actual partner's privacy policy text.\n\nTo create your digital profile, we need information about you, including your name, contact details and date of birth. This is to recognize you from other people and ensure we can uniquely identify you in the system. Mastercard is responsible for your profile data.\n\nWe store it securely in the United States. We only create the digital profile with your consent. You decide how your data is used. At any time, you can ask how we use your data or tell us you no longer want your digital profile to exist. We will delete your profile. If you have any such wish, please contact your agent or any nearby service provider.\n\nWhen you withdraw your consent to Community Pass, you have a 30-day period to change your mind and opt-in again. If you do not opt-in again within 30 days after having withdrawn your consent to Community Pass, we will automatically be deleting your profile.\n\nClick the close button to close the Partner Privacy Policy and go back to the Consent Page.",
              partnerPrivacyPolicyExcerptTitle:
                  "Consent to Partner's Reliant Application",
              partnerPrivacyPolicyExcerptContent:
                  "This is placeholder text that should be replaced with an actual partner's privacy policy excerpt text.",
              acceptConsentButtonLabel: "Accept",
              declineConsentButtonLabel: "Decline",
              enableCommunityPassPrivacyPolicy: true,
              enableBiometricNotice: true,
              enablePartnerPrivacyPolicy: true,
              beforeYouProceedText: "BEFORE YOU PROCEED",
              beforeYouProceedFontSize: 18,
              consentTitleFontSize: 16,
              consentContentFontSize: 14,
              switchLabelFontSize: 14,
              buttonLabelFontSize: 14,
              buttonBorderRadius: 25,
              buttonHeight: 50,
              darkThemeColorScheme: DarkThemeColorScheme(
                  primary: "#ff0000",
                  onPrimary: "#00ff00",
                  primaryContainer: "#000ff0",
                  onPrimaryContainer: "#0ff000",
                  background: "#00ff00",
                  onBackground: "#0000ff",
                  tertiaryContainer: "#0ff000"),
              lightThemeColorScheme: LightThemeColorScheme(
                  primary: "#ff0000",
                  onPrimary: "#00ff00",
                  primaryContainer: "#000ff0",
                  onPrimaryContainer: "#0ff000",
                  background: "#00ff00",
                  onBackground: "#0000ff",
                  tertiaryContainer: "#0ff000")));

      // check whether the state is mounted on the tree
      if (!mounted) return;

      if (result.status == ConsentStatus.CONSENT_GRANTED) {
        setState(() {
          globalLoading = false;
        });
        Navigator.push(
            context,
            MaterialPageRoute(
                builder: (context) => RegisterUserWithBiometricsScreen(
                    value: result.result!.additionalInfo!.consentID!)));
      } else {
        setState(() {
          globalLoading = false;
        });
        Navigator.of(context).push(MaterialPageRoute(
            builder: (context) => const RegisterBasicUserScreen()));
      }
    } on PlatformException catch (ex) {
      if (!mounted) return;
      setState(() {
        globalError = "${ex.code}: ${ex.message}";
        globalLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Pre-Built Consent UI'),
          backgroundColor: mastercardOrange,
        ),
        body: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                child: globalError.isNotEmpty
                    ? Text(
                        'Error: $globalError',
                        style:
                            const TextStyle(fontSize: 12, color: mastercardRed),
                      )
                    : null),
            const Padding(
                padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                child: Text(
                  'Part 1: Capture Community Pass Consent',
                  style: TextStyle(fontSize: 20),
                )),
            const Padding(
                padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                child: Text(
                  'This step calls uses the communityPassConsentWithPreBuiltUI API method. In this step, a pre-built consent UI is displayed to the user to capture 3 different types of consent. The user can decline or grant all consent.',
                  style: TextStyle(fontSize: 16),
                )),
            Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 20, vertical: 5),
                child: globalLoading
                    ? LinearProgressIndicator(
                        value: controller.value,
                        color: mastercardOrange,
                        backgroundColor: gray,
                        semanticsLabel: 'Linear progress indicator',
                      )
                    : null),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                    flex: 1,
                    child: Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 20, vertical: 20),
                        child: ElevatedButton(
                            style: ElevatedButton.styleFrom(
                                minimumSize: const Size(100, 50),
                                backgroundColor: mastercardOrange),
                            onPressed: globalLoading
                                ? null
                                : (() {
                                    requestUserForConsent(
                                        _reliantAppGuid, _programGuid);
                                  }),
                            child: const Text('Show Consent UI'))))
              ],
            )
          ],
        ));
  }
}

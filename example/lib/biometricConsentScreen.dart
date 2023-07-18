import 'package:flutter/material.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/registerBasicUserScreen.dart';
import 'package:compass_library_wrapper_plugin_example/registerUserWithBiometricsScreen.dart';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class BiometricConsentScreen extends StatefulWidget {
  const BiometricConsentScreen({super.key});

  @override
  State<BiometricConsentScreen> createState() => _BiometricConsentScreenState();
}

class _BiometricConsentScreenState extends State<BiometricConsentScreen>
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

  Future<void> saveBiometricConsent(
      String reliantGUID, String programGUID, bool consumerConsentValue) async {
    if (mounted) {
      globalLoading = true;
    }

    SaveBiometricConsentResult result;
    try {
      result = await _communityPassFlutterplugin.saveBiometricConsent(
          reliantGUID, programGUID, consumerConsentValue);

      // check whether the state is mounted on the tree
      if (!mounted) return;

      if (result.responseStatus == ResponseStatus.SUCCESS &&
          consumerConsentValue) {
        setState(() {
          globalLoading = false;
        });
        Navigator.push(
            context,
            MaterialPageRoute(
                builder: (context) =>
                    RegisterUserWithBiometricsScreen(value: result.consentID)));
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
          title: const Text('Biometrics Consent'),
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
                  'Part 1: Capture Consent',
                  style: TextStyle(fontSize: 20),
                )),
            const Padding(
                padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                child: Text(
                  'This step calls the saveBiometricConsent API method. In this step, we check that a user has consented to capturing and storing their biometrics. If a user declines. You will register using passcode.',
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
                        child: OutlinedButton(
                            style: OutlinedButton.styleFrom(
                              minimumSize: const Size(100, 50),
                            ),
                            onPressed: globalLoading
                                ? null
                                : (() {
                                    saveBiometricConsent(
                                        _reliantAppGuid, _programGuid, false);
                                  }),
                            child: const Text(
                              'Deny Consent',
                              style: TextStyle(color: mastercardOrange),
                            )))),
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
                                    saveBiometricConsent(
                                        _reliantAppGuid, _programGuid, true);
                                  }),
                            child: const Text('Grant Consent'))))
              ],
            )
          ],
        ));
  }
}

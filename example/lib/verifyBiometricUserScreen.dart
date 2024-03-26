import 'package:compass_library_wrapper_plugin_example/main.dart';
import 'package:compass_library_wrapper_plugin_example/writeProfileScreen.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class VerifyBiometricUserScreen extends StatefulWidget {
  Map<String, dynamic> navigationParams;
  VerifyBiometricUserScreen({super.key, required this.navigationParams});

  @override
  State<VerifyBiometricUserScreen> createState() =>
      _VerifyBiometricUserScreenState(navigationParams);
}

class _VerifyBiometricUserScreenState extends State<VerifyBiometricUserScreen>
    with TickerProviderStateMixin {
  Map<String, dynamic> receivedParams;
  _VerifyBiometricUserScreenState(this.receivedParams);

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

  Future<void> getUserVerification(
      String reliantGUID, String programGUID) async {
    if (mounted) {
      setState(() {
        globalLoading = true;
      });
    }
    UserVerificationResult result;

    try {
      result = await _communityPassFlutterplugin.getUserVerification(
          reliantGUID,
          programGUID,
          FormFactor.CARD,
          null,
          receivedParams["modalityType"]);

      if (!mounted) return;
      setState(() {
        globalLoading = false;
        Future.delayed(Duration.zero, () {
          showAlert(context, result.rID, result.isMatchFound);
        });
      });
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
          title: const Text('Verify Biometric User'),
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
                          style: const TextStyle(
                              fontSize: 12, color: mastercardRed),
                        )
                      : null),
              const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                  child: Text(
                    'Part 2: Verify User Biometrics',
                    style: TextStyle(fontSize: 20),
                  )),
              const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                  child: Text(
                    'This step calls the getUserVerification API method and returns a rID, isRegisteredInProgram and authMethods.',
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
              SizedBox(
                  width: double.infinity,
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
                                  getUserVerification(
                                      _reliantAppGuid, _programGuid);
                                }),
                          child: const Text('Verify User Biometrics')))),
            ]));
  }

  void showAlert(BuildContext context, rID, isMatchFound) {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
              title: isMatchFound
                  ? const Text('User Found In Records!')
                  : const Text('User Not Found In Records!'),
              content: isMatchFound
                  ? const Text(
                      'A match of the biometrics hashes of this user has been found in our records.')
                  : const Text("User records does not exist in our database."),
              actions: <Widget>[
                TextButton(
                  onPressed: () => Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) => const MyApp(),
                  )),
                  child: const Text('Go to Home Page',
                      style: TextStyle(color: mastercardRed)),
                ),
              ],
            ));
  }
}

import 'package:compass_library_wrapper_plugin_example/main.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class VerifyPasscodeScreen extends StatefulWidget {
  Map<String, dynamic> navigationParams;
  VerifyPasscodeScreen({super.key, required this.navigationParams});

  @override
  State<VerifyPasscodeScreen> createState() =>
      _VerifyPasscodeScreenState(navigationParams);
}

class _VerifyPasscodeScreenState extends State<VerifyPasscodeScreen>
    with TickerProviderStateMixin {
  Map<String, dynamic> receivedParams;
  _VerifyPasscodeScreenState(this.receivedParams);

  final myController = TextEditingController();
  late AnimationController controller;

  final _communityPassFlutterplugin = CommunityPassApi();
  static final String _reliantAppGuid = dotenv.env['RELIANT_APP_GUID'] ?? '';
  static final String _programGuid = dotenv.env['PROGRAM_GUID'] ?? '';

  String globalError = '';
  bool globalLoading = false;

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
    // Clean up the controller when the widget is disposed.
    myController.dispose();
    controller.dispose();
    super.dispose();
  }

  Future<void> getRegistrationData(
      String reliantGUID, String programGUID, String passcode) async {
    if (mounted) {
      setState(() {
        globalLoading = true;
      });
    }
    VerifyPasscodeResult result;

    try {
      result = await _communityPassFlutterplugin.getVerifyPasscode(
          reliantGUID, programGUID, passcode, FormFactor.CARD, null);

      if (!mounted) return;
      setState(() {
        globalLoading = false;
        Future.delayed(Duration.zero, () {
          showAlert(context, result.rID, result.retryCount, result.status);
        });
        // if (result.authType.contains("BIO")) {
        //   Navigator.of(context).push(MaterialPageRoute(
        //       builder: (context) => WriteProfileScreen(navigationParams: {
        //             "rID": result.rID,
        //             "registrationType": result.rID,
        //           })));
        // } else {
        //   Navigator.of(context).push(MaterialPageRoute(
        //       builder: (context) => WriteProfileScreen(navigationParams: {
        //             "rID": result.rID,
        //             "registrationType": result.rID,
        //           })));
        // }
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
          title: const Text('Verify Basic User'),
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
                    'Part 2: Verify Passcode',
                    style: TextStyle(fontSize: 20),
                  )),
              const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                  child: Text(
                    'This step calls the getVerifyPasscode API method and returns a rID, isRegisteredInProgram and authMethods.',
                    style: TextStyle(fontSize: 16),
                  )),
              Padding(
                padding:
                    const EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                child: TextField(
                  controller: myController,
                  cursorColor: mastercardYellow,
                  keyboardType: TextInputType.number,
                  decoration: const InputDecoration(
                    border: OutlineInputBorder(),
                    enabledBorder: OutlineInputBorder(
                      borderSide: BorderSide(width: 3, color: mastercardOrange),
                    ),
                    focusedBorder: OutlineInputBorder(
                      borderSide: BorderSide(width: 3, color: mastercardYellow),
                    ),
                    hintText: 'Enter a 6 digit passcode',
                  ),
                ),
              ),
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
                                  getRegistrationData(
                                      _reliantAppGuid,
                                      _programGuid,
                                      myController.text.toString());
                                }),
                          child: const Text('Verify Passcode')))),
            ]));
  }

  void showAlert(BuildContext context, rID, retryCount, status) {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
              title: status
                  ? const Text('User Found In Records!')
                  : const Text('User Not Found In Records!'),
              content: status
                  ? const Text(
                      'A match of the biometrics hashes of this user has been found in our records.')
                  : Text(
                      "User records does not exist in our database.\n\n ${retryCount == null ? "" : "Retry Count: $retryCount"}"),
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

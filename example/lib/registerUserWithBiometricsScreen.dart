import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/main.dart';
import 'package:compass_library_wrapper_plugin_example/writeProfileScreen.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class RegisterUserWithBiometricsScreen extends StatefulWidget {
  String value;
  RegisterUserWithBiometricsScreen({super.key, required this.value});

  @override
  State<RegisterUserWithBiometricsScreen> createState() =>
      _RegisterUserWithBiometricsScreenState(value);
}

class _RegisterUserWithBiometricsScreenState
    extends State<RegisterUserWithBiometricsScreen>
    with TickerProviderStateMixin {
  String value;
  _RegisterUserWithBiometricsScreenState(this.value);

  static final String _reliantAppGuid = dotenv.env['RELIANT_APP_GUID'] ?? '';
  static final String _programGuid = dotenv.env['PROGRAM_GUID'] ?? '';

  final _communityPassFlutterplugin = CommunityPassApi();

  List<String> listOfModalities = ["FACE", "LEFT_PALM", "RIGHT_PALM"];
  String globalError = '';
  bool globalLoading = false;
  bool isDialogOpen = false;

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

  Future<void> getRegisterUserWithBiometrics(
      String reliantGUID,
      String programGUID,
      String consentID,
      List<String> modalities,
      OperationMode operationMode) async {
    if (mounted) {
      setState(() {
        globalLoading = true;
      });
    }

    RegisterUserWithBiometricsResult result;

    try {
      result = await _communityPassFlutterplugin.getRegisterUserWithBiometrics(
          reliantGUID, programGUID, consentID, modalities, operationMode);

      if (!mounted) return;
      setState(() {
        globalLoading = false;
        if (result.enrolmentStatus == EnrolmentStatus.EXISTING) {
          Future.delayed(Duration.zero, () {
            showAlert(context, result.rID);
          });
        } else {
          Navigator.of(context).push(MaterialPageRoute(
            builder: (context) => WriteProfileScreen(navigationParams: {
              "rID": result.rID,
              "registrationType": 'BIOMETRIC_USER',
            }),
          ));
        }
      });
    } on PlatformException catch (ex) {
      globalError = "${ex.code}: ${ex.message}";
      globalLoading = false;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Register User With Biometrics'),
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
                    'Part 2: Register Biometric User',
                    style: TextStyle(fontSize: 20),
                  )),
              const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                  child: Text(
                    'This step calls the getRegisterUserWithBiometrics API method. The kernel will perform capture, validate biometric hashes created via a one to many search, create the Compass and return a R-ID as part of the process.',
                    style: TextStyle(fontSize: 16),
                  )),
              CheckboxListTile(
                title: const Text("Face"),
                value: listOfModalities.contains("FACE"),
                activeColor: mastercardOrange,
                onChanged: (newValue) {
                  if (newValue == true) {
                    listOfModalities.add("FACE");
                  } else {
                    listOfModalities.remove("FACE");
                  }
                },
                controlAffinity:
                    ListTileControlAffinity.leading, //  <-- leading Checkbox
              ),
              CheckboxListTile(
                title: const Text("Left Palm"),
                value: listOfModalities.contains("LEFT_PALM"),
                activeColor: mastercardOrange,
                onChanged: (newValue) {
                  if (newValue == true) {
                    listOfModalities.add("LEFT_PALM");
                  } else {
                    listOfModalities.remove("LEFT_PALM");
                  }
                },
                controlAffinity:
                    ListTileControlAffinity.leading, //  <-- leading Checkbox
              ),
              CheckboxListTile(
                title: const Text("Right Palm"),
                value: listOfModalities.contains("RIGHT_PALM"),
                activeColor: mastercardOrange,
                onChanged: (newValue) {
                  if (newValue == true) {
                    listOfModalities.add("RIGHT_PALM");
                  } else {
                    listOfModalities.remove("RIGHT_PALM");
                  }
                },
                controlAffinity:
                    ListTileControlAffinity.leading, //  <-- leading Checkbox
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
                                  getRegisterUserWithBiometrics(
                                      _reliantAppGuid,
                                      _programGuid,
                                      value,
                                      listOfModalities,
                                      OperationMode.BEST_AVAILABLE);
                                }),
                          child: const Text('Start registration')))),
            ]));
  }

  void showAlert(BuildContext context, rID) {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
              title: const Text('User Found In Records!'),
              content: const Text(
                  'A match of the biometrics hashes of this user has been found in our records.'),
              actions: <Widget>[
                TextButton(
                  onPressed: () => Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) => const MyApp(),
                  )),
                  child: const Text('Cancel Registration',
                      style: TextStyle(color: mastercardRed)),
                ),
                TextButton(
                  onPressed: () => Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) => WriteProfileScreen(navigationParams: {
                      "rID": rID,
                      "registrationType": 'BIOMETRIC_USER',
                    }),
                  )),
                  child: const Text(
                    'Continue with Registration',
                    style: TextStyle(color: Colors.green),
                  ),
                ),
              ],
            ));
  }
}

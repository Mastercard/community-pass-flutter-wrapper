import 'package:compass_library_wrapper_plugin_example/ReadProgramSpaceScreen.dart';
import 'package:compass_library_wrapper_plugin_example/verifyBiometricUserScreen.dart';
import 'package:compass_library_wrapper_plugin_example/verifyPasscodeScreen.dart';
import 'package:compass_library_wrapper_plugin_example/writeProfileScreen.dart';
import 'package:compass_library_wrapper_plugin_example/writeProgramSpaceScreen.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class RegistrationDataScreen extends StatefulWidget {
  Map<String, String> navigationParams;
  RegistrationDataScreen({super.key, required this.navigationParams});

  @override
  State<RegistrationDataScreen> createState() =>
      _RegistrationDataScreenState(navigationParams);
}

class _RegistrationDataScreenState extends State<RegistrationDataScreen>
    with TickerProviderStateMixin {
  Map<String, dynamic> receivedParams;
  _RegistrationDataScreenState(this.receivedParams);

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

  Future<void> getRegistrationData(
      String reliantGUID, String programGUID) async {
    if (mounted) {
      setState(() {
        globalLoading = true;
      });
    }
    RegistrationDataResult result;

    try {
      result = await _communityPassFlutterplugin.getRegistrationData(
          reliantGUID, programGUID);

      if (!mounted) return;
      setState(() {
        globalLoading = false;
        switch (receivedParams["flag"]) {
          case "AUTH":
            {
              if (result.isRegisteredInProgram == false) {
                setState(() {
                  globalError = "User not registered in the program.";
                  globalLoading = false;
                });
              } else if (result.authType.contains("BIO")) {
                Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) =>
                        VerifyBiometricUserScreen(navigationParams: {
                          "rID": result.rID,
                          "authType": result.authType,
                          "modalityType": result.modalityType
                        })));
              } else {
                Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) =>
                        VerifyPasscodeScreen(navigationParams: {
                          "rID": result.rID,
                          "authType": result.authType,
                        })));
              }
            }
            break;
          case "READ_PROGRAM":
            {
              Navigator.of(context).push(MaterialPageRoute(
                  builder: (context) =>
                      ReadProgramSpaceScreen(navigationParams: {
                        "rID": result.rID,
                      })));
            }
            break;
          case "WRITE_PROGRAM":
            {
              Navigator.of(context).push(MaterialPageRoute(
                  builder: (context) =>
                      WriteProgramSpaceScreen(navigationParams: {
                        "rID": result.rID,
                      })));
            }
            break;
        }
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
          title: const Text('Get Registration Data'),
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
                    'Part 1: Get Registration Data',
                    style: TextStyle(fontSize: 20),
                  )),
              const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                  child: Text(
                    'This step calls the getRegistrationData API method and returns a rID, isRegisteredInProgram and authMethods.',
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
                                  getRegistrationData(
                                      _reliantAppGuid, _programGuid);
                                }),
                          child: const Text('Get Registration Data')))),
            ]));
  }
}

import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/writePasscodeScreen.dart';
import 'package:compass_library_wrapper_plugin_example/writeSuccessfulScreen.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class WriteProfileScreen extends StatefulWidget {
  Map<String, String> navigationParams;
  WriteProfileScreen({super.key, required this.navigationParams});

  @override
  State<WriteProfileScreen> createState() =>
      _WriteProfileScreenState(navigationParams);
}

class _WriteProfileScreenState extends State<WriteProfileScreen>
    with TickerProviderStateMixin {
  Map<String, String> receivedParams;
  _WriteProfileScreenState(this.receivedParams);

  static final String _reliantAppGuid = dotenv.env['RELIANT_APP_GUID'] ?? '';
  static final String _programGuid = dotenv.env['PROGRAM_GUID'] ?? '';

  final _communityPassFlutterplugin = CommunityPassApi();

  String globalError = '';
  bool globalLoading = false;
  int? overwriteCardValue;
  int selectedOption = 1;
  String? registrationType;

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
    receivedParams['registrationType']!;
    super.initState();
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  Future<void> getGenerateCpUserProfile(String reliantGUID, String programGUID,
      String rID, String? passcode) async {
    if (mounted) {
      setState(() {
        globalLoading = true;
      });
    }
    GenerateCpUserProfileResult result;

    try {
      result = await _communityPassFlutterplugin.getGenerateCpUserProfile(
          reliantGUID, programGUID, rID, passcode);

      if (!mounted) return;
      setState(() {
        globalLoading = false;
        globalLoading = false;
        if (receivedParams['registrationType'] == "BIOMETRIC_USER") {
          Navigator.of(context).push(MaterialPageRoute(
            builder: (context) => WriteSuccessfulScreen(navigationParams: {
              "rID": receivedParams['rID']!,
              "formFactor": "QR",
              "registrationType": 'BIOMETRIC_USER',
              "consumerDeviceNumber": result.consumerDeviceNumber,
              "token": result.token,
            }),
          ));
        } else {
          Navigator.of(context).push(MaterialPageRoute(
            builder: (context) => WritePasscodeScreen(navigationParams: {
              "rID": receivedParams['rID']!,
              "registrationType": 'BASIC_USER',
              "formFactor": "QR",
              "consumerDeviceNumber": result.consumerDeviceNumber,
              "token": result.token,
            }),
          ));
        }
      });
    } on PlatformException catch (ex) {
      setState(() {
        if (!mounted) return;
        globalError = "${ex.code}: ${ex.message}";
        globalLoading = false;
      });
    }
  }

  Future<void> getWriteProfile(String reliantGUID, String programGUID,
      String rID, bool overwriteCard) async {
    if (mounted) {
      setState(() {
        globalLoading = true;
      });
    }
    WriteProfileResult result;

    try {
      result = await _communityPassFlutterplugin.getWriteProfile(
          reliantGUID, programGUID, rID, overwriteCard);

      if (!mounted) return;
      setState(() {
        overwriteCardValue = null;
        globalLoading = false;
        if (receivedParams['registrationType'] == "BIOMETRIC_USER") {
          Navigator.of(context).push(MaterialPageRoute(
            builder: (context) => WriteSuccessfulScreen(navigationParams: {
              "rID": receivedParams['rID']!,
              "consumerDeviceNumber": result.consumerDeviceNumber,
              "registrationType": 'BIOMETRIC_USER',
              "formFactor": "CARD",
            }),
          ));
        } else {
          Navigator.of(context).push(MaterialPageRoute(
            builder: (context) => WritePasscodeScreen(navigationParams: {
              "rID": receivedParams['rID']!,
              "consumerDeviceNumber": result.consumerDeviceNumber,
              "registrationType": 'BASIC_USER',
              "formFactor": "CARD"
            }),
          ));
        }
      });
    } on PlatformException catch (ex) {
      setState(() {
        if (!mounted) return;
        overwriteCardValue = null;
        globalError = "${ex.code}: ${ex.message}";
        globalLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Write Profile'),
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
                    'Part 3: Write Profile',
                    style: TextStyle(fontSize: 20),
                  )),
              const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                  child: Text(
                    'This step calls the writeProfile API method. The kernel will perform a write operation on the card and return a rId.',
                    style: TextStyle(fontSize: 16),
                  )),
              Column(
                mainAxisAlignment: MainAxisAlignment.start,
                children: <Widget>[
                  ListTile(
                    title: const Text('Write Profile on a Card'),
                    leading: Radio(
                      value: 1,
                      groupValue: selectedOption,
                      onChanged: (value) {
                        setState(() {
                          selectedOption = value!;
                        });
                      },
                    ),
                  ),
                  ListTile(
                    title: const Text('Write Profile to a QR Code'),
                    leading: Radio(
                      value: 2,
                      groupValue: selectedOption,
                      onChanged: (value) {
                        setState(() {
                          selectedOption = value!;
                        });
                      },
                    ),
                  ),
                ],
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
                                  if (selectedOption == 1) {
                                    showCardAlert(context);
                                  } else {
                                    //
                                    // Navigator.of(context)
                                    //     .push(MaterialPageRoute(
                                    //   builder: (context) =>
                                    //       WriteSuccessfulScreen(
                                    //           navigationParams: {
                                    //         "rID": receivedParams['rID']!,
                                    //         "consumerDeviceNumber": "abc",
                                    //         "formFactor": "QR",
                                    //       }),
                                    // ));
                                    //
                                    getGenerateCpUserProfile(
                                        _reliantAppGuid,
                                        _programGuid,
                                        receivedParams['rID']!,
                                        receivedParams['passcode']);
                                  }
                                }),
                          child: selectedOption == 1
                              ? const Text('Write Profile on a Card')
                              : const Text('Write Profile to a QR Code')))),
            ]));
  }

  void showCardAlert(BuildContext context) {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
              title: const Text('Overwrite Card'),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                mainAxisAlignment: MainAxisAlignment.start,
                children: <Widget>[
                  ListTile(
                    title: const Text('Yes'),
                    leading: Radio(
                      value: 1,
                      groupValue: overwriteCardValue,
                      onChanged: (value) {
                        setState(() {
                          Navigator.pop(context);
                          overwriteCardValue = value!;
                          getWriteProfile(_reliantAppGuid, _programGuid,
                              receivedParams['rID']!, true);
                        });
                      },
                    ),
                  ),
                  ListTile(
                    title: const Text('No'),
                    leading: Radio(
                      value: 2,
                      groupValue: overwriteCardValue,
                      onChanged: (value) {
                        setState(() {
                          Navigator.pop(context);
                          overwriteCardValue = value!;
                          getWriteProfile(_reliantAppGuid, _programGuid,
                              receivedParams['rID']!, false);
                        });
                      },
                    ),
                  ),
                ],
              ),
            ));
  }
}

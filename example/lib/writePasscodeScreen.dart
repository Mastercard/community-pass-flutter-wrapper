import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/writeSuccessfulScreen.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class WritePasscodeScreen extends StatefulWidget {
  Map<String, String> navigationParams;
  WritePasscodeScreen({super.key, required this.navigationParams});

  @override
  State<WritePasscodeScreen> createState() =>
      _WritePasscodeScreenState(navigationParams);
}

class _WritePasscodeScreenState extends State<WritePasscodeScreen>
    with TickerProviderStateMixin {
  Map<String, String> receivedParams;
  _WritePasscodeScreenState(this.receivedParams);

  final myController = TextEditingController();
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
    // Clean up the controller when the widget is disposed.
    myController.dispose();
    controller.dispose();
    super.dispose();
  }

  final _communityPassFlutterplugin = CommunityPassApi();
  static final String _reliantAppGuid = dotenv.env['RELIANT_APP_GUID'] ?? '';
  static final String _programGuid = dotenv.env['PROGRAM_GUID'] ?? '';

  String globalError = '';
  bool globalLoading = false;

  Future<void> getWritePasscode(String reliantGUID, String programGUID,
      String rID, String passcode) async {
    if (mounted) {
      setState(() {
        globalLoading = true;
      });
    }

    WritePasscodeResult result;

    try {
      result = await _communityPassFlutterplugin.getWritePasscode(
          reliantGUID, programGUID, rID, passcode);

      if (!mounted) return;
      if (result.responseStatus == ResponseStatus.SUCCESS) {
        setState(() {
          globalLoading = false;
          Navigator.of(context).push(MaterialPageRoute(
            builder: (context) => WriteSuccessfulScreen(navigationParams: {
              "rID": receivedParams['rID']!,
              "consumerDeviceNumber": receivedParams['consumerDeviceNumber']!,
            }),
          ));
        });
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
          title: const Text('Write Passcode'),
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
                    'Part 4: Write Passcode',
                    style: TextStyle(fontSize: 20),
                  )),
              const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                  child: Text(
                    'This step calls the getWritePasscode API used to write a Passcode to the card. This is initiated by the Reliant Application to CPK after a successful user registration.',
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
                  // height: 100,
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
                                  getWritePasscode(
                                      _reliantAppGuid,
                                      _programGuid,
                                      receivedParams['rID']!,
                                      myController.text.toString());
                                }),
                          child: const Text('Start registration')))),
            ]));
  }
}

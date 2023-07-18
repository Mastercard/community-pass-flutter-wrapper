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
  bool overwriteCardValue = false;

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
        globalLoading = false;
        if (receivedParams['registrationType'] == "BIOMETRIC_USER") {
          Navigator.of(context).push(MaterialPageRoute(
            builder: (context) => WriteSuccessfulScreen(navigationParams: {
              "rID": receivedParams['rID']!,
              "consumerDeviceNumber": result.consumerDeviceNumber,
            }),
          ));
        } else {
          Navigator.of(context).push(MaterialPageRoute(
            builder: (context) => WritePasscodeScreen(navigationParams: {
              "rID": receivedParams['rID']!,
              "consumerDeviceNumber": result.consumerDeviceNumber,
              "registrationType": 'BASIC_USER',
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Write Profile on Card'),
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
                    'Part 3: Write Card',
                    style: TextStyle(fontSize: 20),
                  )),
              const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                  child: Text(
                    'This step calls the writeProfile API method. The kernel will perform a write operation on the card and return a rId.',
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
              Padding(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 0, vertical: 5),
                  child: CheckboxListTile(
                    title: const Text("Overwrite card"),
                    value: overwriteCardValue,
                    activeColor: mastercardOrange,
                    onChanged: (newValue) {
                      setState(() {
                        overwriteCardValue = newValue!;
                      });
                    },
                    controlAffinity: ListTileControlAffinity
                        .leading, //  <-- leading Checkbox
                  )),
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
                                  getWriteProfile(
                                      _reliantAppGuid,
                                      _programGuid,
                                      receivedParams['rID']!,
                                      overwriteCardValue);
                                }),
                          child: const Text('Write Profile on Card')))),
            ]));
  }
}

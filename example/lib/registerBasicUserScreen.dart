import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/writeProfileScreen.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class RegisterBasicUserScreen extends StatefulWidget {
  const RegisterBasicUserScreen({super.key});

  @override
  State<RegisterBasicUserScreen> createState() =>
      _RegisterBasicUserScreenState();
}

class _RegisterBasicUserScreenState extends State<RegisterBasicUserScreen>
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

  Future<void> getRegisterBasicUser(
      String reliantGUID, String programGUID) async {
    if (mounted) {
      setState(() {
        globalLoading = true;
      });
    }
    RegisterBasicUserResult result;

    try {
      result = await _communityPassFlutterplugin.getRegisterBasicUser(
          reliantGUID, programGUID, '');

      if (!mounted) return;
      setState(() {
        globalLoading = false;
        Navigator.of(context).push(MaterialPageRoute(
            builder: (context) => WriteProfileScreen(navigationParams: {
                  "rID": result.rID,
                  "registrationType": 'BASIC_USER',
                })));
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
          title: const Text('Basic user Registration'),
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
                    'Part 2: Register Basic User',
                    style: TextStyle(fontSize: 20),
                  )),
              const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                  child: Text(
                    'This step calls the getRegisterBasicUser API method and returns a rId.',
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
                                  getRegisterBasicUser(
                                      _reliantAppGuid, _programGuid);
                                }),
                          child: const Text('Start registration')))),
            ]));
  }
}

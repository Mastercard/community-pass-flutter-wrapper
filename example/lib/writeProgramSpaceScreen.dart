import 'package:compass_library_wrapper_plugin_example/utils.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class WriteProgramSpaceScreen extends StatefulWidget {
  Map<String, String> navigationParams;
  WriteProgramSpaceScreen({super.key, required this.navigationParams});

  @override
  State<WriteProgramSpaceScreen> createState() =>
      _WriteProgramSpaceScreenState(navigationParams);
}

class _WriteProgramSpaceScreenState extends State<WriteProgramSpaceScreen>
    with TickerProviderStateMixin {
  Map<String, String> receivedParams;
  _WriteProgramSpaceScreenState(this.receivedParams);

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

  Future<void> getWriteProgramSpace(String reliantGUID, String programGUID,
      String rID, String programSpaceData, bool encryptData) async {
    if (mounted) {
      setState(() {
        globalLoading = true;
      });
    }
    WriteProgramSpaceResult result;

    try {
      result = await _communityPassFlutterplugin.getWriteProgramSpace(
          reliantGUID, programGUID, rID, programSpaceData, encryptData);
      if (!mounted) return;
      setState(() {
        globalLoading = false;
        debugPrint(result.isSuccess.toString());
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
          title: const Text('Write Program Space'),
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
                    'Part 2: Write Program Space',
                    style: TextStyle(fontSize: 20),
                  )),
              const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                  child: Text(
                    'This step calls the getWriteProgramSpace API method and returns an isSuccess property.',
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
                                  getWriteProgramSpace(
                                      _reliantAppGuid,
                                      _programGuid,
                                      receivedParams["rID"]!,
                                      programSpaceData,
                                      true);
                                }),
                          child: const Text('Write Program Space')))),
            ]));
  }
}

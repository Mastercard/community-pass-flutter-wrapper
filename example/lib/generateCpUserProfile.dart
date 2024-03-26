import 'dart:io';
import 'dart:ui' as ui;
import 'package:path_provider/path_provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:share_plus/share_plus.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/main.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin_example/writePasscodeScreen.dart';
import 'package:compass_library_wrapper_plugin_example/writeSuccessfulScreen.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class GenerateCpUserProfileScreen extends StatefulWidget {
  Map<String, String> navigationParams;
  GenerateCpUserProfileScreen({super.key, required this.navigationParams});

  @override
  State<GenerateCpUserProfileScreen> createState() =>
      _GenerateCpUserProfileScreenState(navigationParams);
}

class _GenerateCpUserProfileScreenState
    extends State<GenerateCpUserProfileScreen> {
  Map<String, String> receivedParams;
  _GenerateCpUserProfileScreenState(this.receivedParams);

  final _communityPassFlutterplugin = CommunityPassApi();
  final GlobalKey globalKey = GlobalKey();
  static final String _reliantAppGuid = dotenv.env['RELIANT_APP_GUID'] ?? '';
  static final String _programGuid = dotenv.env['PROGRAM_GUID'] ?? '';

  String globalError = '';
  bool globalLoading = false;
  String? passcode;
  String rID = '';
  String formFactor = '';

  @override
  void initState() {
    formFactor = receivedParams['formFactor']!;
    passcode = receivedParams['passcode'];
    rID = receivedParams['rID']!;
    super.initState();
  }

  Future<void> captureAndSharePng() async {
    RenderRepaintBoundary? boundary =
        globalKey.currentContext!.findRenderObject() as RenderRepaintBoundary?;
    ui.Image image = await boundary!.toImage();
    ByteData? byteData = await image.toByteData(format: ui.ImageByteFormat.png);
    Uint8List pngBytes = byteData!.buffer.asUint8List();

    final Directory tempDir = await getTemporaryDirectory();
    final file =
        await File('${tempDir.path}/community-pass-qr-code.png').create();
    await file.writeAsBytes(pngBytes);
    await Share.shareXFiles([XFile(file.path)], text: 'Community Pass QR Code');
    file.delete();
  }

  Future<void> getGenerateCpUserProfile(String reliantGUID, String programGUID,
      String rID, String passcode) async {
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
          title: const Text('Registraton Successful'),
          backgroundColor: mastercardOrange,
        ),
        body: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: receivedParams["formFactor"] == "QR"
                ? [
                    const Padding(
                        padding:
                            EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                        child: Text(
                          'User registration was successful!',
                          style:
                              TextStyle(fontSize: 20, color: mastercardOrange),
                        )),
                    Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 20, vertical: 2),
                        child: Text(
                          'rID: $rID',
                          style: const TextStyle(fontSize: 16),
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
                                onPressed: (() {
                                  Navigator.of(context).push(MaterialPageRoute(
                                    builder: (context) => const MyApp(),
                                  ));
                                }),
                                child: const Text('Go Back Home')))),
                  ]
                : [
                    const Padding(
                        padding:
                            EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                        child: Text(
                          'User registration was successful!',
                          style:
                              TextStyle(fontSize: 20, color: mastercardOrange),
                        )),
                    Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 20, vertical: 2),
                        child: Text(
                          'rID: $rID',
                          style: const TextStyle(fontSize: 16),
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
                                onPressed: (() {
                                  Navigator.of(context).push(MaterialPageRoute(
                                    builder: (context) => const MyApp(),
                                  ));
                                }),
                                child: const Text('Go Back Home')))),
                  ]));
  }

  void showQrCodeAlert(BuildContext context) {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
              title: const Text('Issue a QR Code'),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                mainAxisAlignment: MainAxisAlignment.start,
                children: <Widget>[
                  Container(
                      height: 600,
                      width: 400,
                      child: RepaintBoundary(
                          key: globalKey,
                          child: QrImageView(
                            data: receivedParams["rID"]!,
                            version: QrVersions.auto,
                            size: 250.0,
                            gapless: false,
                            errorStateBuilder: (cxt, err) {
                              return Container(
                                child: const Center(
                                  child: Text(
                                    'Uh oh! Something went wrong...',
                                    textAlign: TextAlign.center,
                                  ),
                                ),
                              );
                            },
                            eyeStyle: const QrEyeStyle(
                              eyeShape: QrEyeShape.square,
                              color: mastercardOrange,
                            ),
                            dataModuleStyle: const QrDataModuleStyle(
                              dataModuleShape: QrDataModuleShape.square,
                              color: mastercardOrange,
                            ),
                          ))),
                ],
              ),
              actions: <Widget>[
                TextButton(
                  onPressed: () => {
                    setState(() {
                      Navigator.pop(context);
                      captureAndSharePng();
                    })
                  },
                  child: const Text(
                    'Print QR Code',
                    style: TextStyle(color: mastercardOrange),
                  ),
                ),
              ],
            ));
  }
}

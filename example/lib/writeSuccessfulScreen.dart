import 'dart:io';
import 'dart:typed_data';
import 'dart:ui' as ui;
import 'package:path_provider/path_provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:share_plus/share_plus.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/main.dart';

class WriteSuccessfulScreen extends StatefulWidget {
  Map<String, String> navigationParams;
  WriteSuccessfulScreen({super.key, required this.navigationParams});

  @override
  State<WriteSuccessfulScreen> createState() =>
      _WriteSuccessfulScreenState(navigationParams);
}

class _WriteSuccessfulScreenState extends State<WriteSuccessfulScreen> {
  Map<String, String> receivedParams;
  _WriteSuccessfulScreenState(this.receivedParams);

  final GlobalKey globalKey = GlobalKey();

  String consumerDeviceNumber = '';
  String rID = '';

  @override
  void initState() {
    consumerDeviceNumber = receivedParams['consumerDeviceNumber']!;
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
                          'Consumer Device Number: $consumerDeviceNumber',
                          style: const TextStyle(fontSize: 16),
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
                          'Consumer Device Number: $consumerDeviceNumber',
                          style: const TextStyle(fontSize: 16),
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

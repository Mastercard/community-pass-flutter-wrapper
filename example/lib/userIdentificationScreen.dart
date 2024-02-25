import 'package:flutter/material.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/registerBasicUserScreen.dart';
import 'package:compass_library_wrapper_plugin_example/registerUserWithBiometricsScreen.dart';
import 'package:flutter/services.dart';
import 'package:compass_library_wrapper_plugin/compassapi.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:simple_barcode_scanner/enum.dart';
import 'package:simple_barcode_scanner/simple_barcode_scanner.dart';

import 'main.dart';

class UserIdentificationConsentScreen extends StatefulWidget {
  const UserIdentificationConsentScreen({super.key});

  @override
  State<UserIdentificationConsentScreen> createState() =>
      _UserIdentificationConsentScreenState();
}

class _UserIdentificationConsentScreenState
    extends State<UserIdentificationConsentScreen>
    with TickerProviderStateMixin {
  final _communityPassFlutterplugin = CommunityPassApi();
  static final String _reliantAppGuid = dotenv.env['RELIANT_APP_GUID'] ?? '';
  static final String _programGuid = dotenv.env['PROGRAM_GUID'] ?? '';

  List<String> listOfModalities = ["FACE", "LEFT_PALM", "RIGHT_PALM"];
  String globalError = '';
  bool globalLoading = false;
  bool isDialogOpen = false;
  FormFactor _formFactor = FormFactor.NONE;
  String? scannedQrBase64;

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

  Future<void> launchQRCamera() async {
    try {
      setState(() async {
        globalLoading = true;
        var res = await Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const SimpleBarcodeScannerPage(
                key: Key("ScanrQR"),
                lineColor: "#FF5F00",
                cancelButtonText: "Cancel",
                isShowFlashIcon: true,
                appBarTitle: "Community Pass Service",
                scanType: ScanType.qr,
                centerTitle: true,
              ),
            ));
        setState(() {
          if (res is String) {
            scannedQrBase64 = res;
            Future.delayed(Duration.zero, () {
              showAlert(
                  context,
                  "QR scanned successfully!",
                  "You may proceed to capture the user's biometrics!",
                  "Capture Biometrics",
                  () => {
                        Navigator.of(context, rootNavigator: true)
                            .pop('dialog'),
                        identifyUser(_reliantAppGuid, _programGuid,
                            listOfModalities, true, res, _formFactor)
                      });
            });
          }
          globalLoading = false;
        });
      });
    } on PlatformException catch (ex) {
      if (!mounted) return;
      setState(() {
        globalError = "${ex.code}: ${ex.message}";
        globalLoading = false;
      });
    }
  }

  Future<void> identifyUser(
      String reliantGUID,
      String programGUID,
      List<String> modalities,
      bool cacheHashesIfIdentified,
      String? qrBase64,
      FormFactor formFactor) async {
    if (mounted) {
      globalLoading = true;
    }

    UserIdentificationResult result;
    try {
      result = await _communityPassFlutterplugin.getUserIdentification(
          reliantGUID,
          programGUID,
          modalities,
          cacheHashesIfIdentified,
          qrBase64,
          formFactor);

      // check whether the state is mounted on the tree
      if (!mounted) return;

      if (result.isMatchFound) {
        setState(() {
          globalLoading = false;
        });
        Future.delayed(Duration.zero, () {
          showAlert(
              context,
              "Match found!",
              "User exists in the records!\nrID: ${result.rID}",
              "Okay, Go back home.",
              () => Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) => const MyApp(),
                  )));
        });
      } else {
        setState(() {
          globalLoading = false;
        });
        Future.delayed(Duration.zero, () {
          showAlert(
            context,
            "Match not found!",
            "User does not exists in the records!",
            "Cancel and Retry",
            () => Navigator.of(context, rootNavigator: true).pop('dialog'),
          );
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
          title: const Text('User Identification'),
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
                        style:
                            const TextStyle(fontSize: 12, color: mastercardRed),
                      )
                    : null),
            const Padding(
                padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                child: Text(
                  'Part 1: Identify a User',
                  style: TextStyle(fontSize: 20),
                )),
            const Padding(
                padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                child: Text(
                  'This step calls the getUserIdentification API method. In this step, we will capture a user biometric and identify them using a 1:1 match in the kernel.',
                  style: TextStyle(fontSize: 16),
                )),
            const Padding(
                padding: EdgeInsets.symmetric(horizontal: 20, vertical: 10),
                child: Text(
                  'Please select biometric modalities below:',
                  style: TextStyle(fontSize: 16),
                )),
            Row(children: [
              TextButton(
                  onPressed: () {
                    if (listOfModalities.contains("FACE")) {
                      listOfModalities.remove("FACE");
                    } else {
                      listOfModalities.add("FACE");
                    }
                  },
                  child: Row(
                    children: [
                      Checkbox(
                        value: listOfModalities.contains("FACE"),
                        activeColor: mastercardOrange,
                        onChanged: (newValue) {
                          if (newValue == true) {
                            listOfModalities.add("FACE");
                          } else {
                            listOfModalities.remove("FACE");
                          }
                        },
                      ),
                      const Text("Face", style: TextStyle(color: Colors.black)),
                    ],
                  )),
              TextButton(
                  onPressed: () {
                    if (listOfModalities.contains("LEFT_PALM")) {
                      listOfModalities.remove("LEFT_PALM");
                    } else {
                      listOfModalities.add("LEFT_PALM");
                    }
                  },
                  child: Row(
                    children: [
                      Checkbox(
                        value: listOfModalities.contains("LEFT_PALM"),
                        activeColor: mastercardOrange,
                        onChanged: (newValue) {
                          if (newValue == true) {
                            listOfModalities.add("LEFT_PALM");
                          } else {
                            listOfModalities.remove("LEFT_PALM");
                          }
                        },
                      ),
                      const Text(
                        "Left Palm",
                        style: TextStyle(color: Colors.black),
                      ),
                    ],
                  )),
              TextButton(
                  onPressed: () {
                    if (listOfModalities.contains("RIGHT_PALM")) {
                      listOfModalities.remove("RIGHT_PALM");
                    } else {
                      listOfModalities.add("RIGHT_PALM");
                    }
                  },
                  child: Row(
                    children: [
                      Checkbox(
                        value: listOfModalities.contains("RIGHT_PALM"),
                        activeColor: mastercardOrange,
                        onChanged: (newValue) {
                          if (newValue == true) {
                            listOfModalities.add("RIGHT_PALM");
                          } else {
                            listOfModalities.remove("RIGHT_PALM");
                          }
                        },
                      ),
                      const Text("Right Palm",
                          style: TextStyle(color: Colors.black)),
                    ],
                  ))
            ]),
            const Padding(
                padding: EdgeInsets.symmetric(horizontal: 20, vertical: 10),
                child: Text(
                  'Please select a form factor below:',
                  style: TextStyle(fontSize: 16),
                )),
            Row(children: [
              TextButton(
                  onPressed: () {
                    _formFactor = FormFactor.CARD;
                  },
                  child: Row(
                    children: [
                      Radio(
                        value: FormFactor.CARD,
                        groupValue: _formFactor,
                        activeColor: mastercardOrange,
                        onChanged: (FormFactor? value) {
                          setState(() {
                            _formFactor = value!;
                          });
                        },
                      ),
                      const Text("CARD", style: TextStyle(color: Colors.black)),
                    ],
                  )),
              TextButton(
                  onPressed: () {
                    _formFactor = FormFactor.QR;
                  },
                  child: Row(
                    children: [
                      Radio(
                        value: FormFactor.QR,
                        groupValue: _formFactor,
                        activeColor: mastercardOrange,
                        onChanged: (FormFactor? value) {
                          setState(() {
                            _formFactor = value!;
                          });
                        },
                      ),
                      const Text("QR", style: TextStyle(color: Colors.black)),
                    ],
                  )),
              TextButton(
                  onPressed: () {
                    _formFactor = FormFactor.NONE;
                  },
                  child: Row(
                    children: [
                      Radio(
                        value: FormFactor.NONE,
                        groupValue: _formFactor,
                        activeColor: mastercardOrange,
                        onChanged: (FormFactor? value) {
                          setState(() {
                            _formFactor = value!;
                          });
                        },
                      ),
                      const Text("NONE", style: TextStyle(color: Colors.black)),
                    ],
                  )),
            ]),
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
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                    flex: 1,
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
                                    if (_formFactor == FormFactor.QR &&
                                        scannedQrBase64 == null) {
                                      launchQRCamera();
                                    } else if (_formFactor == FormFactor.QR &&
                                        scannedQrBase64 != null) {
                                      identifyUser(
                                          _reliantAppGuid,
                                          _programGuid,
                                          listOfModalities,
                                          true,
                                          scannedQrBase64,
                                          _formFactor);
                                    } else {
                                      identifyUser(
                                          _reliantAppGuid,
                                          _programGuid,
                                          listOfModalities,
                                          true,
                                          null,
                                          _formFactor);
                                    }
                                  }),
                            child: const Text('Identify a user 1:1')))),
              ],
            )
          ],
        ));
  }

  void showAlert(BuildContext context, String title, String description,
      String buttonText, void Function() pushCallback) {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
              title: Text(title),
              content: Text(description),
              actions: <Widget>[
                TextButton(
                  onPressed: () => pushCallback(),
                  child: Text(
                    buttonText,
                    style: const TextStyle(color: Colors.green),
                  ),
                ),
              ],
            ));
  }
}

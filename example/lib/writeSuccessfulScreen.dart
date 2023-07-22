import 'package:flutter/material.dart';
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

  String consumerDeviceNumber = '';
  String rID = '';

  @override
  void initState() {
    consumerDeviceNumber = receivedParams['consumerDeviceNumber']!;
    rID = receivedParams['rID']!;
    super.initState();
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
            children: [
              const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                  child: Text(
                    'User registration was successful!',
                    style: TextStyle(fontSize: 20, color: mastercardOrange),
                  )),
              Padding(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 20, vertical: 2),
                  child: Text(
                    'Consumer Device Number: $consumerDeviceNumber',
                    style: const TextStyle(fontSize: 16),
                  )),
              Padding(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 20, vertical: 2),
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
}

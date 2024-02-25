import 'package:compass_library_wrapper_plugin_example/compassConsentScreen.dart';
import 'package:compass_library_wrapper_plugin_example/sharedSpaceScreen.dart';
import 'package:flutter/material.dart';
import 'package:compass_library_wrapper_plugin_example/biometricConsentScreen.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/reusableCardWidget.dart';

class PreTransactionScreen extends StatelessWidget {
  const PreTransactionScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Pre-Transactions Phase'),
        backgroundColor: mastercardOrange,
      ),
      body: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: <Widget>[
            const Spacer(),
            CardWidgetStateless(
              onClick: () {
                Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) => const CompassConsentScreenScreen()));
              },
              cardLabel: 'Action',
              title: 'Enrol a New User',
              description: 'Enroll a user using either biometrics or passcode',
              cardIcon: const Icon(
                Icons.person_add,
                size: 30,
              ),
            ),
            CardWidgetStateless(
              onClick: () {
                // Utils.displayToast('Shared Space has not yet been implemented');
                Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) => const SharedSpaceScreen()));
              },
              cardLabel: 'Action',
              title: 'Use the Shared Space',
              description: 'Sync data between the POI and the Card',
              cardIcon: const Icon(
                Icons.share,
                size: 30,
              ),
            ),
            const Spacer(),
          ],
        ),
      ),
    );
  }
}

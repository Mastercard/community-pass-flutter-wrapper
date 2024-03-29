import 'package:compass_library_wrapper_plugin_example/registrationDataScreen.dart';
import 'package:compass_library_wrapper_plugin_example/scanCommunityPassQR.dart';
import 'package:compass_library_wrapper_plugin_example/userIdentificationScreen.dart';
import 'package:flutter/material.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/reusableCardWidget.dart';
import 'package:compass_library_wrapper_plugin_example/utils.dart';

class TransactionScreen extends StatelessWidget {
  const TransactionScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Transactions Phase'),
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
                    builder: (context) =>
                        RegistrationDataScreen(navigationParams: const {
                          "flag": "AUTH",
                        })));
              },
              cardLabel: 'Action',
              title: 'Authenticate a user with card',
              description:
                  'Authenticate a user with a card using either biometrics or passcode',
              cardIcon: const Icon(
                Icons.person_add,
                size: 30,
              ),
            ),
            CardWidgetStateless(
              onClick: () {
                // Utils.displayToast('This action has not yet been implemented');
                Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) => const ScanCommunityPassQRScreen()));
              },
              cardLabel: 'Action',
              title: 'Authenticate a user with QR',
              description:
                  'Authenticate a user with a QR using either biometrics or passcode',
              cardIcon: const Icon(
                Icons.share,
                size: 30,
              ),
            ),
            CardWidgetStateless(
              onClick: () {
                // Utils.displayToast('This action has not yet been implemented');
                Navigator.of(context).push(MaterialPageRoute(
                    builder: (context) =>
                        const UserIdentificationConsentScreen()));
              },
              cardLabel: 'Action',
              title: 'Identify a user 1:1',
              description:
                  'Authenticate a user with a CARD, QR ot No Form Factor using biometrics',
              cardIcon: const Icon(
                Icons.person_pin_outlined,
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

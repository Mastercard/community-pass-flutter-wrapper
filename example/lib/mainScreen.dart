import 'package:compass_library_wrapper_plugin_example/transactionsPhaseScreen.dart';
import 'package:flutter/material.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/pretransactionsPhaseScreen.dart';
import 'package:compass_library_wrapper_plugin_example/reusableCardWidget.dart';
import 'package:compass_library_wrapper_plugin_example/utils.dart';

class MainScreen extends StatelessWidget {
  const MainScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text(
            'Community Pass Flutter Reliant Application',
            style: TextStyle(color: Colors.white),
          ),
          backgroundColor: mastercardOrange,
        ),
        body: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            children: <Widget>[
              const Spacer(),
              CardWidgetStateless(
                cardLabel: 'Section',
                title: 'Pre-Transactions Phase',
                description:
                    'You will complete user setup and on-boarding in this phase',
                cardIcon: const Icon(
                  Icons.logout_outlined,
                  size: 30,
                ),
                onClick: () {
                  Navigator.of(context).push(MaterialPageRoute(
                      builder: (context) => const PreTransactionScreen()));
                },
              ),
              CardWidgetStateless(
                cardLabel: 'Section',
                title: 'Transactions Phase',
                description:
                    'You will conduct a life transaction during this phase',
                cardIcon: const Icon(
                  Icons.repeat_on_outlined,
                  size: 30,
                ),
                onClick: () {
                  Navigator.of(context).push(MaterialPageRoute(
                      builder: (context) => const TransactionScreen()));
                },
              ),
              CardWidgetStateless(
                cardLabel: 'Section',
                title: 'Admin-Transactions Phase',
                description:
                    'You can make changes to a user profile in this phase',
                cardIcon: const Icon(
                  Icons.shield_outlined,
                  size: 30,
                ),
                onClick: () {
                  Utils.displayToast(
                      'Admin-Transactions phase has not yet been implemented');
                },
              ),
              const Spacer(),
            ],
          ),
        ),
      ),
    );
  }
}

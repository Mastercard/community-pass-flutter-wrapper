import 'package:compass_library_wrapper_plugin_example/ReadProgramSpaceScreen.dart';
import 'package:compass_library_wrapper_plugin_example/registrationDataScreen.dart';
import 'package:compass_library_wrapper_plugin_example/writeProgramSpaceScreen.dart';
import 'package:flutter/material.dart';
import 'package:compass_library_wrapper_plugin_example/color_utils.dart';
import 'package:compass_library_wrapper_plugin_example/reusableCardWidget.dart';

class SharedSpaceScreen extends StatelessWidget {
  const SharedSpaceScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Shared Space Operations'),
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
                    "flag": 'READ_PROGRAM',
                  }),
                ));
              },
              cardLabel: 'Action',
              title: 'Read Program Space',
              description:
                  'Read existing data from the program space on in community pass card',
              cardIcon: const Icon(
                Icons.person_add,
                size: 30,
              ),
            ),
            CardWidgetStateless(
              onClick: () {
                Navigator.of(context).push(MaterialPageRoute(
                  builder: (context) =>
                      RegistrationDataScreen(navigationParams: const {
                    "flag": 'WRITE_PROGRAM',
                  }),
                ));
              },
              cardLabel: 'Action',
              title: 'Write Program Space',
              description:
                  'Store data to the program space in a community pass card',
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

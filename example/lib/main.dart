import 'package:flutter/material.dart';
import 'package:flutter_cpk_plugin_example/mainScreen.dart';
import 'color_utils.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        title: 'Community Pass Flutter Reliant Application',
        theme: ThemeData(primaryColor: mastercardOrange),
        home: const MainScreen());
  }
}

import 'dart:async';

import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

import 'color_utils.dart';

class Utils {
  static Future<bool?> displayToast(String message) {
    return Fluttertoast.showToast(
        msg: message,
        toastLength: Toast.LENGTH_SHORT,
        gravity: ToastGravity.BOTTOM,
        backgroundColor: mastercardOrange,
        textColor: Colors.white,
        fontSize: 14.0);
  }

  static SnackBar displaySnackBar(String message,
      {String? actionMessage, required VoidCallback onClick}) {
    return SnackBar(
      content: Text(
        message,
        style: const TextStyle(color: Colors.white, fontSize: 14.0),
      ),
      action: (actionMessage != null)
          ? SnackBarAction(
              textColor: Colors.white,
              label: actionMessage,
              onPressed: () {
                return onClick();
              },
            )
          : null,
      duration: const Duration(seconds: 2),
      backgroundColor: mastercardOrange,
    );
  }
}

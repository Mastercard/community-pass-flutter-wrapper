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

const programSpaceData = '''
{
  "collectionInfo": {
    "fifthCollection": {
      "amount": 1900.0,
      "amountPerGram": 400.0,
      "id": "5",
      "produceId": "975",
      "produceName": "Maize",
      "timeStamp": 1681382463864,
      "weightInGrams": 1500
    },
    "firstCollection": {
      "amount": 1200.0,
      "amountPerGram": 300.0,
      "id": "1",
      "produceId": "316",
      "produceName": "Beans",
      "timeStamp": 1681382463864,
      "weightInGrams": 3676
    },
    "fourthCollection": {
      "amount": 3000.0,
      "amountPerGram": 100.0,
      "id": "4",
      "produceId": "425",
      "produceName": "Peas",
      "timeStamp": 1681382463864,
      "weightInGrams": 2199
    },
    "lastAmountPaidOut": 700.0,
    "lastPayoutTimeStamp": 1681382463863,
    "noOfPaidCollections": 2,
    "secondCollection": {
      "amount": 4556.0,
      "amountPerGram": 200.0,
      "id": "2",
      "produceId": "432",
      "produceName": "Oats",
      "timeStamp": 1681382463864,
      "weightInGrams": 1000
    },
    "thirdCollection": {
      "amount": 1400.0,
      "amountPerGram": 600.0,
      "id": "3",
      "produceId": "534",
      "produceName": "Potatoes",
      "timeStamp": 1681382463864,
      "weightInGrams": 1300
    },
    "totalAmountPaid": 6000.0
  },
  "user": {
    "address": "Street 123",
    "age": 23,
    "consentId": "123456",
    "consumerDeviceId": "123456789",
    "firstName": "John",
    "identifier": "144",
    "lastName": "Doe",
    "mobileNumber": "11234245343",
    "rId": "1fa97fd3b394609528520301d04860c4d53e620b"
  }
}
''';

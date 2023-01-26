### Prerequisites <a name="prerequisites"></a>

- [Flutter](https://docs.flutter.dev/get-started/install) installed and working.
- Android Studio IDE or equivalent
- Android 7.0 (Nougat) or later
- minSdkVersion 24 or later
- Android Gradle Plugin v3.4.0 or greater required
- Gradle 5.1.1 or greater required

### Configuration <a name="configuration"></a>

To get started please visit our documentation at [Mastercard Developer Zone](https://developer.mastercard.com/cp-kernel-integration-api/tutorial/getting-started-guide/) and complete the following sections

- [Section 1:](https://developer.mastercard.com/cp-kernel-integration-api/tutorial/getting-started-guide/step1) Pre-requisites required to get you starting
- [Section 2:](https://developer.mastercard.com/cp-kernel-integration-api/tutorial/getting-started-guide/step2) Setting up your Community Pass Approved Device
- [Section 3:](https://developer.mastercard.com/cp-kernel-integration-api/tutorial/getting-started-guide/step3) Setting up your development environment
- [Section 4:](https://developer.mastercard.com/cp-kernel-integration-api/tutorial/getting-started-guide/step4) Submit your Reliant App details to Community Pass
- [Section 6:](https://developer.mastercard.com/cp-kernel-integration-api/tutorial/getting-started-guide/step6) Install and Activate the Community Pass Kernel

To report any issue about the [Mastercard Developer Zone](https://developer.mastercard.com/cp-kernel-integration-api/tutorial/getting-started-guide/), please send an email to `cp.patnerprogram[at]mastercard.com`

For issues related with this plugin, please use github issues to report it.

### Installation <a name="installation"></a>

The following steps will help you to connect your reliant application with Community Pass

1. Depend on this plugin

Run this command Flutter:

```
flutter pub add flutter_cpk_plugin
```

This will add a line like this to your package's pubspec.yaml (and run an implicit `flutter pub get`):

```yaml
dependencies:
  flutter_cpk_plugin: ^0.0.1
```

Alternatively, your editor might support flutter pub get. Check the docs for your editor to learn more.

2. Import it

Now in your Dart code, you can use:

```dart
import 'package:flutter_cpk_plugin/flutter_cpk_plugin.dart';
```

### Usage Examples <a name="usage-examples"></a>

Create a channel

```dart
final _channel = const MethodChannel('flutter_cpk_plugin');
```

Implement the saveBiometricConsent() method method

```dart
//main.dart

Future<void> saveBiometricConsent(String reliantApplicationGuid, String programGuid) async {
    String result;

    try {

      // invoke saveBiometricConsent() method and await for the consentId
      result = await _channel.invokeMethod('saveBiometricConsent', {
        'RELIANT_APP_GUID': reliantApplicationGuid,
        'PROGRAM_GUID': programGuid
      });

      // catch exceptions
    } on PlatformException catch(e) {
      result = '';
    }

    // check whether this [state] object is currently in a tree
    if (!mounted) return;

    // save the consentId in dart state
    setState(() {
      _consentId = result;
    });

  }
```

Invoke the saveBiometricConsent() method

```
saveBiometricConsent(_reliantApplicationGuid, _programGuid);
```
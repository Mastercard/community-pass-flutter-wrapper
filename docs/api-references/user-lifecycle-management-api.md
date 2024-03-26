# User Lifecycle Management API

## 1 User Registration

### 1.1 saveBiometricConsent

This API is used to save the user consent prior to collecting the biometric(s) and generating a unique digital identity. This is a blocking call, therefore it is advised to perform on a non-UI thread.

```
NOTE: It is the responsibility of the Reliant Application to show and capture the user’s consent.
Then, the Reliant Application must store it with CPK.
```

**Compatibility**
| **Available as of CPK version #** | **Deprecated as of CPK version #** |
|--------------------------------------------------|------------------------------------------------------------------|
| + CPK 2.4.1 | + n/a |

**Input Parameters**
| **Parameter** | **Type** | **Description** |
|---------------|----------|------------------------------------------------------|
| consentRequest | SaveBiometricConsentParams | An object that contains a reliantGUID, programGUID and consumerConsentValue |

**Response Parameters**
| **Parameter** | **Type** | **Description** |
|-----------------|-----------------|----------------------------------------------------------|
| consentResponse | Promise<[SaveBiometricConsentResult]> | A promise that resolves to an object containing either a consentID and responseStatus fields or an error field. |

**Type Aliases**

```dart
// SaveBiometricConsentParams
String reliantGUID;
String programGUID;
bool consumerConsentValue;

// SaveBiometricConsentResult
class SaveBiometricConsentResult {
  final String consentID;
  final ResponseStatus responseStatus;

  SaveBiometricConsentResult(this.consentID, this.responseStatus);
}

enum ResponseStatus { SUCCESS }
```

**Error codes**

In addition to the [general error codes](https://developer.mastercard.com/cp-kernel-integration-api/documentation/reference-pages/code-and-formats/), below are the error codes that CPK can send as part of the response:

| **Error Code**                                | **Code** | **Description**                                         |
| --------------------------------------------- | -------- | ------------------------------------------------------- |
| ERROR_CODE_REGISTRATION_FAILED                | 600      | Instance registration failed                            |
| ERROR_CODE_PROGRAM_NOT_SUPPORTED              | 300      | Specified Program ID is not supported by CPK            |
| ERROR_CODE_PROGRAM_DOES_NOT_SUPPORT_BIOMETRIC | 301      | Specified Program ID does not support biometric capture |

### 1.1 communityPassConsentWithPreBuiltUI

This step calls uses the communityPassConsentWithPreBuiltUI API method. In this step, a pre-built consent UI is displayed to the user to capture 3 different types of consent. The user can decline or grant all consent. This is a blocking call, therefore it is advised to perform on a non-UI thread.

**Compatibility**
| **Available as of CPK version #** | **Deprecated as of CPK version #** |
|--------------------------------------------------|------------------------------------------------------------------|
| + CPK 2.4.1 | + n/a |

**Input Parameters**
| **Parameter** | **Type** | **Description** |
|---------------|----------|------------------------------------------------------|
| consentRequest | SaveBiometricConsentParams | An object that contains a reliantGUID, programGUID and consumerConsentValue |

**Response Parameters**
| **Parameter** | **Type** | **Description** |
|-----------------|-----------------|----------------------------------------------------------|
| consentResponse | Promise<[CommunityPassConsentScreenResult]> | A promise that resolves to an object containing either a consentID and responseStatus fields or an error field. |

**Type Aliases**

```dart
// Consent UI Params
String reliantGUID,
String programGUID,
ConsentScreenConfig? consentScreenConfig

class ConsentScreenConfig {
  String? partnerPrivacyPolicyTitle;
  String? partnerPrivacyPolicyContent;
  String? partnerPrivacyPolicyExcerptTitle;
  String? partnerPrivacyPolicyExcerptContent;
  String? acceptConsentButtonLabel;
  String? declineConsentButtonLabel;
  bool? enableCommunityPassPrivacyPolicy;
  bool? enableBiometricNotice;
  bool? enablePartnerPrivacyPolicy;
  String? beforeYouProceedText;
  int? beforeYouProceedFontSize;
  int? consentTitleFontSize;
  int? consentContentFontSize;
  int? switchLabelFontSize;
  int? buttonLabelFontSize;
  int? buttonBorderRadius;
  int? buttonHeight;
  DarkThemeColorScheme? darkThemeColorScheme;
  LightThemeColorScheme? lightThemeColorScheme;

  ConsentScreenConfig(
      this.partnerPrivacyPolicyTitle,
      this.partnerPrivacyPolicyContent,
      this.partnerPrivacyPolicyExcerptTitle,
      this.partnerPrivacyPolicyExcerptContent,
      this.acceptConsentButtonLabel,
      this.declineConsentButtonLabel,
      this.enableCommunityPassPrivacyPolicy,
      this.enableBiometricNotice,
      this.enablePartnerPrivacyPolicy,
      this.beforeYouProceedText,
      this.beforeYouProceedFontSize,
      this.consentTitleFontSize,
      this.consentContentFontSize,
      this.switchLabelFontSize,
      this.buttonLabelFontSize,
      this.buttonBorderRadius,
      this.buttonHeight,
      this.darkThemeColorScheme,
      this.lightThemeColorScheme);
}

class DarkThemeColorScheme {
  String primary;
  String onPrimary;
  String primaryContainer;
  String onPrimaryContainer;
  String background;
  String onBackground;
  String tertiaryContainer;

  DarkThemeColorScheme(
      this.primary,
      this.onPrimary,
      this.primaryContainer,
      this.onPrimaryContainer,
      this.background,
      this.onBackground,
      this.tertiaryContainer);
}

class LightThemeColorScheme {
  String primary;
  String onPrimary;
  String primaryContainer;
  String onPrimaryContainer;
  String background;
  String onBackground;
  String tertiaryContainer;

  LightThemeColorScheme(
      this.primary,
      this.onPrimary,
      this.primaryContainer,
      this.onPrimaryContainer,
      this.background,
      this.onBackground,
      this.tertiaryContainer);
}

// Consent UI Result
class CommunityPassConsentScreenResult {
  final ConsentStatus status;
  final ConsentResult? result;

  CommunityPassConsentScreenResult(this.status, this.result);
}

enum ConsentStatus { CONSENT_GRANTED, CONSENT_DENIED }

class ConsentResult {
  final bool? communityPassPrivacyPolicyAccepted;
  final bool? communityPassBiometricNoticeAccepted;
  final bool? partnerPrivacyPolicyAccepted;
  final AdditionalInfo? additionalInfo;

  ConsentResult(
      this.communityPassPrivacyPolicyAccepted,
      this.communityPassBiometricNoticeAccepted,
      this.partnerPrivacyPolicyAccepted,
      this.additionalInfo);
}

class AdditionalInfo {
  final String? consentID;
  final String? responseStatus;

  AdditionalInfo(this.consentID, this.responseStatus);
}
```

**Error codes**

In addition to the [general error codes](https://developer.mastercard.com/cp-kernel-integration-api/documentation/reference-pages/code-and-formats/), below are the error codes that CPK can send as part of the response:

| **Error Code**                                | **Code** | **Description**                                         |
| --------------------------------------------- | -------- | ------------------------------------------------------- |
| ERROR_CODE_REGISTRATION_FAILED                | 600      | Instance registration failed                            |
| ERROR_CODE_PROGRAM_NOT_SUPPORTED              | 300      | Specified Program ID is not supported by CPK            |
| ERROR_CODE_PROGRAM_DOES_NOT_SUPPORT_BIOMETRIC | 301      | Specified Program ID does not support biometric capture |

### 1.2 getRegisterBasicUser

This API is used to register an existing user with their card/CP Consumer Device present.

**Compatibility**
| **Available as of CPK version #** | **Deprecated as of CPK version #** |
|-----------------------------------|------------------------------------|
| + CPK 2.4.1 | + n/a |

**Input Parameters**
| **Parameter** | **Type** | **Description** |
|---------------|----------|------------------------------------------------------|
| registerBasicUserRequest | RegisterBasicUserParams | An object that contains a reliantGUID, programGUID and formFactor |

**Response Parameters**
| **Parameter** | **Type** | **Description** |
|-----------------|-----------------|----------------------------------------------------------|
| registerBasicUserResponse | Promise<[RegisterBasicUserResult]> | A promise that resolves to an object containing either a rID field or an error field. |

**Type Aliases**

```dart
// FormFactor enum
enum FormFactor {
  CARD,
  QR,
  NONE,
}

// RegisterBasicUserParams
String reliantGUID;
String programGUID;
String formFactor; // e.g FormFactor.CARD.name

// RegisterBasicUserResult
class RegisterBasicUserResult {
  final String consentID
  final ResponseStatus responseStatus;

  RegisterBasicUserResult(this.consentID, this.responseStatus);
}

enum ResponseStatus { SUCCESS }
```

**Error codes**

In addition to the [general error codes](https://developer.mastercard.com/cp-kernel-integration-api/documentation/reference-pages/code-and-formats/), below are the error codes that CPK can send as part of the response:

| **Error Code**                                     | **Code** | **Description**                                   |
| -------------------------------------------------- | -------- | ------------------------------------------------- |
| ERROR_CODE_KERNEL_NOT_ACTIVATED                    | 101      | Kernel is not activated                           |
| ERROR_CODE_PROGRAM_NOT_SUPPORTED                   | 300      | Program is not supported by Kernel                |
| ERROR_CODE_INVALID_ARGUMENT                        | 200      | Program GUID is empty                             |
| ERROR_CODE_PROGRAM_DOES_NOT_SUPPORT_CARD           | 303      | Program does not support the card                 |
| ERROR_CODE_CARD_BLACKLISTED                        | 702      | Card is blacklisted                               |
| ERROR_CODE_CARD_NOT_ACTIVE                         | 704      | Card is inactive                                  |
| ERROR_CODE_PROGRAM_DOES_NOT_SUPPORT_QR_FORM_FACTOR | 304      | Specified Program does not support QR form factor |
| ERROR_CODE_INVALID_CP_USER_PROFILE                 | 1355     | Invalid Cp User Profile                           |

### 1.3 getRegisterUserWithBiometrics

This API is used by the Reliant Application to initiate the user registration flow. It returns the Intent object which can be used by the Reliant Application to start the user registration using the user’s biometric data. Following a successful user registration, a user profile is created and associated with a CP Program. If the user’s profile already exists, e.g., the user is already registered in another program, the user’s profile is updated with the new association. Moreover, it enables you to select a formfactor during the registration process i.e. Card, QR or None.

```
Warning: Reliant Application must obtain the consentID first using the saveBiometricConsent API before invoking the user registration flow.
```

**Compatibility**
| **Available as of CPK version #** | **Deprecated as of CPK version #** |
|-----------------------------------|------------------------------------|
| + CPK 2.4.1 | + n/a |

**Input Parameters**
| **Parameter** | **Type** | **Description** |
|---------------|----------|------------------------------------------------------|
| registerUserWithBiometricsRequest | RegisterUserWithBiometricsParams | An object that contains a reliantGUID, programGUID and consentID |

**Response Parameters**
| **Parameter** | **Type** | **Description** |
|-----------------|-----------------|----------------------------------------------------------|
| registerUserWithBiometricsResponse | Promise<[RegisterUserWithBiometricsResult]> | A promise that resolves to an object containing either a rID, bioToken, enrolmentStatus and programGUID fields or an error field. |

**Type Aliases**

```dart
// RegisterUserWithBiometricsParams
String reliantGUID;
String programGUID;
String consentID

// RegisterUserWithBiometricsResult
class RegisterUserWithBiometricsResult {
  final String bioToken;
  final String programGUID;
  final String rID;
  final EnrolmentStatus enrolmentStatus;

  RegisterUserWithBiometricsResult(
      this.bioToken, this.programGUID, this.rID, this.enrolmentStatus);
}

enum EnrolmentStatus { EXISTING, NEW }
```

**Error codes**

In addition to the [general error codes](https://developer.mastercard.com/cp-kernel-integration-api/documentation/reference-pages/code-and-formats/), below are the error codes that CPK can send as part of the response:

| **Error Code**                                     | **Code** | **Description**                                              |
| -------------------------------------------------- | -------- | ------------------------------------------------------------ |
| ERROR_CODE_CONSENT_NOT_FOUND                       | 400      | Specified Consent Id is not found by CPK                     |
| ERROR_CODE_DIFFERENT_CONSENT_TYPE                  | 401      | Specified Consent Id was issued for a different consent type |
| ERROR_CODE_CONSENT_DOES_NOT_MATCH                  | 402      | Specified Consent Id does match                              |
| ERROR_CODE_INVALID_CONSENT                         | 403      | Specified Consent Id is not valid                            |
| ERROR_CODE_PROGRAM_NOT_SUPPORTED                   | 300      | Specified Program GUID is not supported by CPK               |
| ERROR_CODE_PROGRAM_DOES_NOT_SUPPORT_BIOMETRIC      | 301      | Specified Program GUID does not support Biometric Capture    |
| ERROR_CODE_PROGRAM_DOES_NOT_SUPPORT_QR_FORM_FACTOR | 304      | Specified Program does not support QR form factor            |

## 2 Card Issuance

### 2.1 getWritePasscode

This API is used to write the Passcode to the card. This is initiated by the Reliant Application to CPK after a successful user registration.

```
WARNING: The Passcode that will get stored on the card must be of Integer Datatype, and composed of 6 digits.
```

**Compatibility**
| **Available as of CPK version #** | **Deprecated as of CPK version #** |
|-----------------------------------|------------------------------------|
| + CPK 2.4.1 | + n/a |

**Input Parameters**
| **Parameter** | **Type** | **Description** |
|---------------|----------|------------------------------------------------------|
| writePasscodeRequest | WritePasscodeParams | An object that contains a reliantGUID, programGUID, rID and passcode |

**Response Parameters**
| **Parameter** | **Type** | **Description** |
|-----------------|-----------------|----------------------------------------------------------|
| writePasscodeResponse | Promise<[WritePasscodeResult]> | A promise that resolves to an object containing either a responseStatus field or an error field. |

**Type Aliases**

```dart
// WritePasscodeParams
String reliantGUID;
String programGUID;
String rID;
String passcode;

// WritePasscodeResult
class WritePasscodeResult {
  final ResponseStatus responseStatus;

  WritePasscodeResult(this.responseStatus);
}

enum ResponseStatus { SUCCESS }
```

**Error codes**

In addition to the [general error codes](https://developer.mastercard.com/cp-kernel-integration-api/documentation/reference-pages/code-and-formats/), below are the error codes that CPK can send as part of the response:

| **Error Code**                    | **Code** | **Description**                                                                   |
| --------------------------------- | -------- | --------------------------------------------------------------------------------- |
| ERROR_CODE_CARD_NOT_ACTIVE        | 704      | The card is not in ACTIVE state                                                   |
| ERROR_CODE_CARD_BLACKLISTED       | 702      | Card is Blacklisted                                                               |
| ERROR_CODE_CARD_INVALID_PASSCODE  | 705      | Card passcode length is incorrect. Should be 6 digit long.                        |
| ERROR_CODE_CARD_CONNECTION_ERROR  | 721      | Card was moved or removed during read/write operation                             |
| ERROR_CODE_CARD_OPERATION_ABORTED | 771      | Card operation terminated before card transaction started by pressing Back button |

### 2.2 getWriteProfile

This API is used for card issuance to write the user’s basic profile data to the card once the user has been successfully registered, either by biometric flow or passcode flow. This operation is initiated by the Reliant Application after a successful user registration, and the Reliant Application receives the R-ID.

```
WARNING: The Passcode that will get stored on the card must be of Integer Datatype, and composed of 6 digits.
```

**Compatibility**
| **Available as of CPK version #** | **Deprecated as of CPK version #** |
|-----------------------------------|------------------------------------|
| + CPK 2.4.1 | + n/a |

**Input Parameters**
| **Parameter** | **Type** | **Description** |
|---------------|----------|------------------------------------------------------|
| writeProfileRequest | WriteProfileParams | An object that contains a reliantGUID, programGUID, rID and overwriteCard |

**Response Parameters**
| **Parameter** | **Type** | **Description** |
|-----------------|-----------------|----------------------------------------------------------|
| writeProfileResponse | Promise<[WriteProfileResult]> | A promise that resolves to an object containing either a consumerDeviceNumber field or an error field. |

**Type Aliases**

```dart
// WriteProfileParams
String reliantGUID;
String programGUID;
String rID;
bool overwriteCard;

// WriteProfileResult
class WriteProfileResult {
  final String consumerDeviceNumber;

  WriteProfileResult(this.consumerDeviceNumber);
}
```

**Error codes**

In addition to the [general error codes](https://developer.mastercard.com/cp-kernel-integration-api/documentation/reference-pages/code-and-formats/), below are the error codes that CPK can send as part of the response:

| **Error Code**                                  | **Code** | **Description**                                                                                                                     |
| ----------------------------------------------- | -------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| ERROR_CODE_CARD_ALREADY_IN_USE                  | 703      | The card is already in ACTIVE state                                                                                                 |
| ERROR_CODE_CARD_BLACKLISTED                     | 702      | Card is Blacklisted                                                                                                                 |
| ERROR_CODE_CARD_CONNECTION_ERROR                | 721      | Card was moved or removed during read/write operation                                                                               |
| ERROR_CODE_INSUFFICIENT_DATA                    | 762      | User profile is not found locally                                                                                                   |
| ERROR_CODE_AUTH_METHOD_BIOMETRIC_BUT_NO_HASHES  | 763      | User found, insufficient data cannot write profile on the card – missing modalities (if any LP, RP, Face configured to the program) |
| ERROR_CODE_INSUFFICIENT_HASHES_TO_WRITE_ON_CARD | 765      | Insufficient data cannot write hashes on the card– missing modalities (if any LP, RP, Face configured to the program)               |

[Return to API reference](README.md)

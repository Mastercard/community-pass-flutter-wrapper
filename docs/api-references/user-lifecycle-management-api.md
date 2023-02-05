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
| + CPK 2.0.1 | + n/a |

**Input Parameters**
| **Parameter** | **Type** | **Description** |
|---------------|----------|------------------------------------------------------|
| consentRequest | SaveBiometricConsentParams | An object that contains a reliantAppGUID, programGUID and consumerConsentValue |

**Response Parameters**
| **Parameter** | **Type** | **Description** |
|-----------------|-----------------|----------------------------------------------------------|
| consentResponse | Promise<[SaveBiometricConsentResult]> | A promise that resolves to an object containing either a consentId and responseStatus fields or an error field. |

**Type Aliases**

```dart
// SaveBiometricConsentParams
String reliantAppGUID;
String programGUID;

// SaveBiometricConsentResult
class SaveBiometricConsentResult {
  final String consentId;
  final ResponseStatus responseStatus;

  SaveBiometricConsentResult(this.consentId, this.responseStatus);
}

enum ResponseStatus { SUCCESS }
```

**Error codes**

In addition to the [general error codes](https://developer.mastercard.com/cp-kernel-integration-api/documentation/reference-pages/code-and-formats/), below are the error codes that CPK can send as part of the response:

| **Error Code**                                | **Description**                                         |
| --------------------------------------------- | ------------------------------------------------------- |
| ERROR_CODE_PROGRAM_NOT_SUPPORTED              | Specified Program ID is not supported by CPK            |
| ERROR_CODE_PROGRAM_DOES_NOT_SUPPORT_BIOMETRIC | Specified Program ID does not support biometric capture |

### 1.2 getRegisterBasicUser

This API is used to register an existing user with their card/CP Consumer Device present.

**Compatibility**
| **Available as of CPK version #** | **Deprecated as of CPK version #** |
|-----------------------------------|------------------------------------|
| + CPK 2.0.1 | + n/a |

**Input Parameters**
| **Parameter** | **Type** | **Description** |
|---------------|----------|------------------------------------------------------|
| registerBasicUserRequest | RegisterBasicUserParams | An object that contains a reliantAppGUID and programGUID |

**Response Parameters**
| **Parameter** | **Type** | **Description** |
|-----------------|-----------------|----------------------------------------------------------|
| registerBasicUserResponse | Promise<[RegisterBasicUserResult]> | A promise that resolves to an object containing either a rId field or an error field. |

**Type Aliases**

```dart
// RegisterBasicUserParams
String reliantAppGUID;
String programGUID;

// RegisterBasicUserResult
class RegisterBasicUserResult {
  final String consentId;
  final ResponseStatus responseStatus;

  RegisterBasicUserResult(this.consentId, this.responseStatus);
}

enum ResponseStatus { SUCCESS }
```

**Error codes**

In addition to the [general error codes](https://developer.mastercard.com/cp-kernel-integration-api/documentation/reference-pages/code-and-formats/), below are the error codes that CPK can send as part of the response:

### 1.3 getRegisterUserWithBiometrics

This API is used by the Reliant Application to initiate the user registration flow. It returns the Intent object which can be used by the Reliant Application to start the user registration using the user’s biometric data. Following a successful user registration, a user profile is created and associated with a CP Program. If the user’s profile already exists, e.g., the user is already registered in another program, the user’s profile is updated with the new association. Moreover, it enables you to select a formfactor during the registration process i.e. Card, QR or None.

```
Warning: Reliant Application must obtain the consentId first using the saveBiometricConsent API before invoking the user registration flow.
```

**Compatibility**
| **Available as of CPK version #** | **Deprecated as of CPK version #** |
|-----------------------------------|------------------------------------|
| + CPK 2.0.1 | + n/a |

**Input Parameters**
| **Parameter** | **Type** | **Description** |
|---------------|----------|------------------------------------------------------|
| registerUserWithBiometricsRequest | RegisterUserWithBiometricsParams | An object that contains a reliantAppGUID, programGUID and consentId |

**Response Parameters**
| **Parameter** | **Type** | **Description** |
|-----------------|-----------------|----------------------------------------------------------|
| registerUserWithBiometricsResponse | Promise<[RegisterUserWithBiometricsResult]> | A promise that resolves to an object containing either a rId, bioToken, enrolmentStatus and programGUID fields or an error field. |

**Type Aliases**

```dart
// RegisterUserWithBiometricsParams
String reliantAppGUID;
String programGUID;
String consentId

// RegisterUserWithBiometricsResult
class RegisterUserWithBiometricsResult {
  final String bioToken;
  final String programGUID;
  final String rId;
  final EnrolmentStatus enrolmentStatus;

  RegisterUserWithBiometricsResult(
      this.bioToken, this.programGUID, this.rId, this.enrolmentStatus);
}

enum EnrolmentStatus { EXISTING, NEW }
```

**Error codes**

In addition to the [general error codes](https://developer.mastercard.com/cp-kernel-integration-api/documentation/reference-pages/code-and-formats/), below are the error codes that CPK can send as part of the response:

| **Error Code**                                     | **Description**                                              |
| -------------------------------------------------- | ------------------------------------------------------------ |
| ERROR_CODE_CONSENT_NOT_FOUND                       | Specified Consent Id is not found by CPK                     |
| ERROR_CODE_DIFFERENT_CONSENT_TYPE                  | Specified Consent Id was issued for a different consent type |
| ERROR_CODE_CONSENT_DOES_NOT_MATCH                  | Specified Consent Id does match                              |
| ERROR_CODE_INVALID_CONSENT                         | Specified Consent Id is not valid                            |
| ERROR_CODE_PROGRAM_NOT_SUPPORTED                   | Specified Program GUID is not supported by CPK               |
| ERROR_CODE_PROGRAM_DOES_NOT_SUPPORT_BIOMETRIC      | Specified Program GUID does not support Biometric Capture    |
| ERROR_CODE_PROGRAM_DOES_NOT_SUPPORT_QR_FORM_FACTOR | Specified Program does not support QR form factor            |

## 2 Card Issuance

### 2.1 getWritePasscode

This API is used to write the Passcode to the card. This is initiated by the Reliant Application to CPK after a successful user registration.

```
WARNING: The Passcode that will get stored on the card must be of Integer Datatype, and composed of 6 digits.
```

**Compatibility**
| **Available as of CPK version #** | **Deprecated as of CPK version #** |
|-----------------------------------|------------------------------------|
| + CPK 2.0.1 | + n/a |

**Input Parameters**
| **Parameter** | **Type** | **Description** |
|---------------|----------|------------------------------------------------------|
| writePasscodeRequest | WritePasscodeParams | An object that contains a reliantAppGUID, programGUID, rID and passcode |

**Response Parameters**
| **Parameter** | **Type** | **Description** |
|-----------------|-----------------|----------------------------------------------------------|
| writePasscodeResponse | Promise<[WritePasscodeResult]> | A promise that resolves to an object containing either a responseStatus field or an error field. |

**Type Aliases**

```dart
// WritePasscodeParams
String reliantAppGUID;
String programGUID;
String rId;
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

| **Error Code**                    | **Description**                                                                   |
| --------------------------------- | --------------------------------------------------------------------------------- |
| ERROR_CODE_CARD_NOT_ACTIVE        | The card is not in ACTIVE state                                                   |
| ERROR_CODE_CARD_BLACKLISTED       | Card is Blacklisted                                                               |
| ERROR_CODE_PROGRAM_GUID_NOT_MATCH | Program GUID does not match on the card                                           |
| ERROR_CODE_CARD_INVALID_PASSCODE  | Card passcode length is incorrect. Should be 6 digit long.                        |
| ERROR_CODE_CARD_CONNECTION_ERROR  | Card was moved or removed during read/write operation                             |
| ERROR_CODE_CARD_OPERATION_ABORTED | Card operation terminated before card transaction started by pressing Back button |

### 2.2 getWriteProfile

This API is used for card issuance to write the user’s basic profile data to the card once the user has been successfully registered, either by biometric flow or passcode flow. This operation is initiated by the Reliant Application after a successful user registration, and the Reliant Application receives the R-ID.

```
WARNING: The Passcode that will get stored on the card must be of Integer Datatype, and composed of 6 digits.
```

**Compatibility**
| **Available as of CPK version #** | **Deprecated as of CPK version #** |
|-----------------------------------|------------------------------------|
| + CPK 2.0.1 | + n/a |

**Input Parameters**
| **Parameter** | **Type** | **Description** |
|---------------|----------|------------------------------------------------------|
| writeProfileRequest | WriteProfileParams | An object that contains a reliantAppGUID, programGUID, rID and overwriteCard |

**Response Parameters**
| **Parameter** | **Type** | **Description** |
|-----------------|-----------------|----------------------------------------------------------|
| writeProfileResponse | Promise<[WriteProfileResult]> | A promise that resolves to an object containing either a consumerDeviceNumber field or an error field. |

**Type Aliases**

```dart
// WriteProfileParams
String reliantAppGUID;
String programGUID;
String rId;
bool overwriteCard;

// WriteProfileResult
class WriteProfileResult {
  final String consumerDeviceNumber;

  WriteProfileResult(this.consumerDeviceNumber);
}
```

**Error codes**

In addition to the [general error codes](https://developer.mastercard.com/cp-kernel-integration-api/documentation/reference-pages/code-and-formats/), below are the error codes that CPK can send as part of the response:

| **Error Code**                                  | **Description**                                                                                                                     |
| ----------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| ERROR_CODE_CARD_ALREADY_IN_USE                  | The card is already in ACTIVE state                                                                                                 |
| ERROR_CODE_CARD_BLACKLISTED                     | Card is Blacklisted                                                                                                                 |
| ERROR_CODE_PROGRAM_GUID_NOT_MATCH               | Program GUID does not match on the card                                                                                             |
| ERROR_CODE_CARD_CONNECTION_ERROR                | Card was moved or removed during read/write operation                                                                               |
| ERROR_CODE_INSUFFICIENT_DATA_WITH_CPK           | CPK does not have sufficient data to write/update the profile on the card                                                           |
| ERROR_CODE_AUTH_METHOD_BIOMETRIC_BUT_NO_HASHES  | User found, insufficient data cannot write profile on the card – missing modalities (if any LP, RP, Face configured to the program) |
| ERROR_CODE_INSUFFICIENT_HASHES_TO_WRITE_ON_CARD | Insufficient data cannot write hashes on the card– missing modalities (if any LP, RP, Face configured to the program)               |

[Return to API reference](README.md)

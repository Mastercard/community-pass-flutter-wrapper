

### API <a name="api"></a>

| Method                                                          | Parameters                                                                                                                                    | Return Type         |
| --------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- | ------------------- |
| [saveBiometricConsent](#savebiometricconsent)                   | `reliantAppGuid`: String (required)<br/> `programGuid`: String (required)                                                                     | `consentId`: String |
| [getRegisterUserWithBiometrics](#getregisteruserwithbiometrics) | `reliantAppGuid`: String (required)<br/> `programGuid`: String (required)<br/> `consentId`: String (required)                                 | `rId`: String       |
| [getRegisterBasicUser](#getregisterbasicuser)                   | `reliantAppGuid`: String (required)<br/> `programGuid`: String (required)                                                                     | `rId`: String       |
| [getWriteProfile](#getwriteprofile)                             | `reliantAppGuid`: String (required)<br/> `programGuid`: String (required)<br/> `rId`: String (required)<br/> `overwriteCard`: Bool (optional) | `<Object>`          |
| [getWritePasscode](#getwritepasscode)                           | `reliantAppGuid`: String (required)<br/> `programGuid`: String (required)<br/> `rId`: String (required)<br/> `passcode`: String (required)    | `<Object>`          |

### saveBiometricConsent

This API is used to save the user consent prior to collecting the biometric(s) and generating a unique digital identity.

### getRegisterUserWithBiometrics

This API is used by the Reliant Application to receive a bio-token and an R-ID following the registration of a new user.

### getRegisterBasicUser

This API is used to initiate the user registration with a passcode. The API is initiated by the Reliant Application after verifying that the user is unique and is invoked when the user declines the biometric consent capture.

### getWriteProfile

This API is used for card issuance to write the user’s basic profile data to the card once the user has been successfully registered, either by biometric flow or passcode flow. This operation is initiated by the Reliant Application after a successful user registration, when the Reliant Application receives the R-ID.

### getWritePasscode

This API is used to write the Passcode to the card. It is initiated by the Reliant Application to CPK after a successful user registration. The received Intent must be used for starting the Activity using startActivityForResult(), and the result must be handled via the onActivityResult() method.
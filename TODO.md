## 1. Pre-Transaction Phase

This phase will include all user setup actions that facilitate the onboarding of Users for Community Pass Digital ID, multi-wallet and acceptance accounts. These actions provides the ability to issue a Community Pass credential to a user using cards (or QR) and onboard them onto Community Pass:

✅ Action: Enroll a new user and Issue card using Biometrics

      saveBiometricConsent()
          ✅ Implement method
          ✅ Add example
      getRegisterUserForBioTokenActivityIntent()
          ✅ Implement method
          ✅ Add example
      getWriteProfileActivityIntent()
          ✅  Implement method
          ✅  Add example

✅ Action: Enroll a new user and issue card using Passcode

      saveBiometricConsent()
          ✅ Implement method
          ✅ Add example
      getRegisterBasicUserActivityIntent()
          ✅ Implement method
          ✅ Add example
      getGenerateCpUserProfileActivityIntent()
          ✅ Implement method
          ✅ Add example

✅ Action: Enroll a new user and issue QR using Biometrics

      saveBiometricConsent()
          ✅ Implement method
          ✅ Add example
      getRegisterUserForBioTokenActivityIntent()
          ✅ Implement method
          ✅ Add example
      getGenerateCpUserProfileActivityIntent()
          ✅ Implement method
          ✅ Add example

✅ Action: Enroll a new user and issue QR using passcode

      saveBiometricConsent()
          ✅ Implement method
          ✅ Add example
      getRegisterBasicUserActivityIntent()
          ✅ Implement method
          ✅ Add example
      getGenerateCpUserProfileActivityIntent()
          ✅ Implement method
          ✅ Add example

## 2. Transaction Phase

This phase will include all actions and activities that enable you to conduct a life transaction using Community Pass. These actions provides safe and secure interactions between an acceptor and end-users in both online and off-line environments:

✅ Action: Authenticate Biometric Registered User on Program using Form Factor-Card

      getRegistrationDataActivityIntent()
          ✅ Implement method
          ✅ Add example
      getUserVerificationActivityIntent()
          ✅ Implement method
          ✅ Add example

⬜ Action: Authenticate Passcode Registered User on Program using Form Factor-Card

      getRegistrationDataActivityIntent()
          ✅ Implement method
          ✅ Add example
      getVerifyPasscodeActivityIntent()
          ✅ Implement method
          ✅ Add example

⬜ Action: Authenticate Biometric Registered User on Program using Form Factor-QR

      getUserVerificationActivityIntent()
          ✅ Implement method
          ✅ Add example

⬜ Action: Authenticate Passcode Registered User on Program using Form Factor-QR

      getVerifyPasscodeActivityIntent()
          ✅ Implement method
          ✅ Add example

## 3. Admin-Transaction Phase

This phase demonstrates actions and activities that enable you to manage any actions and interactions with users that take place outside of conducting a transaction e.g. updating a user credential data and managing a program within Community Pass:

⬜ Action: Add Biometric Details to Passcode Authenticated User and Update Card

      getRegistrationDataActivityIntent()
          ✅ Implement method
          ✅ Add example
      saveBiometricConsent()
          ✅ Implement method
          ✅ Add example
      getVerifyPasscodeActivityIntent()
          ✅ Implement method
          ✅ Add example
      getAddBiometricsActivityIntent()
          ⬜ Implement method
          ⬜ Add example
      getUpdateCardProfileActivityIntent()
          ⬜ Implement method
          ⬜ Add example

⬜ Action: Add Biometric Details to Passcode Authenticated User and update QR

      saveBiometricConsent()
          ✅ Implement method
          ✅ Add example
      getVerifyPasscodeActivityIntent()
          ✅ Implement method
          ✅ Add example
      getAddBiometricsActivityIntent()
          ⬜ Implement method
          ⬜ Add example
      getGenerateCpUserProfileActivityIntent()
          ✅ Implement method
          ✅ Add example

⬜ Action: Issue new Form Factor to an existing user with Biometrics mapped

      getUserIdentificationActivityIntent()
          ✅ Implement method
          ✅ Add example
      getBlacklistFormFactorActivityIntent()
          ✅ Implement method
          ⬜ Add example
      getWriteProfileActivityIntent()
          ✅ Implement method
          ✅ Add example
      getGenerateCpUserProfileActivityIntent()
          ✅ Implement method
          ✅ Add example

⬜ Action: Issue new Form Factor to an existing user with passcode authentication

      getWritePasscodeActivityIntent()
          ✅ Implement method
          ✅ Add example
      getBlacklistFormFactorActivityIntent()
          ✅ Implement method
          ⬜ Add example
      getWriteProfileActivityIntent()
          ✅ Implement method
          ✅ Add example
      getGenerateCpUserProfileActivityIntent()
          ✅ Implement method
          ✅ Add example

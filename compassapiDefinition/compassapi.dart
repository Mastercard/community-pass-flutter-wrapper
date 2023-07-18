import 'package:pigeon/pigeon.dart';

// flutter pub run pigeon --input compassapiDefinition/compassapi.dart
@ConfigurePigeon(PigeonOptions(
  dartOut: 'lib/compassapi.dart',
  dartTestOut: 'test/test_api.dart',
  kotlinOut:
      'android/src/main/kotlin/com/mastercard/compass/cp3/lib/flutter_wrapper/CompassApiFlutter.kt',
  kotlinOptions:
      KotlinOptions(package: "com.mastercard.compass.cp3.lib.flutter_wrapper"),
  // javaOut:
  // 'android/src/main/java/com/mastercard/compass/cp3/java_flutter_wrapper/CompassApiFlutter.java',
  // 'android/src/main/kotlin/com/mastercard/compass/cp3/lib/flutter_wrapper/CompassApiFlutter.java',
  // javaOptions: JavaOptions(
  //   package: 'com.mastercard.compass.cp3.lib.flutter_wrapper',
  // ),
  copyrightHeader: 'compassapiDefinition/copyright.txt',
))
enum EnrolmentStatus { EXISTING, NEW }

enum ResponseStatus { SUCCESS, ERROR, UNDEFINED }

enum OperationMode { BEST_AVAILABLE, FULL }

enum Modality { FACE, LEFT_PALM, RIGHT_PALM }

enum FormFactorStatus { ACTIVE, BLACKLISTED, UNKNOWN, BLOCED }

enum FormFactor { CARD, QR, NONE }

enum SVAType { FinancialSVA, EVoucherSVA }

enum EVoucherType { COMMODITY, POINT }

class SaveBiometricConsentResult {
  final String consentID;
  final ResponseStatus responseStatus;

  SaveBiometricConsentResult(this.consentID, this.responseStatus);
}

class RegisterUserWithBiometricsResult {
  final String bioToken;
  final String programGUID;
  final String rID;
  final EnrolmentStatus enrolmentStatus;

  RegisterUserWithBiometricsResult(
      this.bioToken, this.programGUID, this.rID, this.enrolmentStatus);
}

class RegisterBasicUserResult {
  final String rID;

  RegisterBasicUserResult(this.rID);
}

class WriteProfileResult {
  final String consumerDeviceNumber;

  WriteProfileResult(this.consumerDeviceNumber);
}

class WritePasscodeResult {
  final ResponseStatus responseStatus;

  WritePasscodeResult(this.responseStatus);
}

class VerifyPasscodeResult {
  final String rID;
  final bool status;
  final int retryCount;

  VerifyPasscodeResult(this.rID, this.status, this.retryCount);
}

class Match {
  final int distance;
  final String modality;
  final int normalizedScore;

  Match(this.distance, this.modality, this.normalizedScore);
}

class UserVerificationResult {
  final bool isMatchFound;
  final String rID;
  final List<Match?> biometricMatchList;

  UserVerificationResult(this.isMatchFound, this.rID, this.biometricMatchList);
}

class RegistrationDataResult {
  final bool isRegisteredInProgram;
  final List<String?> authMethods;
  final List<String?> modalityType;
  final String rID;

  RegistrationDataResult(this.isRegisteredInProgram, this.authMethods,
      this.modalityType, this.rID);
}

class ReadProgramSpaceResult {
  final String programSpaceData;
  ReadProgramSpaceResult(this.programSpaceData);
}

class WriteProgramSpaceResult {
  final bool isSuccess;
  WriteProgramSpaceResult(this.isSuccess);
}

class BlacklistFormFactorResult {
  final String type;
  final FormFactorStatus status;
  final String consumerDeviceNumber;

  BlacklistFormFactorResult(this.type, this.status, this.consumerDeviceNumber);
}

class ReadSVAResult {
  final int currentBalance;
  final int transactionCount;
  final String purseType;
  final String unit;
  final Transaction lastTransaction;

  ReadSVAResult(this.currentBalance, this.transactionCount, this.purseType,
      this.unit, this.lastTransaction);
}

class Transaction {
  final int amount;
  final int balance;

  Transaction(this.amount, this.balance);
}

class SVA {
  final SVAType type;
  final String unit;
  final EVoucherType eVoucherType;

  SVA(this.type, this.unit, this.eVoucherType);
}

class CreateSVAResult {
  final String response;
  CreateSVAResult(this.response);
}

class GenerateCpUserProfileResult {
  final String token;
  final String consumerDeviceNumber;
  final String message;
  GenerateCpUserProfileResult(
      this.token, this.consumerDeviceNumber, this.message);
}

@HostApi()
abstract class CommunityPassApi {
  @async
  SaveBiometricConsentResult saveBiometricConsent(
      String reliantGUID, String programGUID, bool consumerConsentValue);

  @async
  RegisterUserWithBiometricsResult getRegisterUserWithBiometrics(
      String reliantGUID,
      String programGUID,
      String consentID,
      List<String> modalities,
      OperationMode operationMode);

  @async
  RegisterBasicUserResult getRegisterBasicUser(
      String reliantGUID, String programGUID);

  @async
  WriteProfileResult getWriteProfile(
      String reliantGUID, String programGUID, String rID, bool overwriteCard);

  @async
  WritePasscodeResult getWritePasscode(
      String reliantGUID, String programGUID, String rID, String passcode);

  @async
  VerifyPasscodeResult getVerifyPasscode(String reliantGUID, String programGUID,
      String passcode, FormFactor formFactor, String? qrCpUserProfile);

  @async
  UserVerificationResult getUserVerification(String reliantGUID,
      String programGUID, String token, List<String> modalities);

  @async
  RegistrationDataResult getRegistrationData(
      String reliantGUID, String programGUID);

  @async
  WriteProgramSpaceResult getWriteProgramSpace(
    String reliantGUID,
    String programGUID,
    String rID,
    String programSpaceData,
    bool encryptData,
  );

  @async
  ReadProgramSpaceResult getReadProgramSpace(
    String reliantGUID,
    String programGUID,
    String rID,
    bool decryptData,
  );

  @async
  BlacklistFormFactorResult getBlacklistFormFactor(
      String reliantGUID,
      String programGUID,
      String rID,
      String consumerDeviceNumber,
      FormFactor type);

  @async
  ReadSVAResult getReadSVA(
      String reliantGUID, String programGUID, String rID, String svaUnit);

  @async
  CreateSVAResult getCreateSVA(
      String reliantGUID, String programGUID, String? rID, SVA sva);

  @async
  GenerateCpUserProfileResult getGenerateCpUserProfile(
      String reliantGUID, String programGUID, String rID, String? passcode);
}

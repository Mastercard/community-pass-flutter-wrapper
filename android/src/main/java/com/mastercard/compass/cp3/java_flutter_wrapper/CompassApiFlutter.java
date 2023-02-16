// Copyright (c) 2022 Mastercard. All rights reserved.
// Use of this source code is governed by a Apache License, Version 2.0 that can be
// found in the LICENSE file.
// Autogenerated from Pigeon (v8.0.0), do not edit directly.
// See also: https://pub.dev/packages/pigeon

package com.mastercard.compass.cp3.java_flutter_wrapper;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.common.StandardMessageCodec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Generated class from Pigeon. */
@SuppressWarnings({"unused", "unchecked", "CodeBlock2Expr", "RedundantSuppression"})
public class CompassApiFlutter {
  @NonNull
  private static ArrayList<Object> wrapError(@NonNull Throwable exception) {
    ArrayList<Object> errorList = new ArrayList<Object>(3);
    errorList.add(exception.toString());
    errorList.add(exception.getClass().getSimpleName());
    errorList.add(
      "Cause: " + exception.getCause() + ", Stacktrace: " + Log.getStackTraceString(exception));
    return errorList;
  }

  public enum EnrolmentStatus {
    EXISTING(0),
    NEW(1);

    private final int index;

    private EnrolmentStatus(final int index) {
      this.index = index;
    }
  }

  public enum ResponseStatus {
    SUCCESS(0),
    FAIL(1);

    private final int index;

    private ResponseStatus(final int index) {
      this.index = index;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static final class SaveBiometricConsentResult {
    private @NonNull String consentID;

    public @NonNull String getConsentID() {
      return consentID;
    }

    public void setConsentID(@NonNull String setterArg) {
      if (setterArg == null) {
        throw new IllegalStateException("Nonnull field \"consentID\" is null.");
      }
      this.consentID = setterArg;
    }

    private @NonNull ResponseStatus responseStatus;

    public @NonNull ResponseStatus getResponseStatus() {
      return responseStatus;
    }

    public void setResponseStatus(@NonNull ResponseStatus setterArg) {
      if (setterArg == null) {
        throw new IllegalStateException("Nonnull field \"responseStatus\" is null.");
      }
      this.responseStatus = setterArg;
    }

    /** Constructor is private to enforce null safety; use Builder. */
    private SaveBiometricConsentResult() {}

    public static final class Builder {

      private @Nullable String consentID;

      public @NonNull Builder setConsentID(@NonNull String setterArg) {
        this.consentID = setterArg;
        return this;
      }

      private @Nullable ResponseStatus responseStatus;

      public @NonNull Builder setResponseStatus(@NonNull ResponseStatus setterArg) {
        this.responseStatus = setterArg;
        return this;
      }

      public @NonNull SaveBiometricConsentResult build() {
        SaveBiometricConsentResult pigeonReturn = new SaveBiometricConsentResult();
        pigeonReturn.setConsentID(consentID);
        pigeonReturn.setResponseStatus(responseStatus);
        return pigeonReturn;
      }
    }

    @NonNull
    ArrayList<Object> toList() {
      ArrayList<Object> toListResult = new ArrayList<Object>(2);
      toListResult.add(consentID);
      toListResult.add(responseStatus == null ? null : responseStatus.index);
      return toListResult;
    }

    static @NonNull SaveBiometricConsentResult fromList(@NonNull ArrayList<Object> list) {
      SaveBiometricConsentResult pigeonResult = new SaveBiometricConsentResult();
      Object consentID = list.get(0);
      pigeonResult.setConsentID((String) consentID);
      Object responseStatus = list.get(1);
      pigeonResult.setResponseStatus(responseStatus == null ? null : ResponseStatus.values()[(int) responseStatus]);
      return pigeonResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static final class RegisterUserWithBiometricsResult {
    private @NonNull String bioToken;

    public @NonNull String getBioToken() {
      return bioToken;
    }

    public void setBioToken(@NonNull String setterArg) {
      if (setterArg == null) {
        throw new IllegalStateException("Nonnull field \"bioToken\" is null.");
      }
      this.bioToken = setterArg;
    }

    private @NonNull String programGUID;

    public @NonNull String getProgramGUID() {
      return programGUID;
    }

    public void setProgramGUID(@NonNull String setterArg) {
      if (setterArg == null) {
        throw new IllegalStateException("Nonnull field \"programGUID\" is null.");
      }
      this.programGUID = setterArg;
    }

    private @NonNull String rID;

    public @NonNull String getRID() {
      return rID;
    }

    public void setRID(@NonNull String setterArg) {
      if (setterArg == null) {
        throw new IllegalStateException("Nonnull field \"rID\" is null.");
      }
      this.rID = setterArg;
    }

    private @NonNull EnrolmentStatus enrolmentStatus;

    public @NonNull EnrolmentStatus getEnrolmentStatus() {
      return enrolmentStatus;
    }

    public void setEnrolmentStatus(@NonNull EnrolmentStatus setterArg) {
      if (setterArg == null) {
        throw new IllegalStateException("Nonnull field \"enrolmentStatus\" is null.");
      }
      this.enrolmentStatus = setterArg;
    }

    /** Constructor is private to enforce null safety; use Builder. */
    private RegisterUserWithBiometricsResult() {}

    public static final class Builder {

      private @Nullable String bioToken;

      public @NonNull Builder setBioToken(@NonNull String setterArg) {
        this.bioToken = setterArg;
        return this;
      }

      private @Nullable String programGUID;

      public @NonNull Builder setProgramGUID(@NonNull String setterArg) {
        this.programGUID = setterArg;
        return this;
      }

      private @Nullable String rID;

      public @NonNull Builder setRID(@NonNull String setterArg) {
        this.rID = setterArg;
        return this;
      }

      private @Nullable EnrolmentStatus enrolmentStatus;

      public @NonNull Builder setEnrolmentStatus(@NonNull EnrolmentStatus setterArg) {
        this.enrolmentStatus = setterArg;
        return this;
      }

      public @NonNull RegisterUserWithBiometricsResult build() {
        RegisterUserWithBiometricsResult pigeonReturn = new RegisterUserWithBiometricsResult();
        pigeonReturn.setBioToken(bioToken);
        pigeonReturn.setProgramGUID(programGUID);
        pigeonReturn.setRID(rID);
        pigeonReturn.setEnrolmentStatus(enrolmentStatus);
        return pigeonReturn;
      }
    }

    @NonNull
    ArrayList<Object> toList() {
      ArrayList<Object> toListResult = new ArrayList<Object>(4);
      toListResult.add(bioToken);
      toListResult.add(programGUID);
      toListResult.add(rID);
      toListResult.add(enrolmentStatus == null ? null : enrolmentStatus.index);
      return toListResult;
    }

    static @NonNull RegisterUserWithBiometricsResult fromList(@NonNull ArrayList<Object> list) {
      RegisterUserWithBiometricsResult pigeonResult = new RegisterUserWithBiometricsResult();
      Object bioToken = list.get(0);
      pigeonResult.setBioToken((String) bioToken);
      Object programGUID = list.get(1);
      pigeonResult.setProgramGUID((String) programGUID);
      Object rID = list.get(2);
      pigeonResult.setRID((String) rID);
      Object enrolmentStatus = list.get(3);
      pigeonResult.setEnrolmentStatus(enrolmentStatus == null ? null : EnrolmentStatus.values()[(int) enrolmentStatus]);
      return pigeonResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static final class RegisterBasicUserResult {
    private @NonNull String rID;

    public @NonNull String getRID() {
      return rID;
    }

    public void setRID(@NonNull String setterArg) {
      if (setterArg == null) {
        throw new IllegalStateException("Nonnull field \"rID\" is null.");
      }
      this.rID = setterArg;
    }

    /** Constructor is private to enforce null safety; use Builder. */
    private RegisterBasicUserResult() {}

    public static final class Builder {

      private @Nullable String rID;

      public @NonNull Builder setRID(@NonNull String setterArg) {
        this.rID = setterArg;
        return this;
      }

      public @NonNull RegisterBasicUserResult build() {
        RegisterBasicUserResult pigeonReturn = new RegisterBasicUserResult();
        pigeonReturn.setRID(rID);
        return pigeonReturn;
      }
    }

    @NonNull
    ArrayList<Object> toList() {
      ArrayList<Object> toListResult = new ArrayList<Object>(1);
      toListResult.add(rID);
      return toListResult;
    }

    static @NonNull RegisterBasicUserResult fromList(@NonNull ArrayList<Object> list) {
      RegisterBasicUserResult pigeonResult = new RegisterBasicUserResult();
      Object rID = list.get(0);
      pigeonResult.setRID((String) rID);
      return pigeonResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static final class WriteProfileResult {
    private @NonNull String consumerDeviceNumber;

    public @NonNull String getConsumerDeviceNumber() {
      return consumerDeviceNumber;
    }

    public void setConsumerDeviceNumber(@NonNull String setterArg) {
      if (setterArg == null) {
        throw new IllegalStateException("Nonnull field \"consumerDeviceNumber\" is null.");
      }
      this.consumerDeviceNumber = setterArg;
    }

    /** Constructor is private to enforce null safety; use Builder. */
    private WriteProfileResult() {}

    public static final class Builder {

      private @Nullable String consumerDeviceNumber;

      public @NonNull Builder setConsumerDeviceNumber(@NonNull String setterArg) {
        this.consumerDeviceNumber = setterArg;
        return this;
      }

      public @NonNull WriteProfileResult build() {
        WriteProfileResult pigeonReturn = new WriteProfileResult();
        pigeonReturn.setConsumerDeviceNumber(consumerDeviceNumber);
        return pigeonReturn;
      }
    }

    @NonNull
    ArrayList<Object> toList() {
      ArrayList<Object> toListResult = new ArrayList<Object>(1);
      toListResult.add(consumerDeviceNumber);
      return toListResult;
    }

    static @NonNull WriteProfileResult fromList(@NonNull ArrayList<Object> list) {
      WriteProfileResult pigeonResult = new WriteProfileResult();
      Object consumerDeviceNumber = list.get(0);
      pigeonResult.setConsumerDeviceNumber((String) consumerDeviceNumber);
      return pigeonResult;
    }
  }

  /** Generated class from Pigeon that represents data sent in messages. */
  public static final class WritePasscodeResult {
    private @NonNull ResponseStatus responseStatus;

    public @NonNull ResponseStatus getResponseStatus() {
      return responseStatus;
    }

    public void setResponseStatus(@NonNull ResponseStatus setterArg) {
      if (setterArg == null) {
        throw new IllegalStateException("Nonnull field \"responseStatus\" is null.");
      }
      this.responseStatus = setterArg;
    }

    /** Constructor is private to enforce null safety; use Builder. */
    private WritePasscodeResult() {}

    public static final class Builder {

      private @Nullable ResponseStatus responseStatus;

      public @NonNull Builder setResponseStatus(@NonNull ResponseStatus setterArg) {
        this.responseStatus = setterArg;
        return this;
      }

      public @NonNull WritePasscodeResult build() {
        WritePasscodeResult pigeonReturn = new WritePasscodeResult();
        pigeonReturn.setResponseStatus(responseStatus);
        return pigeonReturn;
      }
    }

    @NonNull
    ArrayList<Object> toList() {
      ArrayList<Object> toListResult = new ArrayList<Object>(1);
      toListResult.add(responseStatus == null ? null : responseStatus.index);
      return toListResult;
    }

    static @NonNull WritePasscodeResult fromList(@NonNull ArrayList<Object> list) {
      WritePasscodeResult pigeonResult = new WritePasscodeResult();
      Object responseStatus = list.get(0);
      pigeonResult.setResponseStatus(responseStatus == null ? null : ResponseStatus.values()[(int) responseStatus]);
      return pigeonResult;
    }
  }

  public interface Result<T> {
    void success(T result);

    void error(Throwable error);
  }

  private static class CommunityPassApiCodec extends StandardMessageCodec {
    public static final CommunityPassApiCodec INSTANCE = new CommunityPassApiCodec();

    private CommunityPassApiCodec() {}

    @Override
    protected Object readValueOfType(byte type, @NonNull ByteBuffer buffer) {
      switch (type) {
        case (byte) 128:
          return RegisterBasicUserResult.fromList((ArrayList<Object>) readValue(buffer));
        case (byte) 129:
          return RegisterUserWithBiometricsResult.fromList((ArrayList<Object>) readValue(buffer));
        case (byte) 130:
          return SaveBiometricConsentResult.fromList((ArrayList<Object>) readValue(buffer));
        case (byte) 131:
          return WritePasscodeResult.fromList((ArrayList<Object>) readValue(buffer));
        case (byte) 132:
          return WriteProfileResult.fromList((ArrayList<Object>) readValue(buffer));
        default:
          return super.readValueOfType(type, buffer);
      }
    }

    @Override
    protected void writeValue(@NonNull ByteArrayOutputStream stream, Object value) {
      if (value instanceof RegisterBasicUserResult) {
        stream.write(128);
        writeValue(stream, ((RegisterBasicUserResult) value).toList());
      } else if (value instanceof RegisterUserWithBiometricsResult) {
        stream.write(129);
        writeValue(stream, ((RegisterUserWithBiometricsResult) value).toList());
      } else if (value instanceof SaveBiometricConsentResult) {
        stream.write(130);
        writeValue(stream, ((SaveBiometricConsentResult) value).toList());
      } else if (value instanceof WritePasscodeResult) {
        stream.write(131);
        writeValue(stream, ((WritePasscodeResult) value).toList());
      } else if (value instanceof WriteProfileResult) {
        stream.write(132);
        writeValue(stream, ((WriteProfileResult) value).toList());
      } else {
        super.writeValue(stream, value);
      }
    }
  }

  /** Generated interface from Pigeon that represents a handler of messages from Flutter. */
  public interface CommunityPassApi {

    void saveBiometricConsent(@NonNull String reliantGUID, @NonNull String programGUID, Result<SaveBiometricConsentResult> result);

    void getRegisterUserWithBiometrics(@NonNull String reliantGUID, @NonNull String programGUID, @NonNull String consentID, Result<RegisterUserWithBiometricsResult> result);

    void getRegisterBasicUser(@NonNull String reliantGUID, @NonNull String programGUID, Result<RegisterBasicUserResult> result);

    void getWriteProfile(@NonNull String reliantGUID, @NonNull String programGUID, @NonNull String rID, @NonNull Boolean overwriteCard, Result<WriteProfileResult> result);

    void getWritePasscode(@NonNull String reliantGUID, @NonNull String programGUID, @NonNull String rID, @NonNull String passcode, Result<WritePasscodeResult> result);

    /** The codec used by CommunityPassApi. */
    static MessageCodec<Object> getCodec() {
      return CommunityPassApiCodec.INSTANCE;
    }
    /**Sets up an instance of `CommunityPassApi` to handle messages through the `binaryMessenger`. */
    static void setup(BinaryMessenger binaryMessenger, CommunityPassApi api) {
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.CommunityPassApi.saveBiometricConsent", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                try {
                  ArrayList<Object> args = (ArrayList<Object>) message;
                  assert args != null;
                  String reliantGUIDArg = (String) args.get(0);
                  if (reliantGUIDArg == null) {
                    throw new NullPointerException("reliantGUIDArg unexpectedly null.");
                  }
                  String programGUIDArg = (String) args.get(1);
                  if (programGUIDArg == null) {
                    throw new NullPointerException("programGUIDArg unexpectedly null.");
                  }
                  Result<SaveBiometricConsentResult> resultCallback = 
                      new Result<SaveBiometricConsentResult>() {
                        public void success(SaveBiometricConsentResult result) {
                          wrapped.add(0, result);
                          reply.reply(wrapped);
                        }

                        public void error(Throwable error) {
                          ArrayList<Object> wrappedError = wrapError(error);
                          reply.reply(wrappedError);
                        }
                      };

                  api.saveBiometricConsent(reliantGUIDArg, programGUIDArg, resultCallback);
                } catch (Error | RuntimeException exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  reply.reply(wrappedError);
                }
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.CommunityPassApi.getRegisterUserWithBiometrics", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                try {
                  ArrayList<Object> args = (ArrayList<Object>) message;
                  assert args != null;
                  String reliantGUIDArg = (String) args.get(0);
                  if (reliantGUIDArg == null) {
                    throw new NullPointerException("reliantGUIDArg unexpectedly null.");
                  }
                  String programGUIDArg = (String) args.get(1);
                  if (programGUIDArg == null) {
                    throw new NullPointerException("programGUIDArg unexpectedly null.");
                  }
                  String consentIDArg = (String) args.get(2);
                  if (consentIDArg == null) {
                    throw new NullPointerException("consentIDArg unexpectedly null.");
                  }
                  Result<RegisterUserWithBiometricsResult> resultCallback = 
                      new Result<RegisterUserWithBiometricsResult>() {
                        public void success(RegisterUserWithBiometricsResult result) {
                          wrapped.add(0, result);
                          reply.reply(wrapped);
                        }

                        public void error(Throwable error) {
                          ArrayList<Object> wrappedError = wrapError(error);
                          reply.reply(wrappedError);
                        }
                      };

                  api.getRegisterUserWithBiometrics(reliantGUIDArg, programGUIDArg, consentIDArg, resultCallback);
                } catch (Error | RuntimeException exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  reply.reply(wrappedError);
                }
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.CommunityPassApi.getRegisterBasicUser", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                try {
                  ArrayList<Object> args = (ArrayList<Object>) message;
                  assert args != null;
                  String reliantGUIDArg = (String) args.get(0);
                  if (reliantGUIDArg == null) {
                    throw new NullPointerException("reliantGUIDArg unexpectedly null.");
                  }
                  String programGUIDArg = (String) args.get(1);
                  if (programGUIDArg == null) {
                    throw new NullPointerException("programGUIDArg unexpectedly null.");
                  }
                  Result<RegisterBasicUserResult> resultCallback = 
                      new Result<RegisterBasicUserResult>() {
                        public void success(RegisterBasicUserResult result) {
                          wrapped.add(0, result);
                          reply.reply(wrapped);
                        }

                        public void error(Throwable error) {
                          ArrayList<Object> wrappedError = wrapError(error);
                          reply.reply(wrappedError);
                        }
                      };

                  api.getRegisterBasicUser(reliantGUIDArg, programGUIDArg, resultCallback);
                } catch (Error | RuntimeException exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  reply.reply(wrappedError);
                }
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.CommunityPassApi.getWriteProfile", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                try {
                  ArrayList<Object> args = (ArrayList<Object>) message;
                  assert args != null;
                  String reliantGUIDArg = (String) args.get(0);
                  if (reliantGUIDArg == null) {
                    throw new NullPointerException("reliantGUIDArg unexpectedly null.");
                  }
                  String programGUIDArg = (String) args.get(1);
                  if (programGUIDArg == null) {
                    throw new NullPointerException("programGUIDArg unexpectedly null.");
                  }
                  String rIDArg = (String) args.get(2);
                  if (rIDArg == null) {
                    throw new NullPointerException("rIDArg unexpectedly null.");
                  }
                  Boolean overwriteCardArg = (Boolean) args.get(3);
                  if (overwriteCardArg == null) {
                    throw new NullPointerException("overwriteCardArg unexpectedly null.");
                  }
                  Result<WriteProfileResult> resultCallback = 
                      new Result<WriteProfileResult>() {
                        public void success(WriteProfileResult result) {
                          wrapped.add(0, result);
                          reply.reply(wrapped);
                        }

                        public void error(Throwable error) {
                          ArrayList<Object> wrappedError = wrapError(error);
                          reply.reply(wrappedError);
                        }
                      };

                  api.getWriteProfile(reliantGUIDArg, programGUIDArg, rIDArg, overwriteCardArg, resultCallback);
                } catch (Error | RuntimeException exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  reply.reply(wrappedError);
                }
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
      {
        BasicMessageChannel<Object> channel =
            new BasicMessageChannel<>(
                binaryMessenger, "dev.flutter.pigeon.CommunityPassApi.getWritePasscode", getCodec());
        if (api != null) {
          channel.setMessageHandler(
              (message, reply) -> {
                ArrayList<Object> wrapped = new ArrayList<Object>();
                try {
                  ArrayList<Object> args = (ArrayList<Object>) message;
                  assert args != null;
                  String reliantGUIDArg = (String) args.get(0);
                  if (reliantGUIDArg == null) {
                    throw new NullPointerException("reliantGUIDArg unexpectedly null.");
                  }
                  String programGUIDArg = (String) args.get(1);
                  if (programGUIDArg == null) {
                    throw new NullPointerException("programGUIDArg unexpectedly null.");
                  }
                  String rIDArg = (String) args.get(2);
                  if (rIDArg == null) {
                    throw new NullPointerException("rIDArg unexpectedly null.");
                  }
                  String passcodeArg = (String) args.get(3);
                  if (passcodeArg == null) {
                    throw new NullPointerException("passcodeArg unexpectedly null.");
                  }
                  Result<WritePasscodeResult> resultCallback = 
                      new Result<WritePasscodeResult>() {
                        public void success(WritePasscodeResult result) {
                          wrapped.add(0, result);
                          reply.reply(wrapped);
                        }

                        public void error(Throwable error) {
                          ArrayList<Object> wrappedError = wrapError(error);
                          reply.reply(wrappedError);
                        }
                      };

                  api.getWritePasscode(reliantGUIDArg, programGUIDArg, rIDArg, passcodeArg, resultCallback);
                } catch (Error | RuntimeException exception) {
                  ArrayList<Object> wrappedError = wrapError(exception);
                  reply.reply(wrappedError);
                }
              });
        } else {
          channel.setMessageHandler(null);
        }
      }
    }
  }
}

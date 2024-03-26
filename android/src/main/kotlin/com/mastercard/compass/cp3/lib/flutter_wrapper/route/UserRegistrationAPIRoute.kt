package com.mastercard.compass.cp3.lib.flutter_wrapper.route

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.material3.ButtonDefaults
import com.mastercard.compass.base.ResponseStatus
import com.mastercard.compass.cp3.lib.flutter_wrapper.AdditionalInfo
import com.mastercard.compass.cp3.lib.flutter_wrapper.CommunityPassConsentScreenResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.CompassKernelUIController
import com.mastercard.compass.cp3.lib.flutter_wrapper.ConsentResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.ConsentScreenConfig
import com.mastercard.compass.cp3.lib.flutter_wrapper.ConsentStatus
import com.mastercard.compass.cp3.lib.flutter_wrapper.FlutterError
import com.mastercard.compass.cp3.lib.flutter_wrapper.GenerateCpUserProfileResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.OperationMode
import com.mastercard.compass.cp3.lib.flutter_wrapper.RegisterBasicUserResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.RegisterUserWithBiometricsResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.SaveBiometricConsentResult
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.BiometricConsentCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.ConsentScreenDataConfiguration
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.ConsentUICompassApiActivityHandler
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.GenerateCpUserProfileApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.ParcelableCompassConsentResponse
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.ParseColorScheme
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.RegisterBasicUserCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.ui.RegisterUserForBioTokenCompassApiHandlerActivity
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.parseEnrolmentStatus
import com.mastercard.compass.jwt.RegisterUserForBioTokenResponse
import com.mastercard.compass.model.consent.ConsentResponse
import com.mastercard.compass.model.cpuserprofile.GenerateCpUserProfileResponse
import java.util.Base64

class UserRegistrationAPIRoute(
    private val activity: Activity,
    private val helper: CompassKernelUIController.CompassHelper,
) {
    private lateinit var registerUserWithBiometricsResultCallback: (Result<RegisterUserWithBiometricsResult>) -> Unit
    private lateinit var generateCpUserProfileResultCallback: (Result<GenerateCpUserProfileResult>) -> Unit
    private lateinit var saveBiometricConsentResultCallback: (Result<SaveBiometricConsentResult>) -> Unit
    private lateinit var registerBasicUserResultCallback: (Result<RegisterBasicUserResult>) -> Unit
    private lateinit var compassConsentScreenResultCallback: (Result<CommunityPassConsentScreenResult>) -> Unit

    companion object {
        const val SAVE_BIOMETRIC_CONSENT_REQUEST_CODE = 401
        const val BIOMETRIC_USER_REGISTRATION_REQUEST_CODE = 402
        const val BASIC_USER_REGISTRATION_REQUEST_CODE = 403
        const val GENERATE_CP_USER_PROFILE_REQUEST_CODE = 404
        const val COMPASS_CONSENT_SCREEN_REQUEST_CODE = 405
    }

    fun startSaveBiometricConsentIntent(
        reliantGUID: String,
        programGUID: String,
        consumerConsentValue: Boolean,
        callback: (Result<SaveBiometricConsentResult>) -> Unit
    ) {
        saveBiometricConsentResultCallback = callback

        val intent = Intent(activity, BiometricConsentCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.CONSUMER_CONSENT_VALUE, consumerConsentValue)
        }

        activity.startActivityForResult(intent, SAVE_BIOMETRIC_CONSENT_REQUEST_CODE)
    }

    fun startCompassConsentScreenIntent(
        reliantGUID: String,
        programGUID: String,
        consentScreenConfigJson: ConsentScreenConfig?,
        callback: (Result<CommunityPassConsentScreenResult>) -> Unit
    ) {
        compassConsentScreenResultCallback = callback

        val intent = Intent(activity, ConsentUICompassApiActivityHandler::class.java).apply {
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.CONSENT_SCREEN_CONFIGURATION, ConsentScreenDataConfiguration(
                partnerPrivacyPolicyTitle = consentScreenConfigJson?.partnerPrivacyPolicyTitle ?: "Partner Privacy Policy",
                partnerPrivacyPolicyContent = consentScreenConfigJson?.partnerPrivacyPolicyContent ?: "The following is a placeholder text that should be replaced with an actual partner's privacy policy text. When you enrol to Community Pass, Mastercard creates your digital profile to help you and your service provider access and digitally manage records of the services you receive.\n\nTo create your digital profile, we need information about you, including your name, contact details and date of birth. This is to recognize you from other people and ensure we can uniquely identify you in the system. Mastercard is responsible for your profile data.\n\nWe store it securely in the United States. We only create the digital profile with your consent. You decide how your data is used. At any time, you can ask how we use your data or tell us you no longer want your digital profile to exist. We will delete your profile. If you have any such wish, please contact your agent or any nearby service provider.\n\nWhen you withdraw your consent to Community Pass, you have a 30-day period to change your mind and opt-in again. If you do not opt-in again within 30 days after having withdrawn your consent to Community Pass, we will automatically be deleting your profile.\n\nClick the close button to close the Community Pass Privacy Policy and come back to the above Consent Page.",
                partnerPrivacyPolicyExcerptTitle = consentScreenConfigJson?.partnerPrivacyPolicyExcerptTitle ?: "Consent to Partner Reliant Application",
                partnerPrivacyPolicyExcerptContent = consentScreenConfigJson?.partnerPrivacyPolicyExcerptContent ?: "Partner consent excerpt: This is a placeholder text that should be replaced with an actual partner's privacy policy text.",
                acceptConsentButtonLabel = consentScreenConfigJson?.acceptConsentButtonLabel ?: "Accept",
                declineConsentButtonLabel = consentScreenConfigJson?.declineConsentButtonLabel ?: "Decline",
                enableCommunityPassPrivacyPolicy = consentScreenConfigJson?.enableCommunityPassPrivacyPolicy ?: true,
                enableBiometricNotice = consentScreenConfigJson?.enableBiometricNotice ?: true,
                enablePartnerPrivacyPolicy = consentScreenConfigJson?.enablePartnerPrivacyPolicy ?: true,
                beforeYouProceedText = consentScreenConfigJson?.beforeYouProceedText ?: "BEFORE YOU PROCEED",
                beforeYouProceedFontSize = consentScreenConfigJson?.beforeYouProceedFontSize?.toInt() ?: 18,
                consentTitleFontSize = consentScreenConfigJson?.consentTitleFontSize?.toInt() ?: 16,
                consentContentFontSize = consentScreenConfigJson?.consentContentFontSize?.toInt() ?: 14,
                switchLabelFontSize = consentScreenConfigJson?.switchLabelFontSize?.toInt() ?: 14,
                buttonLabelFontSize = consentScreenConfigJson?.buttonLabelFontSize?.toInt() ?: 14,
                buttonBorderRadius = consentScreenConfigJson?.buttonBorderRadius?.toFloat()
                    ?: (ButtonDefaults.MinHeight.value / 2),
                buttonHeight = consentScreenConfigJson?.buttonHeight?.toFloat() ?: ButtonDefaults.MinHeight.value,
                darkThemeColorScheme = if(consentScreenConfigJson?.darkThemeColorScheme == null) null else ParseColorScheme(
                    primary = consentScreenConfigJson.darkThemeColorScheme.primary,
                    onPrimary = consentScreenConfigJson.darkThemeColorScheme.onPrimary,
                    primaryContainer = consentScreenConfigJson.darkThemeColorScheme.primaryContainer,
                    onPrimaryContainer = consentScreenConfigJson.darkThemeColorScheme.onPrimaryContainer,
                    background = consentScreenConfigJson.darkThemeColorScheme.background,
                    onBackground = consentScreenConfigJson.darkThemeColorScheme.onBackground,
                    tertiaryContainer = consentScreenConfigJson.darkThemeColorScheme.tertiaryContainer
                ),
                lightThemeColorScheme = if(consentScreenConfigJson?.lightThemeColorScheme == null) null else ParseColorScheme(
                    primary = consentScreenConfigJson.lightThemeColorScheme.primary,
                    onPrimary = consentScreenConfigJson.lightThemeColorScheme.onPrimary,
                    primaryContainer = consentScreenConfigJson.lightThemeColorScheme.primaryContainer,
                    onPrimaryContainer = consentScreenConfigJson.lightThemeColorScheme.onPrimaryContainer,
                    background = consentScreenConfigJson.lightThemeColorScheme.background,
                    onBackground = consentScreenConfigJson.lightThemeColorScheme.onBackground,
                    tertiaryContainer = consentScreenConfigJson.lightThemeColorScheme.tertiaryContainer
                ),
            ))
        }

        activity.startActivityForResult(intent, COMPASS_CONSENT_SCREEN_REQUEST_CODE)
    }

    fun startGenerateCpUserProfileIntent(
        reliantGUID: String,
        programGUID: String,
        rID: String,
        passcode: String?,
        callback: (Result<GenerateCpUserProfileResult>) -> Unit
    ) {
        generateCpUserProfileResultCallback = callback

        val intent = Intent(activity, GenerateCpUserProfileApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.RID, rID)
            putExtra(Key.PASSCODE, passcode)
        }

        activity.startActivityForResult(intent, GENERATE_CP_USER_PROFILE_REQUEST_CODE)
    }

    fun startBiometricRegistrationIntent(
        reliantGUID: String,
        programGUID: String,
        consentID: String,
        modalities: List<String>,
        operationMode: OperationMode,
        callback: (Result<RegisterUserWithBiometricsResult>) -> Unit
    ) {
        registerUserWithBiometricsResultCallback = callback

        val intent = Intent(activity, RegisterUserForBioTokenCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.CONSENT_ID, consentID)
            putExtra(Key.MODALITIES, modalities as ArrayList<String>)
            putExtra(Key.OPERATION_MODE, operationMode.toString())
        }

        activity.startActivityForResult(intent, BIOMETRIC_USER_REGISTRATION_REQUEST_CODE)
    }

    fun startRegisterBasicUserIntent(
        reliantGUID: String,
        programGUID: String,
        formFactor: String,
        callback: (Result<RegisterBasicUserResult>) -> Unit
    ) {
        registerBasicUserResultCallback = callback

        val intent = Intent(activity, RegisterBasicUserCompassApiHandlerActivity::class.java).apply {
            putExtra(Key.RELIANT_APP_GUID, reliantGUID)
            putExtra(Key.PROGRAM_GUID, programGUID)
            putExtra(Key.FORM_FACTOR, formFactor)
        }

        activity.startActivityForResult(intent, BASIC_USER_REGISTRATION_REQUEST_CODE)
    }

    fun handleSaveBiometricConsentResponse(
        resultCode: Int,
        data: Intent?
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response: ConsentResponse = data?.extras?.get(Key.DATA) as ConsentResponse
                val result = SaveBiometricConsentResult.fromList(
                    listOf(
                        response.consentId,
                        parseConsentResponseStatus(response.responseStatus)
                    )
                )
                Log.e("COMPASS_ERROR_FOUND", "${response.consentId} ${response.responseStatus}")
                saveBiometricConsentResultCallback(Result.success(result))
            }

            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE) ?: "Unknown error"
                Log.e("COMPASS_ERROR_FOUND", "$code $message")
                saveBiometricConsentResultCallback(
                    Result.failure(
                        FlutterError(
                            code.toString(),
                            message,
                            null
                        )
                    )
                )
            }
        }
    }


    fun handleCompassConsentScreenResponse(
        resultCode: Int,
        data: Intent?
    ) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.get(Key.DATA) as ParcelableCompassConsentResponse
                compassConsentScreenResultCallback(Result.success(CommunityPassConsentScreenResult(
                    status = if(response.status?.name == ConsentStatus.CONSENT_GRANTED.name) ConsentStatus.CONSENT_GRANTED else ConsentStatus.CONSENT_DENIED,
                    result = ConsentResult(
                        communityPassPrivacyPolicyAccepted = response.result.communityPassPrivacyPolicyAccepted,
                        communityPassBiometricNoticeAccepted = response.result.communityPassBiometricNoticeAccepted,
                        partnerPrivacyPolicyAccepted = response.result.partnerPrivacyPolicyAccepted,
                        additionalInfo = AdditionalInfo(
                            consentID =  response.result.additionalInfo?.consentID,
                            responseStatus = response.result.additionalInfo?.responseStatus
                        )
                    )
                )))
            }

            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE) ?: "Unknown error"
                Log.e("COMPASS_ERROR_FOUND", "$code $message")
                saveBiometricConsentResultCallback(
                    Result.failure(
                        FlutterError(
                            code.toString(),
                            message,
                            null
                        )
                    )
                )
            }
        }
    }


    fun handleBiometricRegistrationResponse(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val jwt = data?.extras?.get(Key.DATA).toString()
                val response: RegisterUserForBioTokenResponse = helper.parseBioTokenJWT((jwt))

                val result = RegisterUserWithBiometricsResult.fromList(
                    listOf(
                        response.bioToken,
                        response.programGUID,
                        response.rId,
                        parseEnrolmentStatus(response.enrolmentStatus!!)
                    )
                )
                registerUserWithBiometricsResultCallback(Result.success(result))
            }

            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE) ?: "Unknown error"
                Log.e("COMPASS_ERROR_FOUND", "$code $message")
                registerUserWithBiometricsResultCallback(
                    Result.failure(
                        FlutterError(
                            code.toString(),
                            message,
                            null
                        )
                    )
                )
            }
        }
    }

    fun handleRegisterBasicUserResponse(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response = data?.extras?.get(Key.DATA).toString()
                val result = RegisterBasicUserResult.fromList(listOf(response))
                registerBasicUserResultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                registerBasicUserResultCallback(Result.failure(FlutterError(code.toString(), message, null)))
            }
        }
    }


    fun handleGenerateCpUserProfileResponse(resultCode: Int, data: Intent?) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val response: GenerateCpUserProfileResponse = data?.extras?.get(Key.DATA) as GenerateCpUserProfileResponse
                val qrBase64: String = Base64.getEncoder().encodeToString(response.token)

                val result = GenerateCpUserProfileResult.fromList(listOf(qrBase64, response.consumerDeviceNumber, response.message))

                generateCpUserProfileResultCallback(Result.success(result))
            }
            Activity.RESULT_CANCELED -> {
                val code = data?.getIntExtra(Key.ERROR_CODE, 0)
                val message = data?.getStringExtra(Key.ERROR_MESSAGE)!!
                generateCpUserProfileResultCallback(Result.failure(FlutterError(code.toString(), message, null)))
            }
        }
    }
}


private fun parseConsentResponseStatus(responseStatus: ResponseStatus): Int {
    return when (responseStatus) {
        ResponseStatus.SUCCESS -> 0
        ResponseStatus.ERROR -> 1
        ResponseStatus.UNDEFINED -> 2
    }
}

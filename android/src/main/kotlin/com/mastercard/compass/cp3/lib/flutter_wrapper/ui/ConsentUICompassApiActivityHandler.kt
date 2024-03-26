package com.mastercard.compass.cp3.lib.flutter_wrapper.ui


import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.lifecycle.lifecycleScope
import com.mastercard.compass.base.ConsentValue
import com.mastercard.compass.base.Constants
import com.mastercard.compass.cp3.lib.flutter_wrapper.CompassConsentUITheme
import com.mastercard.compass.cp3.lib.flutter_wrapper.CompassKernelUIController
import com.mastercard.compass.cp3.lib.flutter_wrapper.ConsentScreen
import com.mastercard.compass.cp3.lib.flutter_wrapper.saveAllCommunityPassConsent
import com.mastercard.compass.cp3.lib.flutter_wrapper.shouldEnableButton
import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.model.consent.Consent
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


class ConsentUICompassApiActivityHandler: CompassKernelUIController.CompassKernelComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val programGuid = intent.getStringExtra(Key.PROGRAM_GUID)!!
        val reliantGUID = intent.getStringExtra(Key.RELIANT_APP_GUID)!!
        val deserializedConsentScreenConfiguration = intent.extras?.get(Key.CONSENT_SCREEN_CONFIGURATION) as ConsentScreenDataConfiguration

        connectKernelServiceApi(reliantAppGUID = reliantGUID)

        setContent {
            val partnerConsentTitle =
                rememberSaveable { mutableStateOf(deserializedConsentScreenConfiguration.partnerPrivacyPolicyTitle) }.value
            val partnerConsentContent =
                rememberSaveable { mutableStateOf(deserializedConsentScreenConfiguration.partnerPrivacyPolicyContent) }.value
            val partnerConsentExcerptTitle =
                rememberSaveable { mutableStateOf(deserializedConsentScreenConfiguration.partnerPrivacyPolicyExcerptTitle) }.value
            val partnerConsentExcerpt =
                rememberSaveable { mutableStateOf(deserializedConsentScreenConfiguration.partnerPrivacyPolicyExcerptContent) }.value
            val acceptConsentButtonLabel =
                rememberSaveable { mutableStateOf(deserializedConsentScreenConfiguration.acceptConsentButtonLabel) }.value
            val declineConsentButtonLabel =
                rememberSaveable { mutableStateOf(deserializedConsentScreenConfiguration.declineConsentButtonLabel) }.value
            val enableCommunityPassPrivacyPolicyConsent =
                rememberSaveable { mutableStateOf(deserializedConsentScreenConfiguration.enableCommunityPassPrivacyPolicy) }.value
            val enableBiometricNoticeConsent =
                rememberSaveable { mutableStateOf(deserializedConsentScreenConfiguration.enableBiometricNotice) }.value
            val enablePartnerPrivacyPolicyConsent =
                rememberSaveable { mutableStateOf(deserializedConsentScreenConfiguration.enablePartnerPrivacyPolicy) }.value
            val beforeYouProceedText =
                rememberSaveable { mutableStateOf(deserializedConsentScreenConfiguration.beforeYouProceedText) }.value
            val beforeYouProceedFontSize: TextUnit =
                deserializedConsentScreenConfiguration.parsedBeforeYouProceedFontSize
            val consentTitleFontSize: TextUnit =
                deserializedConsentScreenConfiguration.parsedConsentTitleFontSize
            val consentContentFontSize: TextUnit =
                deserializedConsentScreenConfiguration.parsedConsentContentFontSize
            val switchLabelFontSize: TextUnit =
                deserializedConsentScreenConfiguration.parsedSwitchLabelFontSize
            val buttonLabelFontSize: TextUnit =
                deserializedConsentScreenConfiguration.parsedButtonLabelFontSize
            val buttonBorderRadius: Dp = deserializedConsentScreenConfiguration.parsedButtonBorderRadius
            val buttonHeight: Dp = deserializedConsentScreenConfiguration.parsedButtonHeight

            val communityPassPrivacyPolicy = rememberSaveable { mutableStateOf(false) }
            val communityPassBiometricNotice = rememberSaveable { mutableStateOf(false) }
            val partnerPrivacyPolicy = rememberSaveable { mutableStateOf(false) }
            val showCommunityPassPrivacyPolicyPopup = remember { mutableStateOf(false) }
            val showCommunityPassBiometricNoticePopup = remember { mutableStateOf(false) }
            val showPartnerPrivacyPolicyPopup = remember { mutableStateOf(false) }
            val acceptConsentButtonLoading = remember { mutableStateOf(false) }
            val declineConsentButtonLoading = remember { mutableStateOf(false) }
            val continueButtonLoading = remember { mutableStateOf(true) }
            val consentFlag =
                rememberSaveable {
                    mutableStateOf<CompassKernelUIController.CompassHelper.ConsentStatus?>(
                        null
                    )
                }
            val isRepeat = rememberSaveable { mutableStateOf(consentFlag.value != null) }

            val listOfEnabledConsent =
                mutableListOf<CompassKernelUIController.CompassHelper.ActiveConsentList>()
            if (enableCommunityPassPrivacyPolicyConsent) listOfEnabledConsent.add(
                CompassKernelUIController.CompassHelper.ActiveConsentList.COMMUNITY_PASS_PRIVACY_POLICY
            )
            if (enableBiometricNoticeConsent) listOfEnabledConsent.add(CompassKernelUIController.CompassHelper.ActiveConsentList.COMMUNITY_PASS_BIOMETRIC_NOTICE)
            if (enablePartnerPrivacyPolicyConsent) listOfEnabledConsent.add(
                CompassKernelUIController.CompassHelper.ActiveConsentList.PARTNER_PRIVACY_POLICY
            )

            CompassConsentUITheme(
                isDarkTheme = isSystemInDarkTheme(),
                darkThemeColorPalette = getColorScheme(true, deserializedConsentScreenConfiguration.darkThemeColorScheme),
                lightThemeColorPalette = getColorScheme(false, deserializedConsentScreenConfiguration.lightThemeColorScheme),
                content = {
                    ConsentScreen(
                        grantedCommunityPassBiometricNotice = communityPassBiometricNotice,
                        grantedCommunityPassPrivacyPolicy = communityPassPrivacyPolicy,
                        grantedPartnerPrivacyPolicy = partnerPrivacyPolicy,
                        shouldShowCommunityPassBiometricNoticePopup = showCommunityPassBiometricNoticePopup,
                        shouldShowCommunityPassPrivacyPolicyPopup = showCommunityPassPrivacyPolicyPopup,
                        shouldShowPartnerPrivacyPolicyPopup = showPartnerPrivacyPolicyPopup,
                        beforeYouProceedText = beforeYouProceedText,
                        beforeYouProceedFontSize = beforeYouProceedFontSize,
                        consentTitleFontSize = consentTitleFontSize,
                        consentContentFontSize = consentContentFontSize,
                        switchLabelFontSize = switchLabelFontSize,
                        buttonLabelFontSize = buttonLabelFontSize,
                        buttonBorderRadius = buttonBorderRadius,
                        buttonHeight = buttonHeight,
                        partnerPrivacyPolicyTitle = partnerConsentTitle,
                        partnerPrivacyPolicyExcerptContent = partnerConsentExcerpt,
                        partnerPrivacyPolicyContent = partnerConsentContent,
                        enableBiometricNotice = enableBiometricNoticeConsent,
                        enablePartnerPrivacyPolicy = enablePartnerPrivacyPolicyConsent,
                        enableCommunityPassPrivacyPolicy = enableCommunityPassPrivacyPolicyConsent,
                        acceptConsentButtonLabel = acceptConsentButtonLabel,
                        declineConsentButtonLabel = declineConsentButtonLabel,
                        partnerPrivacyPolicyExcerptTitle = partnerConsentExcerptTitle,
                        acceptConsentButtonLoading = acceptConsentButtonLoading,
                        continueButtonLoading = continueButtonLoading,
                        declineConsentButtonLoading = declineConsentButtonLoading,
                        consentFlag = consentFlag,
                        isRepeat = isRepeat,
                        consentCallback = {
                            lifecycleScope.launch {
                                consentCallback(
                                    isCommunityPassPrivacyPolicyAccepted = communityPassPrivacyPolicy.value,
                                    isCommunityPassBiometricNoticeAccepted = communityPassBiometricNotice.value,
                                    isPartnerPrivacyPolicyAccepted = partnerPrivacyPolicy.value,
                                    listOfEnabledConsent = listOfEnabledConsent,
                                    isRepeat = isRepeat.value,
                                    programGUID = programGuid,
                                    flag = consentFlag.value
                                )
                            }
                        },
                        shouldEnableButton = { enableCommunityPassConsent: Boolean, enableBiometricConsent: Boolean, enablePartnerConsent: Boolean, communityPassPrivacyPolicy: Boolean, communityPassBiometricNotice: Boolean, partnerPrivacyPolicy: Boolean ->
                            shouldEnableButton(
                                enableCommunityPassConsent = enableCommunityPassConsent,
                                enableBiometricConsent = enableBiometricConsent,
                                enablePartnerConsent = enablePartnerConsent,
                                grantedCommunityPassPrivacyPolicy = communityPassPrivacyPolicy,
                                grantedCommunityPassBiometricNotice = communityPassBiometricNotice,
                                grantedPartnerPrivacyPolicy = partnerPrivacyPolicy
                            )
                        },
                        scope = lifecycleScope
                    )
                }
            )
        }
    }

    private fun connectKernelServiceApi(
        overrideValidation: Boolean = false,
        reliantAppGUID: String
    ) {
        if (!overrideValidation && hasActiveKernelConnection) {
            return
        }
        connectKernelService(reliantAppGUID) { isSuccess, errorCode, errorMessage ->
            when (isSuccess) {
                true -> {
                    Log.d("CONNECTION", "Connected")
                }

                false -> {
                    Log.d("CONNECTION", "Disconnected")
                }
            }
        }
    }

    private suspend fun consentCallback(
        isCommunityPassPrivacyPolicyAccepted: Boolean,
        isCommunityPassBiometricNoticeAccepted: Boolean,
        isPartnerPrivacyPolicyAccepted: Boolean,
        listOfEnabledConsent: List<CompassKernelUIController.CompassHelper.ActiveConsentList>,
        isRepeat: Boolean,
        programGUID: String,
        flag: CompassKernelUIController.CompassHelper.ConsentStatus?
    ) = coroutineScope {
        lifecycleScope.launch {
            val consentResultValue: CompassKernelUIController.CompassHelper.CompassConsentResponse =
                saveAllCommunityPassConsent(
                    isCommunityPassPrivacyPolicyAccepted = isCommunityPassPrivacyPolicyAccepted,
                    isCommunityPassBiometricNoticeAccepted = isCommunityPassBiometricNoticeAccepted,
                    isPartnerPrivacyPolicyAccepted = isPartnerPrivacyPolicyAccepted,
                    listOfEnabledConsent = listOfEnabledConsent,
                    flag = flag,
                )

            when (consentResultValue.status) {
                CompassKernelUIController.CompassHelper.ConsentStatus.CONSENT_GRANTED -> {
                    val responseData =
                        if (consentResultValue.result.communityPassBiometricNoticeAccepted == true) {
                            val deferred = async {
                                compassKernelServiceInstance.saveBiometricConsent(
                                    consent = Consent(
                                        consentValue = ConsentValue.ACCEPT,
                                        programID = programGUID
                                    )
                                )
                            }

                            val compassConsentResult = deferred.await()

                            ParcelableCompassConsentResponse(
                                status = consentResultValue.status,
                                result = ParcelableConsentResult(
                                    communityPassPrivacyPolicyAccepted = consentResultValue.result.communityPassPrivacyPolicyAccepted,
                                    communityPassBiometricNoticeAccepted = consentResultValue.result.communityPassBiometricNoticeAccepted,
                                    partnerPrivacyPolicyAccepted = consentResultValue.result.partnerPrivacyPolicyAccepted,
                                    additionalInfo = ParcelableConsentAdditionalInfo(
                                        consentID = compassConsentResult.consentId,
                                        responseStatus = compassConsentResult.responseStatus.name
                                    )
                                )
                            )
                        } else {
                            ParcelableCompassConsentResponse(
                                status = consentResultValue.status,
                                result = ParcelableConsentResult(
                                    communityPassPrivacyPolicyAccepted = consentResultValue.result.communityPassPrivacyPolicyAccepted,
                                    communityPassBiometricNoticeAccepted = consentResultValue.result.communityPassBiometricNoticeAccepted,
                                    partnerPrivacyPolicyAccepted = consentResultValue.result.partnerPrivacyPolicyAccepted,
                                    additionalInfo = null
                                )
                            )
                        }

                    val responseIntent = Intent().apply { putExtra(Key.DATA, responseData) }
                    this@ConsentUICompassApiActivityHandler.setResult(RESULT_OK, responseIntent)
                }

                CompassKernelUIController.CompassHelper.ConsentStatus.CONSENT_DENIED -> {
                    val responseData = ParcelableCompassConsentResponse(
                        status = consentResultValue.status,
                        result = ParcelableConsentResult(
                            communityPassPrivacyPolicyAccepted = consentResultValue.result.communityPassPrivacyPolicyAccepted,
                            communityPassBiometricNoticeAccepted = consentResultValue.result.communityPassBiometricNoticeAccepted,
                            partnerPrivacyPolicyAccepted = consentResultValue.result.partnerPrivacyPolicyAccepted,
                            additionalInfo = null
                        )
                    )

                    val responseIntent = Intent().apply { putExtra(Key.DATA, responseData) }
                    this@ConsentUICompassApiActivityHandler.setResult(RESULT_OK, responseIntent)
                }

                else -> {
                    if (isRepeat) {
                        val responseIntent = Intent().apply {
                            putExtra(Key.ERROR_CODE, 0)
                            putExtra(Key.ERROR_MESSAGE, "Something went wrong")
                        }
                        this@ConsentUICompassApiActivityHandler.setResult(RESULT_CANCELED, responseIntent)
                    }
                }
            }
            this@ConsentUICompassApiActivityHandler.finish()
        }
    }
}


@Parcelize
@Serializable
data class ParcelableCompassConsentResponse(val status: CompassKernelUIController.CompassHelper.ConsentStatus?, val result: ParcelableConsentResult): Parcelable
@Parcelize
@Serializable
data class ParcelableConsentResult(val communityPassPrivacyPolicyAccepted: Boolean?, val communityPassBiometricNoticeAccepted: Boolean?, val partnerPrivacyPolicyAccepted: Boolean?, var additionalInfo: ParcelableConsentAdditionalInfo? = null): Parcelable
@Parcelize
@Serializable
data class ParcelableConsentAdditionalInfo(val consentID: String, val responseStatus: String): Parcelable

@Parcelize
@Serializable
data class ConsentScreenDataConfiguration(
    val partnerPrivacyPolicyTitle: String = "Partner Privacy Policy",
    val partnerPrivacyPolicyContent: String = "The following is a placeholder text that should be replaced with an actual partner's privacy policy text. When you enrol to Community Pass, Mastercard creates your digital profile to help you and your service provider access and digitally manage records of the services you receive.\n\nTo create your digital profile, we need information about you, including your name, contact details and date of birth. This is to recognize you from other people and ensure we can uniquely identify you in the system. Mastercard is responsible for your profile data.\n\nWe store it securely in the United States. We only create the digital profile with your consent. You decide how your data is used. At any time, you can ask how we use your data or tell us you no longer want your digital profile to exist. We will delete your profile. If you have any such wish, please contact your agent or any nearby service provider.\n\nWhen you withdraw your consent to Community Pass, you have a 30-day period to change your mind and opt-in again. If you do not opt-in again within 30 days after having withdrawn your consent to Community Pass, we will automatically be deleting your profile.\n\nClick the close button to close the Community Pass Privacy Policy and come back to the above Consent Page.",
    val partnerPrivacyPolicyExcerptTitle: String = "Consent to Partner Reliant Application",
    val partnerPrivacyPolicyExcerptContent: String = "Partner consent excerpt: This is a placeholder text that should be replaced with an actual partner's privacy policy text.",
    val acceptConsentButtonLabel: String = "Accept",
    val declineConsentButtonLabel: String = "Decline",
    val enableCommunityPassPrivacyPolicy: Boolean = true,
    val enableBiometricNotice: Boolean = true,
    val enablePartnerPrivacyPolicy: Boolean = true,
    val beforeYouProceedText: String = "BEFORE YOU PROCEED",
    val beforeYouProceedFontSize: Int = 18,
    val consentTitleFontSize: Int = 16,
    val consentContentFontSize: Int = 14,
    val switchLabelFontSize: Int = 14,
    val buttonLabelFontSize: Int = 14,
    val buttonBorderRadius: Float = ButtonDefaults.MinHeight.value / 2,
    val buttonHeight: Float = ButtonDefaults.MinHeight.value,
    val darkThemeColorScheme: ParseColorScheme? = null,
    val lightThemeColorScheme: ParseColorScheme? = null,
) : Parcelable {
    val parsedBeforeYouProceedFontSize
        get() = TextUnit(value = beforeYouProceedFontSize.toFloat(), TextUnitType.Sp)
    val parsedConsentTitleFontSize
        get() = TextUnit(value = consentTitleFontSize.toFloat(), TextUnitType.Sp)
    val parsedConsentContentFontSize
        get() = TextUnit(value = consentContentFontSize.toFloat(), TextUnitType.Sp)
    val parsedSwitchLabelFontSize
        get() = TextUnit(value = switchLabelFontSize.toFloat(), TextUnitType.Sp)
    val parsedButtonLabelFontSize
        get() = TextUnit(value = buttonLabelFontSize.toFloat(), TextUnitType.Sp)
    val parsedButtonBorderRadius
        get() = Dp(value = buttonBorderRadius)
    val parsedButtonHeight
        get() = Dp(value = buttonHeight)
}


@Parcelize
@Serializable
data class ParseColorScheme(
    val primary: String,
    val onPrimary: String,
    val primaryContainer: String,
    val onPrimaryContainer: String,
    val background: String,
    val onBackground: String,
    val tertiaryContainer: String,
) : Parcelable {
    val primaryParsed
        get() = Color(android.graphics.Color.parseColor(primary))
    val onPrimaryParsed
        get() = Color(android.graphics.Color.parseColor(onPrimary))
    val primaryContainerParsed
        get() = Color(android.graphics.Color.parseColor(primaryContainer))
    val onPrimaryContainerParsed
        get() = Color(android.graphics.Color.parseColor(onPrimaryContainer))
    val backgroundParsed
        get() = Color(android.graphics.Color.parseColor(background))
    val onBackgroundParsed
        get() = Color(android.graphics.Color.parseColor(onBackground))
    val tertiaryContainerParsed
        get() = Color(android.graphics.Color.parseColor(tertiaryContainer))
}


fun getColorScheme(isDarkMode: Boolean, colorScheme: ParseColorScheme?): ColorScheme {
    when (isDarkMode) {
        true -> {
            if (colorScheme == null) {
                return darkColorScheme(
                    primary = Color(0xFFFF671B),
                    onPrimary = Color.White,
                    primaryContainer = Color(0xFF333333),
                    onPrimaryContainer = Color(0x99FFFFFF),
                    background = Color.Black,
                    onBackground = Color(0x99FFFFFF),
                    tertiaryContainer = Color(0xFFFFE1D1)
                )
            } else {
                return darkColorScheme(
                    primary = colorScheme.primaryParsed,
                    onPrimary = colorScheme.onPrimaryParsed,
                    primaryContainer = colorScheme.primaryContainerParsed,
                    onPrimaryContainer = colorScheme.onPrimaryContainerParsed,
                    background = colorScheme.backgroundParsed,
                    onBackground = colorScheme.onBackgroundParsed,
                    tertiaryContainer = colorScheme.tertiaryContainerParsed
                )
            }
        }
        false -> {
            if (colorScheme == null) {
                return lightColorScheme(
                    primary = Color(0xFFFF671B),
                    onPrimary = Color.White,
                    primaryContainer = Color.White,
                    onPrimaryContainer = Color.Black,
                    background = Color(0xFFFFE1D1),
                    onBackground = Color.Black,
                    tertiaryContainer = Color(0xFFFFE1D1)
                )
            } else {
                return lightColorScheme(
                    primary = colorScheme.primaryParsed,
                    onPrimary = colorScheme.onPrimaryParsed,
                    primaryContainer = colorScheme.primaryContainerParsed,
                    onPrimaryContainer = colorScheme.onPrimaryContainerParsed,
                    background = colorScheme.backgroundParsed,
                    onBackground = colorScheme.onBackgroundParsed,
                    tertiaryContainer = colorScheme.tertiaryContainerParsed
                )
            }
        }
    }
}
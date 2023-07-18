package com.mastercard.compass.cp3.lib.flutter_wrapper.util

object Key {
    // Keys
    const val RELIANT_APP_GUID = "RELIANT_APP_GUID"
    const val RID = "RID"
    const val MODALITIES = "MODALITIES"
    const val OPERATION_MODE = "OPERATION_MODE"
    const val PASSCODE = "PASSCODE"
    const val CONSENT_ID = "CONSENT_ID"
    const val OVERWRITE_CARD = "OVERWRITE_CARD"
    const val CONSUMER_CONSENT_VALUE = "CONSUMER_CONSENT_VALUE"

    const val FACE = "FACE"
    const val LEFT_PALM = "LEFT_PALM"
    const val RIGHT_PALM = "RIGHT_PALM"
    const val UNIT = "UNIT"
    const val TYPE = "TYPE"
    const val E_VOUCHER_TYPE = "E_VOUCHER_TYPE"




    ////


    const val FORM_FACTOR = "FORM_FACTOR"


        // Request
        const val REQUEST_DATA = "REQUEST_DATA"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val PROGRAM_GUID = "PROGRAM_GUID"
        const val RELIANT_GUID = "RELIANT_GUID"
    const val CONSUMER_DEVICE_NUMBER = "CONSUMER_DEVICE_NUMBER"

        // Response
        const val RESPONSE_DATA = "RESPONSE_DATA"
        const val RESPONSE_ERROR = "RESPONSE_ERROR"

        // General
        const val DATA = "DATA"
        const val ENABLE_TORCH = "ENABLE_TORCH"
        const val ENABLE_BEEP = "ENABLE_BEEP"
        const val FACE_MODALITY = "FACE"
        const val LEFT_PALM_MODALITY = "LEFT_PALM"
        const val RIGHT_PALM_MODALITY = "RIGHT_PALM"
        const val BEST_AVAILABLE = "BEST_AVAILABLE"
        const val QR = "QR"
        const val NORMALIZED_SCORE = "normalizedScore"
        const val DISTANCE = "distance"
        const val MODALITY = "modality"

    const val PROGRAM_SPACE_DATA = "PROGRAM_SPACE_DATA"
    const val ENCRYPT_DATA = "ENCRYPT_DATA"

        // Error
        const val ERROR_CODE = "ERROR_CODE"
        const val ERROR_MESSAGE = "ERROR_MESSAGE"


    object UnknownError {
        const val CODE = 0
        const val MESSAGE = "Unknown error. Something went wrong."
        const val QR_ERROR_MESSAGE = "Capture failed."
    }
}
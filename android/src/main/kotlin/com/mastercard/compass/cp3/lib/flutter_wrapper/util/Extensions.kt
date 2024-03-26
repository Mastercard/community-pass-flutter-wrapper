package com.mastercard.compass.cp3.lib.flutter_wrapper.util;

import com.mastercard.compass.base.EnrolmentStatus
import com.mastercard.compass.base.OperationMode;
import com.mastercard.compass.model.biometric.BiometricMatchResult
import com.mastercard.compass.model.biometrictoken.FormFactor;
import com.mastercard.compass.model.biometrictoken.Modality;

fun populateModalityList (modalities: List<String>) : MutableList<Modality> {
    val listOfModalities = mutableListOf<Modality>()

    modalities.forEach {
        if (it == Key.FACE) listOfModalities.add(Modality.FACE)
        if (it == Key.LEFT_PALM) listOfModalities.add(Modality.LEFT_PALM)
        if (it == Key.RIGHT_PALM) listOfModalities.add(Modality.RIGHT_PALM)
    }
    return listOfModalities
}

fun getOperationMode(operationMode: String): OperationMode {
    return if (operationMode == Key.BEST_AVAILABLE) OperationMode.BEST_AVAILABLE else OperationMode.FULL
}

fun getFormFactor(value: String): FormFactor {
    return when (value) {
        "NONE" -> FormFactor.NONE
        "CARD" -> FormFactor.CARD
        "QR" -> FormFactor.QR
        else -> FormFactor.NONE
    }
}

fun getMatchListArray(list: List<BiometricMatchResult>?): ArrayList<Map<String, String>> {
    val matchArray = ArrayList<Map<String, String>>()
    list?.forEach {
        val matchMap = mapOf(
            Key.MODALITY to it.modality,
            Key.DISTANCE to it.distance.toString(),
            Key.NORMALIZED_SCORE to it.normalizedScore.toString()
        )
        matchArray.add(matchMap)
    }
    return matchArray
}

fun parseEnrolmentStatus(enrolmentStatus: EnrolmentStatus): Int {
    return when(enrolmentStatus){
        EnrolmentStatus.EXISTING -> 0
        EnrolmentStatus.NEW -> 1
    }
}
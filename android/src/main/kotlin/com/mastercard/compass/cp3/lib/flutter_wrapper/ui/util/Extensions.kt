package com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util

import com.mastercard.compass.cp3.lib.flutter_wrapper.util.Key
import com.mastercard.compass.model.biometrictoken.Modality
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun BigDecimal.toReadableString(sign: String? = null): String {
    val format = DecimalFormat("#,##0.00")
    return (sign?.let { "$it " } ?: "").plus(format.format(this))
}

fun Date.toReadableString(): String {
    val format = SimpleDateFormat("dd MMM yy | HH:mm")
    return format.format(this)
}


fun populateModalityList (modalities: ArrayList<String>) : MutableList<Modality> {
    val listOfModalities = mutableListOf<Modality>()

    modalities.forEach {
        if(it == Key.FACE_MODALITY) listOfModalities.add(Modality.FACE)
        if(it == Key.LEFT_PALM_MODALITY) listOfModalities.add(Modality.LEFT_PALM)
        if(it == Key.RIGHT_PALM_MODALITY) listOfModalities.add(Modality.RIGHT_PALM)
    }

    return listOfModalities
}
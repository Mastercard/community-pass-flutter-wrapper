package com.mastercard.compass.cp3.lib.flutter_wrapper.ui.util

data class SharedSpace(
    val user: User,
    val collectionInfo: CollectionInfo = CollectionInfo()
)

data class CollectionInfo(
    val noOfPaidCollections: Int = 0,
    val totalAmountPaid: Double = 0.0,
    val lastAmountPaidOut: Double = 0.0,
    val lastPayoutTimeStamp: Long = 0,
    val firstCollection: Collection = Collection(),
    val secondCollection: Collection = Collection(),
    val thirdCollection: Collection = Collection(),
    val fourthCollection: Collection = Collection(),
    val fifthCollection: Collection = Collection()
)

data class Collection(
    val id: String = "",
    val produceId: String = "",
    val produceName: String = "",
    val weightInGrams: Int = 0,
    val amountPerGram: Float = 0f,
    val amount: Double = 0.0,
    val timeStamp: Long = 0
)

data class User(
    val identifier: String,
    val mobileNumber: String,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val address: String,
    val rId: String,
    val consentId: String? = null,
    val consumerDeviceId: String? = null
)
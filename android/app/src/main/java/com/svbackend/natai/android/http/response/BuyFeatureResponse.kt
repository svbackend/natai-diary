package com.svbackend.natai.android.http.response

data class BuyFeatureResponse(
    val checkoutUrl: String,
    val customerId: String,
    val ephemeralKey: String?,
    val paymentIntentSecret: String,
)

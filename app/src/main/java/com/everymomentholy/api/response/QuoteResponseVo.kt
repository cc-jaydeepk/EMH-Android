package com.everymomentholy.api.response

data class QuoteResponseVo(
    val status: String,
    val statusCode: Int,
    var response: QuoteVo
)

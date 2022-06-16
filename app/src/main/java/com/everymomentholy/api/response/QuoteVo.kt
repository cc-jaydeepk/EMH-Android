package com.everymomentholy.api.response

data class QuoteVo(
    val date: String,
    val message: String,
    val parentLiturgy: String,
    val previousquotes: ArrayList<QuotePreviousquoteVo>,
    val quote: String
)
package com.everymomentholy.api.response

import java.io.Serializable

class GetLiturgiesDataVo : Serializable {

    var volumeId: Int = 0
    var volumeTitle: String = ""
    var volumeAmount: String = ""
    var volumeCoverPageImage: String = ""
    var discountAmount: String = ""

    var isPurchased: String = ""
    var isVolume: String = ""
    var bookId: Int = 0
    var LiturgyAvailable: String = ""
    var bookAmount: String = ""

    var bookCoverPageImage: String = ""
    var bookTitle: String = ""
    var isFreeLiturgyAvailable: String = ""
    var isClicked: Boolean = false
    var isFree: String = ""

}
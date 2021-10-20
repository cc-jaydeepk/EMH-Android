package com.everymomentholy.api.response

import java.io.Serializable

class CollectionDataVo :Serializable{

    var LiturgyAvailable: String = ""

    var bookAmount: String = ""

    var bookCoverPageImage: String = ""

    var bookId: Int = 0

    var bookTitle: String = ""

    var isFreeLiturgyAvailable: String = ""

    var isPurchased: String = ""
}
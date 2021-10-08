package com.everymomentholy.api.response

class DataVo {

    //dailyLiturgies API Response
    var notificationId: Int = 1

    var message: String = ""

    var title: String = ""

    var createdAt: String = ""

    //getLiturgies API Response
    var bookId : Int = 1
    var chapterId: Int = 1
    var chapterPageImage: String = ""
    var chapterTitle: String = ""
    var chapterUrl: String = ""
    var isFavorite: String = ""
    var isFeatured: String = ""
    var isFree: String = ""
    var isPurchased: String = ""
    var keyWords: String = ""
    var price: String = ""

    //getBooks API Response
    var LiturgyAvailable: String = ""
    var bookAmount: String = ""
    var bookCoverPageImage: String =""
    //var bookId: Int = 1
    var bookTitle: String = ""
    var discountAmount: String = ""
    var isFreeLiturgyAvailable: String = ""
    //var isPurchased: String = ""
    var isVolume: String = ""
    var volumeAmount: String = ""
    var volumeCoverPageImage: String = ""
    var volumeId: Int = 1
    var volumeTitle: String = ""
}
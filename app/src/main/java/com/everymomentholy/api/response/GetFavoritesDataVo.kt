package com.everymomentholy.api.response

import java.io.Serializable

class GetFavoritesDataVo: Serializable {
    var bookId: Int = -1
    var chapterId: Int = -1
    var chapterPageImage: String = ""
    var chapterTitle: String = ""
    var chapterUrl: String = ""
    var isFavorite: String = ""
    var isFeatured: String = ""
    var isFree: String = ""
    var isPurchased: String = ""
    var price: String = ""
    var audio_file: String = ""
    var volumeTags: String = ""


    var bookTitle: String = ""
    var bookAmount: String = ""
    var bookPurchaseCode: String = ""
    var bookCoverPageImage: String = ""
    var isVolume: String = ""
    var LiturgyAvailable: String = ""
    var isFreeLiturgyAvailable: String = ""
    var liturgyCount: String = ""
}
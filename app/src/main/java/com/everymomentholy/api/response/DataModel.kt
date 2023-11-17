package com.everymomentholy.api.response

sealed class DataModel {
    data class GetFavLiturgyDataVo(
        var bookId: Int = -1,
        var chapterId: Int = -1,
        var chapterPageImage: String = "",
        var chapterTitle: String = "",
        var chapterUrl: String = "",
        var isFavorite: String = "",
        var isFeatured: String = "",
        var isFree: String = "",
        var isPurchased: String = "",
        var price: String = ""
    ) : DataModel()

    data class GetFavBookDataVo(
        var bookId: Int = -1,
        var bookTitle: String = "",
        var bookAmount: String = "",
        var bookPurchaseCode: String = "",
        var bookCoverPageImage: String = "",
        var isPurchased: String = "",
        var isVolume: String = "",
        var LiturgyAvailable: String = "",
        var isFreeLiturgyAvailable: String = "",
        var liturgyCount: String = "",
        var isFavorite: String = "",
        var isFree: String = "",
        var isFeatured: String = ""
    ) : DataModel()
}

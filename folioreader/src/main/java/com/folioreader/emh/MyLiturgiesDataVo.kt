package com.folioreader.emh

import com.folioreader.util.ProductTypes
import java.io.Serializable

class MyLiturgiesDataVo: Serializable {
    var bookId: Int = 1
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
    var userId: Int = 0
    var token: String = ""
    var productType: ProductTypes = ProductTypes.LITURGY
    var liturgyPurchaseCode: String = ""
    var audio_file: String = ""
    var volumeTags: String = ""

   // var productType: ProductTypes = ProductTypes.LITURGY
}


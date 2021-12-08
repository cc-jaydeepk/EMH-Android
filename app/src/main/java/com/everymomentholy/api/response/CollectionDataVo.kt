package com.everymomentholy.api.response

import com.everymomentholy.utils.ProductTypes
import java.io.Serializable

class CollectionDataVo :Serializable{

    var LiturgyAvailable: String = ""

    var bookAmount: String = ""

    var bookCoverPageImage: String = ""

    var bookId: Int = 0

    var bookTitle: String = ""

    var isFreeLiturgyAvailable: String = ""

    var isPurchased: String = ""

    var discountAmount: String = ""

    var volumeId: Int = 0

    var productTypes: ProductTypes = ProductTypes.BOOK
}
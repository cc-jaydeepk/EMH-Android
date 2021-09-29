package com.everymomentholy.model

class LiturgiesModel(title: String?, image: String?) {

    private var title: String
    private var image: String

    init {
        this.title = title!!
        this.image = image!!
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(name: String?) {
        title = name!!
    }

    fun getImage(): String? {
        return image
    }

    fun setImage(genre: String?) {
        this.image = image!!
    }

}
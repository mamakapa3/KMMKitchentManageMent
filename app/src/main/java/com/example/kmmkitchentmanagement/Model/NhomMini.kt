package com.example.kmmkitchentmanagement.Model

import java.io.Serializable
import java.util.Date

data class NhomMini(
    var title: String = "",
    var tacGias: List<TacGia> = emptyList(),
    var isPublished: Boolean = false,
    var addTime: Date? = null,
    var imgUrl: String = ""
) : Serializable
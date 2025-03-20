package com.example.kmmkitchentmanagement.Model

import java.io.Serializable
import java.util.Date

data class Nhom(
    var id: String = "",
    var title: String = "Not null",
    var description: String = "",
    var addTime: Date = Date(),
    var publishedDate: Date = Date(),
    var isPublished: Boolean = false,
    var citation: String = "",
    var researchField: String = "",
    var tacGias: String = "",
    var marked: Boolean = false
) : Serializable {

    override fun toString(): String {
        return """
        LuanVan {
            id: $id
            title: $title
            description: $description
            addTime: $addTime
            citation: $citation
            isPublished: $isPublished
            publishedDate: $publishedDate
            researchField: $researchField
            tacGias: $tacGias
            marked: $marked
        }
    """.trimIndent()
    }

}

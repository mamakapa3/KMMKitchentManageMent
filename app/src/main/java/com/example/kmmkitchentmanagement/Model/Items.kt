package com.example.kmmkitchentmanagement.Model

import java.io.Serializable
import java.util.Date

data class Items(
    var id: String = "",
    var title: String = "Not null",
    var description: String = "",
    var addTime: Date = Date(),
//    var publishedDate: Date = Date(),
    var supplier: String = "",
    var itemType: String = "",
) : Serializable {

    override fun toString(): String {
        return """
        LuanVan {
            id: $id
            title: $title
            description: $description
            addTime: $addTime
            supplier: $supplier
            itemType: $itemType
        }
    """.trimIndent()
    }

}
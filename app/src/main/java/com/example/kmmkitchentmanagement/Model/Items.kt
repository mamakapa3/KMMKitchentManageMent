package com.example.kmmkitchentmanagement.Model

import java.io.Serializable
import java.util.Date

data class Items(
    var id: String = "",
    var title: String = "Not null",
    var number: Int = 0,
    var description: String = "",
    var addTime: Date = Date(),
//    var publishedDate: Date = Date(),
    var supplier: String = "",
    var itemType: String = "",
) : Serializable {

    override fun toString(): String {
        return """
        Items {
            id: $id
            title: $title
            number: $number
            description: $description
            addTime: $addTime
            supplier: $supplier
            itemType: $itemType
        }
    """.trimIndent()
    }

}
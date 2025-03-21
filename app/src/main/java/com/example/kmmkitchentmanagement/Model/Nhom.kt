package com.example.kmmkitchentmanagement.Model

import java.io.Serializable
import java.util.Date

data class Nhom(
    var id: String = "",
    var title: String = "Not null",
    var addTime: Date = Date(),
) : Serializable {

    override fun toString(): String {
        return """
        Nhom {
            id: $id
            title: $title
            addTime: $addTime
        }
    """.trimIndent()
    }

}

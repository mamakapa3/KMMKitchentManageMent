package com.example.kmmkitchentmanagement.Model

import java.io.Serializable
import java.util.Date

data class Nhom(
    var id: String = "",
    var title: String = "Not null",
    var addTime: Date = Date(),
    var memNumb: Int = 0,
    var members: List<String> = listOf()
) : Serializable {

    override fun toString(): String {
        return """
        Nhom {
            id: $id
            title: $title
            addTime: $addTime
            memNumb: $memNumb
            members: $members
        }
    """.trimIndent()
    }
}

package com.example.kmmkitchentmanagement.Model

import android.icu.text.Transliterator.Position
import java.io.Serializable
import java.util.Date

data class Supplier(
    var id: String = "",
    var name: String = "Not null",
    var itemType: String ="Not null",
    var location: String ="Not null",
    var status: Boolean = false
) : Serializable {
    override fun toString(): String {
        return """
        Supplier {
            id: $id
            name: $name
            itemType: $itemType
            location: $location
            status: $status 
        }
    """.trimIndent()
    }

}
package com.example.kmmkitchentmanagement.Model

import java.io.Serializable

data class TacGia(
    var name: String = "",
    var phoneNumber: String = "",
    var emailAddress: String = ""
) : Serializable {

    override fun toString(): String {
        return "$name$phoneNumber$emailAddress"
    }
}
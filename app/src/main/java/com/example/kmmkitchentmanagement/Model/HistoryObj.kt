package com.example.kmmkitchentmanagement.Model

import java.util.Date

data class historyObj(
    var time: Date? = null,
    var event: String? = null
) {
    override fun toString(): String {
        return "${time.toString()}\n\t$event\n\n"
    }
}

package com.example.kmmkitchentmanagement.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NguoiDung(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var role: Int = -1
) : Parcelable {

    fun getRole(): String {
        return when (role) {
            1510 -> "Nhà phát triển"
            1 -> "Quản trị viên"
            2 -> "Người dùng"
            else -> "Không xác định"
        }
    }

    fun isFullVar(): Boolean {
        return name.isNotBlank() && email.isNotBlank() && role in listOf(1510, 1, 2)
    }

    override fun toString(): String {
        return "NguoiDung(userId='$userId', name='$name', email='$email', role=$role)"
    }
}

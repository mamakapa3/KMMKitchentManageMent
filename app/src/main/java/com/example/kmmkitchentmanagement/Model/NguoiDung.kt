package com.example.kmmkitchentmanagement.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NguoiDung(
    var name: String? = null,
    var email: String? = null,
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
        return !name.isNullOrEmpty() && !email.isNullOrEmpty() && getRole() != "Không xác định"
    }

    override fun toString(): String {
        return "NguoiDung(name='$name', email='$email', role=$role)"
    }
}

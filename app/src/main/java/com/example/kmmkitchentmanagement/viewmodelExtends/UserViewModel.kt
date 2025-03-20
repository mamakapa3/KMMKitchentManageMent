package com.example.kmmkitchentmanagement.viewmodelExtends

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kmmkitchentmanagement.Model.NguoiDung
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class UserViewModel : ViewModel() {

    private val userLiveData = MutableLiveData<NguoiDung>()
    private val loginSuccessLiveData = MutableLiveData<Boolean>()
    private val errorLiveData = MutableLiveData<String>()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getUser(): LiveData<NguoiDung> = userLiveData
    fun getLoginSuccess(): LiveData<Boolean> = loginSuccessLiveData
    fun getError(): LiveData<String> = errorLiveData

    fun fetchUserData(userEmail: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("User")
            .whereEqualTo("email", userEmail)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        for (document: QueryDocumentSnapshot in querySnapshot) {
                            val name = document.getString("name") ?: ""
                            val email = document.getString("email") ?: ""
                            val role = document.getLong("role")?.toInt() ?: 0
                            userLiveData.value = NguoiDung(name, email, role)
                        }
                    } else {
                        Log.d("UserViewModel", "No user data found")
                        errorLiveData.value = "No user data found for email: $userEmail"
                    }
                } else {
                    Log.e("UserViewModel", "Error getting user data", task.exception)
                    errorLiveData.value = "Error getting user data: ${task.exception?.message}"
                }
            }
    }
}

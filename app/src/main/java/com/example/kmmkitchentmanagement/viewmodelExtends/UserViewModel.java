package com.example.kmmkitchentmanagement.viewmodelExtends;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.kmmkitchentmanagement.Model.NguoiDung;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<NguoiDung> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public LiveData<NguoiDung> getUser() {
        return userLiveData;
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccessLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void fetchUserData(String userEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for(QueryDocumentSnapshot document : querySnapshot){
                                String name = document.getString("name");
                                String email = document.getString("email");
                                int role = document.getLong("role").intValue();
                                userLiveData.setValue(new NguoiDung(name, email, role));
                            }
                        } else {
                            Log.d("UserViewModel", "No user data found");
                            errorLiveData.setValue("No user data found for email: " + userEmail);
                        }
                    } else {
                        Log.e("UserViewModel", "Error getting user data", task.getException());
                        errorLiveData.setValue("Error getting user data: " + task.getException().getMessage());
                    }
                });
    }
}
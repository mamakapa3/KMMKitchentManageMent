package com.example.kmmkitchentmanagement.interfaceFile;

public interface EmailCallback {
    void onEmailRetrieved(String email);
    void onErrorSearching();
    void onErrorETC();
}

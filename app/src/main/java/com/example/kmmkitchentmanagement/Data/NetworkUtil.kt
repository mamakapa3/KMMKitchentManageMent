package com.example.kmmkitchentmanagement.Data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

object NetworkUtil {

fun isWifiConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    connectivityManager?.let {
        val network = it.activeNetwork
        if (network == null) {
            return false // No active network
        }

        val networkCapabilities = it.getNetworkCapabilities(network)
        if (networkCapabilities == null) {
            return false // No network capabilities found
        }

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    return false
}
}

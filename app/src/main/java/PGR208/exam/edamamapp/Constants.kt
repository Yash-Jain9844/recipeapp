package PGR208.exam.edamamapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

// Object to store constant values and utility functions used across the app
object Constants {
    // Base URL for TheMealDB API
    const val BASE_URL: String = "https://www.themealdb.com/api/json/v1/1/"
    // Request code for internet permission
    const val INTERNET_PERMISSION_REQUEST_CODE = 1

    // Utility function to check if network is available
    fun isNetworkAvailable(context: Context): Boolean {
        // Get connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // Get active network, return false if none
        val network = connectivityManager.activeNetwork ?: return false
        // Get network capabilities, return false if none
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        // Check for supported network transports
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
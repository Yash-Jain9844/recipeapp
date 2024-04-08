package PGR208.exam.edamamapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object Constants {

    const val APP_KEY: String = "cb831881c277fe8f7011fd8fca7b5db2" //"e045baf5336d7b80d99f0aa32050cbce"//"c5c25a28e09370d20e378fd1206917e9"
    const val APP_ID: String = "14edf83e"//4d29f052" "5751be8b"
    const val BASE_URL: String = "https://api.edamam.com/"

    const val INTERNET_PERMISSION_REQUEST_CODE = 1

    fun isNetworkAvailable(context: Context) : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when{
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}
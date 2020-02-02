package com.sample.testapplication.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import org.jsoup.Jsoup
import java.net.MalformedURLException


object AndroidUtils {

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.window.decorView.rootView.windowToken,
            0
        )
    }

    fun isUrlValid(url: String?): Boolean {
        if (url == null) return false

        val pattern = Patterns.WEB_URL
        val matcher = pattern.matcher(url)
        return matcher.matches()
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            return capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } else {
            try {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                return activeNetworkInfo != null && activeNetworkInfo.isConnected
            } catch (ignored: Exception) {
            }
        }

        return false
    }

    @Throws(Exception::class)
    fun getValidUrl(url: String, isThrown: Boolean = false): String {
        var newUrl = url
        return try {
            if (url.startsWith("http"))
                Jsoup.connect(newUrl).get()
            else
                newUrl = "http://$url"
                Jsoup.connect(newUrl).get()
            newUrl
        } catch (ex: Exception) {
            if (!isThrown) {
                newUrl = "https://$url"
                return getValidUrl(newUrl, true)
            } else {
                throw MalformedURLException()
            }
            newUrl
        }
    }
}

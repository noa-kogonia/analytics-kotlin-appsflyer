package com.appsflyer.segment.app

import AnalyticsManager
import android.app.Application
import android.util.Log
import com.appsflyer.AFLogger
import com.appsflyer.AppsFlyerLib
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

//https://segment.com/docs/spec/identify/
//https://segment.com/docs/sources/mobile/android/
class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d(MainActivity.TAG, "Application OnCreate before initiating the analytics")
        AppsFlyerLib.getInstance().setLogLevel(AFLogger.LogLevel.VERBOSE)
        // Initialize Analytics
        AnalyticsManager.initialize(applicationContext)
        Log.d(MainActivity.TAG, "Application OnCreate after initiating the analytics")

        // Initialize AppsFlyerDestination and add it immediately
        AppsFlyerDestinationManager.initialize(applicationContext)
        AnalyticsManager.analytics.add(plugin = AppsFlyerDestinationManager.appsFlyerDestination)
        Log.d(MainActivity.TAG, "AppsFlyerDestination added to Analytics")

        identifyUser()
    }


    private fun identifyUser() {
        AnalyticsManager.analytics.identify(
            userId = "12345",
            traits = buildJsonObject {
                put("name", "John Doe") // Replace with actual user name
                put("email", "john.doe@example.com") // Replace with actual email
                put("currencyCode", "GBP") // Replace with actual currency
            }
        )
        Log.d(MainActivity.TAG, "User identified in Analytics")
    }

}

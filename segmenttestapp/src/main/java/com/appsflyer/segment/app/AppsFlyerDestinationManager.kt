package com.appsflyer.segment.app

import android.content.Context
import com.segment.analytics.kotlin.destinations.appsflyer.AppsFlyerDestination

object AppsFlyerDestinationManager {
    lateinit var appsFlyerDestination: AppsFlyerDestination

    fun initialize(context: Context) {
        appsFlyerDestination = AppsFlyerDestination(context, true)
    }
}
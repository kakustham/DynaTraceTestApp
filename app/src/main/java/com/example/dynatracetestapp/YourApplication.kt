package com.example.dynatracetestapp

import android.app.Application
import com.dynatrace.android.agent.Dynatrace
import com.dynatrace.android.agent.conf.DataCollectionLevel
import com.dynatrace.android.agent.conf.DynatraceConfigurationBuilder

class YourApplication : Application() {

   lateinit var  dynaTrace : Dynatrace;
    override fun onCreate() {
        super.onCreate()


         Dynatrace.startup(this,  DynatraceConfigurationBuilder(
             "26c649ca-556e-4afc-a6e1-cc2710908e77",
             "https://bf83242hqv.bf.dynatrace.com/mbeacon")
            .withUserOptIn(true)
            .withCrashReporting(true)
            .buildConfiguration());

                Dynatrace.setCrashReportingOptedIn(true)
               Dynatrace.setDataCollectionLevel(DataCollectionLevel.USER_BEHAVIOR)
    }
}
package com.pi.attestation

import android.app.Application
import android.content.Context
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraHttpSender
import org.acra.config.CoreConfigurationBuilder
import org.acra.data.StringFormat
import org.acra.sender.HttpSender


@AcraHttpSender(uri = "https://collector.tracepot.com/64efabe3",
    httpMethod = HttpSender.Method.POST)
@AcraCore(buildConfigClass = BuildConfig::class)
class MyApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        val builder = CoreConfigurationBuilder(this)
        builder.setBuildConfigClass(BuildConfig::class.java)
            .setReportFormat(StringFormat.JSON)
        ACRA.init(this, builder)
    }
}
package com.demo.giraffeproxy.util

import android.os.Bundle
import com.demo.giraffeproxy.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirePointUtil {
    private var fire:FirebaseAnalytics?=null

    init {
        if (!BuildConfig.DEBUG){
            fire=Firebase.analytics
        }
    }

    fun setPoint(key:String,bundle: Bundle = Bundle()){
        var time=0L
        runCatching {
            if(key=="giraffpe_snim"||key=="giraffpe_lpop"){
                time=bundle.getLong("time")
                printGiraffe("===point===${key}==time:${time}")
            }else{
                printGiraffe("===point===${key}")
            }
        }
        fire?.logEvent(key,bundle)
        TbaInfo0529.uploadPointEvent(key,time)
    }
}
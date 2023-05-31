package com.demo.giraffeproxy.util

import android.app.Activity
import android.app.Application
import android.os.Bundle


object AppRegister {
    var appFront=true
    private var cancelConnectCallback:CancelConnectCallback?=null

    fun setCancelConnectCallback(cancelConnectCallback:CancelConnectCallback?){
        this.cancelConnectCallback=cancelConnectCallback
    }

    fun register(application: Application){
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{
            private var pages=0
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                pages++
                if (pages==1){
                    appFront=true
                }
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                pages--
                if (pages<=0){
                    appFront=false
                    cancelConnectCallback?.cancelConnect()
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
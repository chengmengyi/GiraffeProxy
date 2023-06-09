package com.demo.giraffeproxy.util

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.demo.giraffeproxy.ac0529.LaunchAc0529
import com.demo.giraffeproxy.ac0529.VpnAc0529
import com.demo.giraffeproxy.conf0529.Fire0529
import com.google.android.gms.ads.AdActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


object AppRegister {
    var appFront=true
    private var job: Job?=null
    private var startLaunchAc=false
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
                job?.cancel()
                job=null
                if (pages==1){
                    appFront=true
                    if (startLaunchAc){
                        Fire0529.isHotStart=true
                        if(ActivityUtils.isActivityExistsInStack(VpnAc0529::class.java)){
                            activity.startActivity(Intent(activity, LaunchAc0529::class.java))
                        }
                    }
                    startLaunchAc=false
                }
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                pages--
                if (pages<=0){
                    appFront=false
                    cancelConnectCallback?.cancelConnect()
                    job= GlobalScope.launch {
                        delay(2500L)
                        ActivityUtils.finishActivity(LaunchAc0529::class.java)
                        ActivityUtils.finishActivity(AdActivity::class.java)
                        delay(500)
                        startLaunchAc=true
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
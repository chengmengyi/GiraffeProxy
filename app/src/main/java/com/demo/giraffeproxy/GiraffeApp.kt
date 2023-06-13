package com.demo.giraffeproxy

import android.app.ActivityManager
import android.app.Application
import com.demo.giraffeproxy.ac0529.VpnAc0529
import com.demo.giraffeproxy.conf0529.Fire0529
import com.demo.giraffeproxy.util.AdLimit0529Util
import com.demo.giraffeproxy.util.AppRegister
import com.demo.giraffeproxy.util.Referrer0529Util
import com.demo.giraffeproxy.vpn0529.VpnInfoUtil0529
import com.github.shadowsocks.Core
import com.google.android.gms.ads.MobileAds
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.tencent.mmkv.MMKV

lateinit var giraffeApp: GiraffeApp
class GiraffeApp:Application() {

    override fun onCreate() {
        super.onCreate()
        giraffeApp=this
        Core.init(this,VpnAc0529::class)
        if (!packageName.equals(processName())){
            return
        }
        MMKV.initialize(this)
        MobileAds.initialize(this)
        Firebase.initialize(this)
        init()
    }

    private fun init(){
        AppRegister.register(this)
        Fire0529.readFireConf()
        Referrer0529Util.readReferrerClient()
        AdLimit0529Util.checkLimitUser()
    }

    private fun processName(): String {
        val pid = android.os.Process.myPid()
        var processName = ""
        val manager = getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid === pid) {
                processName = process.processName
            }
        }
        return processName
    }
}
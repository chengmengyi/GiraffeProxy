package com.demo.giraffeproxy.vpn0529

import com.demo.giraffeproxy.util.ConnectTimeCallback
import com.demo.giraffeproxy.util.RequestUtil0529
import kotlinx.coroutines.*

object VpnConnectTimeUtil0529 {
    var time=0L
    private var job:Job?=null

    private var connectTimeCallback:ConnectTimeCallback?=null

    fun setConnectTimeCallback(connectTimeCallback:ConnectTimeCallback?){
        this.connectTimeCallback=connectTimeCallback
    }

    fun startTime(){
        if (null!= job) return
        job = GlobalScope.launch(Dispatchers.Main) {
            while (null!=job) {
                connectTimeCallback?.connectTime(time)
                time++
                if(time%60==0L){
                    RequestUtil0529.vpnHeartUpload(true)
                }
                delay(1000L)
            }
        }
    }

    fun endTime(){
        job?.cancel()
        job=null
    }
}
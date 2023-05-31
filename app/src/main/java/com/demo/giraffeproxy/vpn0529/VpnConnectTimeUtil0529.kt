package com.demo.giraffeproxy.vpn0529

import com.demo.giraffeproxy.util.ConnectTimeCallback
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
                delay(1000L)
            }
        }
    }

    fun endTime(){
        job?.cancel()
        job=null
    }
}
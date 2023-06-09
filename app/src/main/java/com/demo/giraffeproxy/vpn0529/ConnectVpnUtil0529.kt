package com.demo.giraffeproxy.vpn0529

import android.os.Bundle
import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.admob0529.LoadAd0529Util
import com.demo.giraffeproxy.bean.Vpn0529Bean
import com.demo.giraffeproxy.conf0529.Fire0529
import com.demo.giraffeproxy.util.FirePointUtil
import com.demo.giraffeproxy.util.RequestUtil0529
import com.demo.giraffeproxy.util.VpnStateCallback
import com.demo.giraffeproxy.util.printGiraffe
import com.demo.giraffeproxy.vpn0529.VpnInfoUtil0529.getVpnId
import com.demo.giraffeproxy.vpn0529.VpnInfoUtil0529.isFastVpn
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
import com.github.shadowsocks.preference.DataStore.profileId
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ConnectVpnUtil0529: ShadowsocksConnection.Callback {
    private var baseAc0529:BaseAc0529?=null
    private var state= BaseService.State.Stopped
    private val sc= ShadowsocksConnection(true)
    private var vpnStateCallback:VpnStateCallback?=null

    var connectedVpnBean=Vpn0529Bean()
    var lastVpnBean=Vpn0529Bean()
    var fastVpnBean=Vpn0529Bean()

    private var autoConnectVpn=false

    fun onCreate(baseAc0529: BaseAc0529,vpnStateCallback: VpnStateCallback){
        this.baseAc0529=baseAc0529
        this.vpnStateCallback=vpnStateCallback
        sc.connect(baseAc0529,this)
    }

    fun getHeartUploadIp():String{
        if(connectedVpn()){
            return if(connectedVpnBean.isFastVpn()){
                fastVpnBean.giraffe_ip
            }else{
                connectedVpnBean.giraffe_ip
            }
        }
        return ""
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        this.state=state
        if (stoppedVpn()){
            vpnStateCallback?.vpnDisconnected()
            VpnConnectTimeUtil0529.endTime()

            val bundle = Bundle()
            bundle.putLong("time",VpnConnectTimeUtil0529.time)
            FirePointUtil.setPoint("giraffpe_snim", bundle = bundle)

            RequestUtil0529.vpnHeartUpload(false)
        }
        if (connectedVpn()){
            lastVpnBean= connectedVpnBean
            FirePointUtil.setPoint("giraffpe_hpop")
            VpnConnectTimeUtil0529.startTime()
            if (autoConnectVpn&&Fire0529.planTwo){
                LoadAd0529Util.planTwoReloadAllAd()
            }
            RequestUtil0529.vpnHeartUpload(true)
        }
    }

    override fun onServiceConnected(service: IShadowsocksService) {
        val state = BaseService.State.values()[service.state]
        this.state=state
        if (connectedVpn()){
            lastVpnBean= connectedVpnBean
            vpnStateCallback?.vpnConnected()
        }
    }

    fun connectVpn(autoConnectVpn:Boolean){
        this.autoConnectVpn=autoConnectVpn
        GlobalScope.launch {
            state= BaseService.State.Connecting
            if(connectedVpnBean.isFastVpn()){
                val smartVpn = VpnInfoUtil0529.getSmartVpn()
                if(null!=smartVpn){
                    fastVpnBean=smartVpn
                    val profileId= fastVpnBean.getVpnId()
                    DataStore.profileId = profileId
                    Core.startService()
                }
            }else{
                val profileId=connectedVpnBean.getVpnId()
                DataStore.profileId = profileId
                Core.startService()
            }
        }
    }

    fun disconnectVpn(){
        GlobalScope.launch {
            state= BaseService.State.Stopping
            Core.stopService()
        }
    }

    fun connectedVpn()= state==BaseService.State.Connected

    fun stoppedVpn()= state==BaseService.State.Stopped

    fun cancelConnect(state:BaseService.State){
        ConnectVpnUtil0529.state =state
        if(state==BaseService.State.Connected){
            connectedVpnBean= lastVpnBean
        }
    }

    override fun onBinderDied() {
        if (null!= baseAc0529){
            sc.disconnect(baseAc0529!!)
        }
    }

    fun onDestroy(){
        onBinderDied()
        baseAc0529=null
        vpnStateCallback=null
    }
}
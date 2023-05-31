package com.demo.giraffeproxy.vpn0529

import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.bean.Vpn0529Bean
import com.demo.giraffeproxy.util.VpnStateCallback
import com.demo.giraffeproxy.util.printGiraffe
import com.demo.giraffeproxy.vpn0529.VpnInfoUtil0529.getVpnId
import com.demo.giraffeproxy.vpn0529.VpnInfoUtil0529.isFastVpn
import com.github.shadowsocks.Core
import com.github.shadowsocks.aidl.IShadowsocksService
import com.github.shadowsocks.aidl.ShadowsocksConnection
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.preference.DataStore
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

    fun onCreate(baseAc0529: BaseAc0529,vpnStateCallback: VpnStateCallback){
        this.baseAc0529=baseAc0529
        this.vpnStateCallback=vpnStateCallback
        sc.connect(baseAc0529,this)
    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        this.state=state
        if (stoppedVpn()){
            vpnStateCallback?.vpnDisconnected()
            VpnConnectTimeUtil0529.endTime()
        }
        if (connectedVpn()){
            lastVpnBean= connectedVpnBean
            VpnConnectTimeUtil0529.startTime()
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

    fun connectVpn(){
        GlobalScope.launch {
            state= BaseService.State.Connecting
            var profileId=0L
            if(connectedVpnBean.isFastVpn()){
                fastVpnBean=VpnInfoUtil0529.getSmartVpn()
                profileId= fastVpnBean.getVpnId()
            }else{
                profileId=connectedVpnBean.getVpnId()
            }
            DataStore.profileId = profileId
            Core.startService()
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
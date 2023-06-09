package com.demo.giraffeproxy.ac0529

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.VpnService
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.ActivityUtils
import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.R
import com.demo.giraffeproxy.admob0529.LoadAd0529Util
import com.demo.giraffeproxy.admob0529.ShowNative0529Ad
import com.demo.giraffeproxy.conf0529.Fire0529
import com.demo.giraffeproxy.util.*
import com.demo.giraffeproxy.vpn0529.ConnectVpnUtil0529
import com.demo.giraffeproxy.vpn0529.ConnectVpnUtil0529.connectedVpnBean
import com.demo.giraffeproxy.vpn0529.VpnConnectTimeUtil0529
import com.demo.giraffeproxy.vpn0529.VpnInfoUtil0529
import com.demo.giraffeproxy.vpn0529.VpnInfoUtil0529.isFastVpn
import com.github.shadowsocks.bg.BaseService
import com.github.shadowsocks.utils.StartService
import kotlinx.android.synthetic.main.activity_vpn.*
import kotlinx.android.synthetic.main.vpn_content.*
import kotlinx.android.synthetic.main.vpn_drawer.*
import kotlinx.coroutines.*

class VpnAc0529:BaseAc0529(R.layout.activity_vpn), VpnStateCallback, CancelConnectCallback {
    private var connectJobTime=-1
    private var canClick=true
    private var permission=false
    private var currentIsConnect=false
    private var autoConnectVpn=false
    private var connectJob:Job?=null

    private val showHomeAd = ShowNative0529Ad(LoadAd0529Util.HOME,this)

    override fun init0529View() {
        immersionBar.statusBarView(view_top).init()
        ConnectVpnUtil0529.onCreate(this,this)
        AppRegister.setCancelConnectCallback(this)
        setOnClick()
        if(ConnectVpnUtil0529.connectedVpn()){
            hideGuideView()
        }
        checkAutoConnect(intent)

        if (guide_lottie_view.visibility==View.VISIBLE){
            FirePointUtil.setPoint("giraffpe_sert")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { checkAutoConnect(it) }
    }

    private fun checkAutoConnect(intent: Intent){
        if(intent.getBooleanExtra("autoConnectVpn",false)){
            autoConnectVpn=true
            clickConnectBtn()
        }
    }


    private fun setOnClick(){
        iv_connect_btn.setOnClickListener {
            if (canClick&&!drawer_layout.isOpen){
                clickConnectBtn()
            }
        }
        llc_vpn_list.setOnClickListener {
            if (canClick&&!drawer_layout.isOpen){
                VpnInfoUtil0529.checkCanJumpVpnListAc(supportFragmentManager){
                    startActivityForResult(Intent(this,VpnListAc0529::class.java),529)
                }
            }
            if (!currentIsConnect&&ConnectVpnUtil0529.connectedVpn()){
                cancelConnect()
            }
        }
        iv_set.setOnClickListener {
            if (canClick&&!drawer_layout.isOpen){
                drawer_layout.openDrawer(Gravity.LEFT)
            }
            if (!currentIsConnect&&ConnectVpnUtil0529.connectedVpn()){
                cancelConnect()
            }
        }
        llc_privacy.setOnClickListener { startActivity(Intent(this,PrivacyAc0529::class.java)) }

        llc_share.setOnClickListener {
            val pm = packageManager
            val packageName=pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details?id=${packageName}"
            )
            startActivity(Intent.createChooser(intent, "share"))
        }

        llc_update.setOnClickListener {
            val packName = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).packageName
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=$packName")
            }
            startActivity(intent)
        }
        view_guide.setOnClickListener {  }
        guide_lottie_view.setOnClickListener {
            FirePointUtil.setPoint("giraffpe_opie")
            clickConnectBtn()
        }
    }

    private fun clickConnectBtn(){
        if(AdLimit0529Util.limitUser){
            showLimitUserDialog()
            return
        }
        LoadAd0529Util.preLoad(LoadAd0529Util.CONNECT)
        LoadAd0529Util.preLoad(LoadAd0529Util.RESULT)
        hideGuideView()

        if(!autoConnectVpn){
            FirePointUtil.setPoint("giraffpe_qlvx")
        }

        if (ConnectVpnUtil0529.connectedVpn()){
            currentIsConnect=false
            LoadAd0529Util.disconnectReloadAllAd()
            updateConnectUI(BaseService.State.Stopping)
            startConnectJob()
        }else{
            updateVpnInfo()
            if (!checkHasNetWork()){
                canClick=true
                return
            }
            if (VpnService.prepare(this) != null) {
                permission = true
                registerResult.launch(null)
                return
            }

            checkHasFastVpn()
        }
    }

    private fun checkHasFastVpn(){
        VpnInfoUtil0529.checkHasFastVpn(supportFragmentManager){
            if (it){
                connectVpn()
            }else{
                canClick=true
            }
        }
    }

    private fun connectVpn(){
        FirePointUtil.setPoint("giraffpe_bobv")
        currentIsConnect=true
        updateConnectUI(BaseService.State.Connecting)
        VpnConnectTimeUtil0529.time=0L
        startConnectJob()
    }

    private fun startConnectJob(){
        connectJob=GlobalScope.launch {
            connectJobTime=0
            while (true) {
                if (!isActive) {
                    break
                }
                delay(100)
                connectJobTime++
                if (connectJobTime==21){
                    if(currentIsConnect){
                        ConnectVpnUtil0529.connectVpn(autoConnectVpn)
                        autoConnectVpn=false
                    }else{
                        ConnectVpnUtil0529.disconnectVpn()
                    }
                }

                if(connectJobTime in 22..99){
                    if(checkConnectSuccess()){
                        runOnUiThread {
                            LoadAd0529Util.showFullAd(
                                LoadAd0529Util.CONNECT,
                                this@VpnAc0529,
                                showingAd = {
                                    cancel()
                                    checkConnectResult(canToResult = false)
                                },
                                closeAd = {
                                    cancel()
                                    checkConnectResult()
                                }
                            )
                        }
                    }
                }else if (connectJobTime>=100){
                    cancel()
                    checkConnectResult()
                }
            }
        }
    }

    private fun checkConnectResult(canToResult:Boolean=true){
        runOnUiThread {
            if (checkConnectSuccess()){
                if (currentIsConnect){
                    updateConnectUI(BaseService.State.Connected)
                }else{
                    updateConnectUI(BaseService.State.Stopped)
                    updateVpnInfo()
                }
                toResultAc(canToResult)
            }else{
                updateConnectUI(if (currentIsConnect)BaseService.State.Stopped else BaseService.State.Connected)
                showToast(if (currentIsConnect) "Connect Fail" else "Disconnect Fail")
                if(currentIsConnect){
                    FirePointUtil.setPoint("giraffpe_qwds")
                }
            }
            canClick=true
        }
    }

    private fun toResultAc(canToResult: Boolean) {
        if (canToResult&& AppRegister.appFront&& ActivityUtils.getTopActivity().javaClass.name==VpnAc0529::class.java.name){
            startActivity(Intent(this,ResultAc0529::class.java).apply {
                putExtra("connect",currentIsConnect)
            })
        }
    }

    private fun checkConnectSuccess()=if (currentIsConnect) ConnectVpnUtil0529.connectedVpn() else ConnectVpnUtil0529.stoppedVpn()

    private fun updateConnectUI(state:BaseService.State){
        when(state){
            BaseService.State.Connecting->{
                iv_connected.show(false)
                connect_lottie_view.show(true)
                connect_lottie_view.playAnimation()
                tv_connect.text="Connecting…"
                iv_connect_btn.setImageResource(R.drawable.home3)
            }
            BaseService.State.Connected->{
                iv_connected.show(true)
                connect_lottie_view.show(false)
                connect_lottie_view.cancelAnimation()
                tv_connect.text="Connected"
                iv_connect_btn.setImageResource(R.drawable.home4)
            }
            BaseService.State.Stopped->{
                iv_connected.show(false)
                connect_lottie_view.show(true)
                connect_lottie_view.cancelAnimation()
                tv_connect.text="Connect"
                iv_connect_btn.setImageResource(R.drawable.home3)
            }
            BaseService.State.Stopping->{
                iv_connected.show(false)
                connect_lottie_view.show(true)
                connect_lottie_view.playAnimation()
                tv_connect.text="Stopping…"
                iv_connect_btn.setImageResource(R.drawable.home3)
            }
            else->{

            }
        }
    }

    private fun updateVpnInfo(){
        tv_name.text=if (connectedVpnBean.isFastVpn()){
            "Super Fast Server"
        }else{
            "${connectedVpnBean.giraffe_country} - ${connectedVpnBean.giraffe_city}"
        }
        iv_logo.setImageResource(getVpnLogo(connectedVpnBean.giraffe_country))
    }

    private val registerResult=registerForActivityResult(StartService()) {
        if (!it && permission) {
            permission = false
            FirePointUtil.setPoint("giraffpe_wer")
            checkHasFastVpn()
        } else {
            canClick=true
        }
    }

    override fun vpnConnected() {
        updateConnectUI(BaseService.State.Connected)
    }

    override fun vpnDisconnected() {
        if (canClick){
            updateConnectUI(BaseService.State.Stopped)
        }
    }

    override fun cancelConnect() {
        if(connectJobTime in 0..20){
            connectJobTime=-1
            canClick=true
            endConnectJob()
            if(currentIsConnect){
                ConnectVpnUtil0529.cancelConnect(BaseService.State.Stopped)
                updateConnectUI(BaseService.State.Stopped)
            }else{
                ConnectVpnUtil0529.cancelConnect(BaseService.State.Connected)
                updateConnectUI(BaseService.State.Connected)
            }
        }
    }

    private fun endConnectJob(){
        connectJob?.cancel()
        connectJob=null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==529){
            when(data?.getStringExtra("back")){
                "giraffe_dis"->{
                    clickConnectBtn()
                }
                "giraffe_con"->{
                    updateVpnInfo()
                    clickConnectBtn()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (guide_lottie_view.visibility==View.VISIBLE){
            FirePointUtil.setPoint("giraffpe_hla")
            hideGuideView()
            return
        }
        if(canClick){
            finish()
        }
        if (!currentIsConnect&&ConnectVpnUtil0529.connectedVpn()){
            cancelConnect()
        }
    }

    private fun hideGuideView(){
        view_guide.show(false)
        guide_lottie_view.show(false)
    }

    override fun onResume() {
        super.onResume()
        showHomeAd.loop()
    }


    override fun onDestroy() {
        super.onDestroy()
        AppRegister.setCancelConnectCallback(null)
        ConnectVpnUtil0529.onDestroy()
        endConnectJob()
        showHomeAd.endLoop()
        Fire0529.isHotStart=true
        AdLimit0529Util.setRefreshBool(LoadAd0529Util.HOME,true)
    }
}
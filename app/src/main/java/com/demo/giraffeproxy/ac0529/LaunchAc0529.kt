package com.demo.giraffeproxy.ac0529

import android.animation.ValueAnimator
import android.content.Intent
import androidx.core.animation.doOnEnd
import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.R
import com.demo.giraffeproxy.admob0529.LoadAd0529Util
import com.demo.giraffeproxy.conf0529.Fire0529
import com.demo.giraffeproxy.util.*
import com.demo.giraffeproxy.vpn0529.ConnectVpnUtil0529
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class LaunchAc0529 : BaseAc0529(R.layout.activity_main) {
    private val totalTime=100
    private var animatorJob:Job?=null
    private var valueAnimator:ValueAnimator?=null

    override fun init0529View() {
        RequestUtil0529.requestCloak()
        AdLimit0529Util.readCurrentNum()
        RequestUtil0529.getVpnList()
        startAnimator()
    }

    private fun startAnimator(){
        animatorJob=GlobalScope.launch {
            var time=0
            var startLoadAd=false
            while (true){
                delay(100)
                time++
                if (Fire0529.hasFireData&&time<=40&&!startLoadAd){
                    startLoadAd=true
                    runOnUiThread { LoadAd0529Util.preLoadAllAd() }
                }
                if(time<=totalTime){
                    runOnUiThread {
                        progress_view.progress = time*100/totalTime
                        if (time>40&&!startLoadAd){
                            startLoadAd=true
                            LoadAd0529Util.preLoadAllAd()
                        }
                        if(time>=20){
                            LoadAd0529Util.showFullAd(
                                LoadAd0529Util.OPEN,
                                this@LaunchAc0529,
                                showingAd = {
                                    cancel()
                                    progress_view.progress=100
                                },
                                closeAd = {
                                    cancel()
                                    checkPlanType()
                                })
                        }
                    }
                }else if(time>totalTime){
                    cancel()
                    checkPlanType()
                }
            }
        }
    }


    private fun checkPlanType(){
        if(Referrer0529Util.getReferrerBuyUser()||Referrer0529Util.getReferrerFBUser()){
            Fire0529.createPlanType()
            if(Fire0529.planTwo){
                FirePointUtil.setPoint("giraffpe_textb")
            }
            showVpnAc(autoConnectVpn = Fire0529.planTwo&& !ConnectVpnUtil0529.connectedVpn())
        }else{
            showVpnAc()
        }
    }

    private fun showVpnAc(autoConnectVpn:Boolean=false){
        startActivity(Intent(this,VpnAc0529::class.java).apply {
            putExtra("autoConnectVpn",autoConnectVpn)
        })
        finish()
    }

    private fun endAnimator(){
        valueAnimator?.removeAllUpdateListeners()
        valueAnimator?.cancel()
        valueAnimator=null
    }

    override fun onResume() {
        super.onResume()
        if (valueAnimator?.isPaused==true){
            valueAnimator?.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        valueAnimator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        endAnimator()
    }

    override fun onBackPressed() {

    }
}
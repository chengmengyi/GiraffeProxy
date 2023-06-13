package com.demo.giraffeproxy.ac0529

import android.animation.ValueAnimator
import android.content.Intent
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.BuildConfig
import com.demo.giraffeproxy.R
import com.demo.giraffeproxy.admob0529.LoadAd0529Util
import com.demo.giraffeproxy.conf0529.Fire0529
import com.demo.giraffeproxy.util.*
import com.demo.giraffeproxy.vpn0529.ConnectVpnUtil0529
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class LaunchAc0529 : BaseAc0529(R.layout.activity_main) {
    private var progressAnimator: ValueAnimator?=null
    private var fireJob:Job?=null

    override fun init0529View() {
        RequestUtil0529.requestCloak()
        AdLimit0529Util.readCurrentNum()
        RequestUtil0529.getVpnList()
        startAnimator()
    }

    private fun startAnimator(){
//        animatorJob=GlobalScope.launch {
//            var time=0
//            var startLoadAd=false
//            while (loop){
//                delay(100)
//                time++
//                if (Fire0529.hasFireData&&time<=40&&!startLoadAd){
//                    startLoadAd=true
//                    runOnUiThread { LoadAd0529Util.preLoadAllAd() }
//                }
//                if(time<=totalTime){
//                    runOnUiThread {
//                        progress_view.progress = time*100/totalTime
//                        if (time>40&&!startLoadAd){
//                            startLoadAd=true
//                            LoadAd0529Util.preLoadAllAd()
//                        }
//                        if(time>=20){
//                            LoadAd0529Util.showFullAd(
//                                LoadAd0529Util.OPEN,
//                                this@LaunchAc0529,
//                                showingAd = {
//                                    cancel()
//                                    progress_view.progress=100
//                                },
//                                closeAd = {
//                                    cancel()
//                                    checkPlanType()
//                                })
//                        }
//                    }
//                }else if(time>totalTime){
//                    cancel()
//                    checkPlanType()
//                }
//            }
//        }

        if(BuildConfig.DEBUG){
            LoadAd0529Util.preLoadAllAd()
        }else{
            if (!Fire0529.isHotStart&&!Fire0529.hasFireData){
                var time=0
                fireJob=GlobalScope.launch {
                    while (true){
                        if (!isActive) {
                            break
                        }
                        time++
                        if(Fire0529.hasFireData||time>=8){
                            cancel()
                            runOnUiThread { LoadAd0529Util.preLoadAllAd() }
                        }
                        delay(500L)
                    }
                }
            }else{
                LoadAd0529Util.preLoadAllAd()
            }
        }


        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 10000L
            interpolator = LinearInterpolator()
            addUpdateListener {
                val progress = it.animatedValue as Int
                progress_view.progress = progress
                val pro = (10 * (progress / 100.0F)).toInt()
                if(pro in 2..9){
                    LoadAd0529Util.showFullAd(
                        LoadAd0529Util.OPEN,
                        this@LaunchAc0529,
                        showingAd = {
                            endAnimator()
                            progress_view.progress=100
                        },
                        closeAd = {
                            endAnimator()
                            checkPlanType()
                        })
                }else if (pro>=10){
                    checkPlanType()
                }
            }
            start()
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
        progressAnimator?.removeAllUpdateListeners()
        progressAnimator?.cancel()
        progressAnimator=null
        fireJob?.cancel()
        fireJob=null
    }

    override fun onResume() {
        super.onResume()
        if (progressAnimator?.isPaused==true){
            progressAnimator?.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        progressAnimator?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        endAnimator()
    }

    override fun onBackPressed() {

    }
}
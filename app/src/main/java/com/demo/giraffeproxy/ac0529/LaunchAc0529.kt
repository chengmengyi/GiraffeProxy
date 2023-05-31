package com.demo.giraffeproxy.ac0529

import android.animation.ValueAnimator
import android.content.Intent
import androidx.core.animation.doOnEnd
import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.R
import kotlinx.android.synthetic.main.activity_main.*

class LaunchAc0529 : BaseAc0529(R.layout.activity_main) {
    private var valueAnimator:ValueAnimator?=null

    override fun init0529View() {
        startAnimator()
    }

    private fun startAnimator(){
        valueAnimator = ValueAnimator.ofInt(0, 100)
        valueAnimator?.duration=3000L
        valueAnimator?.addUpdateListener {
            val progress = it.animatedValue as Int
            progress_view.progress = progress
        }
        valueAnimator?.doOnEnd { showVpnAc() }
        valueAnimator?.start()
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
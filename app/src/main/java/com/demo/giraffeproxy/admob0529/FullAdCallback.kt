package com.demo.giraffeproxy.admob0529

import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.util.AdLimit0529Util
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FullAdCallback(
    private val baseAc0529: BaseAc0529,
    private val type:String,
    private val closeAd:()->Unit
): FullScreenContentCallback() {
    override fun onAdDismissedFullScreenContent() {
        super.onAdDismissedFullScreenContent()
        AdLimit0529Util.fullAdShowing =false
        closeAd()
    }

    override fun onAdShowedFullScreenContent() {
        super.onAdShowedFullScreenContent()
        AdLimit0529Util.fullAdShowing =true
        AdLimit0529Util.addShowNum()
        LoadAd0529Util.removeAdByType(type)
    }

    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
        super.onAdFailedToShowFullScreenContent(p0)
        AdLimit0529Util.fullAdShowing =false
        LoadAd0529Util.removeAdByType(type)
        closeAd()
    }


    override fun onAdClicked() {
        super.onAdClicked()
        AdLimit0529Util.addClickNum()
    }

    private fun closeAd(){
        if (type!= LoadAd0529Util.OPEN&&type!= LoadAd0529Util.BACK){
            LoadAd0529Util.preLoad(type)
        }
        GlobalScope.launch(Dispatchers.Main) {
            delay(200L)
            if (baseAc0529.resume0529){
                closeAd.invoke()
            }
        }
    }
}
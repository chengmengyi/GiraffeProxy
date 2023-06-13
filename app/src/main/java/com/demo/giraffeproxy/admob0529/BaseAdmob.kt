package com.demo.giraffeproxy.admob0529

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.SizeUtils
import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.R
import com.demo.giraffeproxy.bean.AdResult0529Bean
import com.demo.giraffeproxy.bean.Admob0529Bean
import com.demo.giraffeproxy.conf0529.Fire0529
import com.demo.giraffeproxy.giraffeApp
import com.demo.giraffeproxy.util.*
import com.demo.giraffeproxy.vpn0529.ConnectVpnUtil0529
import com.demo.giraffeproxy.vpn0529.VpnInfoUtil0529.isFastVpn
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

abstract class BaseAdmob {
    val loadingAdList= arrayListOf<String>()
    val adResultMap= hashMapOf<String,AdResult0529Bean>()

    private val loadAdIpMap= hashMapOf<String,String>()
    private val loadAdCityMap= hashMapOf<String,String?>()

    fun loadAd(type: String, adBean: Admob0529Bean, result: (bean: AdResult0529Bean?) -> Unit) {
        when(adBean.giraffeff_type){
            "open"->loadOpenAd(type, adBean, result)
            "native"->loadNativeAd(type, adBean, result)
            "interstitial"->loadInterstitialAd(type, adBean, result)
        }
    }

    private fun loadOpenAd(
        type: String,
        adBean: Admob0529Bean,
        result: (bean: AdResult0529Bean?) -> Unit
    ) {
        printGiraffe("start load $type ad,${adBean.toString()}","qwerad")
        AppOpenAd.load(
            giraffeApp,
            adBean.giraffeff_id,
            AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(p0: AppOpenAd) {
                    printGiraffe("load ad success----$type","qwerad")
                    saveLoadIp(type)
                    p0.setOnPaidEventListener {
                        TbaInfo0529.uploadAdEvent(type, it, p0.responseInfo, adBean,loadAdIpMap[type]?:"",getCurrentIp(),loadAdCityMap[type]?:"null",get0529CityName())
                    }
                    result.invoke(AdResult0529Bean(time = System.currentTimeMillis(), ad = p0))
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    printGiraffe("load ad fail----$type---${p0.message}","qwerad")
                    result.invoke(null)
                }
            }
        )
    }

    private fun loadInterstitialAd(
        type: String,
        adBean: Admob0529Bean,
        result: (bean: AdResult0529Bean?) -> Unit
    ) {
        InterstitialAd.load(
            giraffeApp,
            adBean.giraffeff_id,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    printGiraffe("load ad fail----$type---${p0.message}")
                    result.invoke(null)
                }

                override fun onAdLoaded(p0: InterstitialAd) {
                    printGiraffe("load ad success----$type")
                    saveLoadIp(type)
                    p0.setOnPaidEventListener {
                        TbaInfo0529.uploadAdEvent(type, it, p0.responseInfo, adBean,loadAdIpMap[type]?:"",getCurrentIp(),loadAdCityMap[type]?:"null",get0529CityName())
                    }
                    result.invoke(AdResult0529Bean(time = System.currentTimeMillis(), ad = p0))
                }
            }
        )
    }
    private fun loadNativeAd(
        type: String,
        adBean: Admob0529Bean,
        result: (bean: AdResult0529Bean?) -> Unit
    ) {
        AdLoader.Builder(
            giraffeApp,
            adBean.giraffeff_id,
        ).forNativeAd {p0->
            printGiraffe("load ad success----$type")
            saveLoadIp(type)
            p0.setOnPaidEventListener {
                TbaInfo0529.uploadAdEvent(type, it, p0.responseInfo, adBean,loadAdIpMap[type]?:"",getCurrentIp(),loadAdCityMap[type]?:"null",get0529CityName())
            }
            result.invoke(AdResult0529Bean(time = System.currentTimeMillis(), ad = p0))
        }
            .withAdListener(object : AdListener(){
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    printGiraffe("load ad fail----$type---${p0.message}")
                    result.invoke(null)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    AdLimit0529Util.addClickNum()
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(
                        NativeAdOptions.ADCHOICES_TOP_RIGHT
                    )
                    .build()
            )
            .build()
            .loadAd(AdRequest.Builder().build())
    }

    fun showFullAd(type: String,baseAc0529: BaseAc0529,noAdBack:Boolean=false, showingAd:()->Unit, closeAd:()->Unit){
        if(adNumLimit(type)||AdLimit0529Util.fullAdShowing||!baseAc0529.resume0529||Fire0529.cannotShowInterAd(type)){
            closeAd.invoke()
            return
        }
        val adByType = getAdByType(type)
        if(null==adByType){
            if(noAdBack){
                closeAd.invoke()
            }
        }else{
            showingAd.invoke()
            when(adByType){
                is InterstitialAd ->{
                    adByType.fullScreenContentCallback= FullAdCallback(baseAc0529,type,closeAd)
                    adByType.show(baseAc0529)
                }
                is AppOpenAd ->{
                    adByType.fullScreenContentCallback= FullAdCallback(baseAc0529,type,closeAd)
                    adByType.show(baseAc0529)
                }
            }
        }
    }

    fun showNativeAd(baseAc0529: BaseAc0529,nativeAd: NativeAd,showed:()->Unit){
        val ad_native_view = baseAc0529.findViewById<NativeAdView>(R.id.ad_native)
        ad_native_view.iconView=baseAc0529.findViewById(R.id.ad_logo)
        (ad_native_view.iconView as ImageFilterView).setImageDrawable(nativeAd.icon?.drawable)

        ad_native_view.callToActionView=baseAc0529.findViewById(R.id.ad_install)
        (ad_native_view.callToActionView as AppCompatTextView).text=nativeAd.callToAction

        ad_native_view.headlineView=baseAc0529.findViewById(R.id.ad_title)
        (ad_native_view.headlineView as AppCompatTextView).text=nativeAd.headline

        ad_native_view.bodyView=baseAc0529.findViewById(R.id.ad_desc)
        (ad_native_view.bodyView as AppCompatTextView).text=nativeAd.body


        ad_native_view.mediaView=baseAc0529.findViewById(R.id.ad_medis)
        nativeAd.mediaContent?.let {
            ad_native_view.mediaView?.apply {
                setMediaContent(it)
                setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View?, outline: Outline?) {
                        if (view == null || outline == null) return
                        outline.setRoundRect(
                            0,
                            0,
                            view.width,
                            view.height,
                            SizeUtils.dp2px(12F).toFloat()
                        )
                        view.clipToOutline = true
                    }
                }
            }
        }

        ad_native_view.setNativeAd(nativeAd)
        baseAc0529.findViewById<AppCompatImageView>(R.id.ad_cover).show(false)
        showed.invoke()
    }

    fun removeAdByType(type:String){
        adResultMap.remove(type)
    }

    fun getAdByType(type: String)=adResultMap[type]?.ad

    fun adNumLimit(type: String):Boolean{
        if (AdLimit0529Util.adNumLimit()){
            printGiraffe("$type ad is num limit")
            return true
        }
        return false
    }

    private fun saveLoadIp(adType: String){
        loadAdIpMap[adType]=getCurrentIp()
        loadAdCityMap[adType]=get0529CityName()
    }

    private fun get0529CityName():String{
        return if(ConnectVpnUtil0529.connectedVpn()){
            if (ConnectVpnUtil0529.connectedVpnBean.isFastVpn()){
                ConnectVpnUtil0529.fastVpnBean.giraffe_city
            }else{
                ConnectVpnUtil0529.connectedVpnBean.giraffe_city
            }

        }else{
            "null"
        }
    }

    private fun getCurrentIp():String{
        return if(ConnectVpnUtil0529.connectedVpn()){
            if (ConnectVpnUtil0529.connectedVpnBean.isFastVpn()){
                ConnectVpnUtil0529.fastVpnBean.giraffe_ip
            }else{
                ConnectVpnUtil0529.connectedVpnBean.giraffe_ip
            }
        }else{
            RequestUtil0529.ip
        }
    }
}
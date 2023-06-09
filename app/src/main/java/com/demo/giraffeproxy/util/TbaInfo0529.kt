package com.demo.giraffeproxy.util

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import android.webkit.WebSettings
import com.android.installreferrer.api.ReferrerDetails
import com.demo.giraffeproxy.bean.Admob0529Bean
import com.demo.giraffeproxy.giraffeApp
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.ResponseInfo
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.security.MessageDigest
import java.util.*

object TbaInfo0529 {
    
    fun uploadInstallEvent(response: ReferrerDetails?){
        if ((null==response&& checkInstallNoReferrerPrompt())||(null!=response&& checkInstallHasReferrerPrompt())){
            return
        }
        RequestUtil0529.getIp {
            GlobalScope.launch {
                val json = getCommonJson().apply {
                    put("poisson", "build/${Build.VERSION.RELEASE}")
                    put("chafe", WebSettings.getDefaultUserAgent(giraffeApp))
                    put("colony", "toll")
                    put("marjoram", getFirstInstallTime(giraffeApp))
                    put("nazi", getLastUpdateTime(giraffeApp))
                    put("needy", "anise")
                    if (null == response) {
                        put("charcoal", "")
                        put("swine", "")
                        put("balky", 0)
                        put("sabina", 0)
                        put("gorham", 0)
                        put("collage", 0)
                    } else {
                        put("charcoal", response.installReferrer)
                        put("swine", response.installVersion)
                        put("balky", response.referrerClickTimestampSeconds)
                        put("sabina", response.installBeginTimestampSeconds)
                        put("gorham", response.referrerClickTimestampServerSeconds)
                        put("collage", response.installBeginTimestampServerSeconds)
                    }
                }
                MMKV.defaultMMKV().encode("install_giraffe",json.toString())
                RequestUtil0529.tbaEvent(json)
            }
        }
    }

    fun uploadSessionEvent(){
        RequestUtil0529.getIp {
            GlobalScope.launch {
                RequestUtil0529.tbaEvent(
                    getCommonJson().apply {
                        put("larval",JSONObject())
                    }
                )
            }
        }
    }

    fun uploadAdEvent(
        type: String,
        value: AdValue,
        responseInfo: ResponseInfo?,
        adBean: Admob0529Bean,
        loadIp:String,
        showIp:String,
        loadCity:String,
        showCity:String
        ){
        RequestUtil0529.getIp {
            GlobalScope.launch {
                RequestUtil0529.tbaEvent(
                    getCommonJson().apply {
                        put("ferris",JSONObject().apply {
                            put("ideology",value.valueMicros)
                            put("pull",value.currencyCode)
                            put("pfennig",getAdNetWork(responseInfo?.mediationAdapterClassName?:""))
                            put("farcical","admob")
                            put("portend",adBean.giraffeff_id)
                            put("banana",type)
                            put("moser","")
                            put("sodium", getAdFormat(adBean.giraffeff_type))
                            put("volvo",getPrecisionType(value.precisionType))
                            put("call",loadIp)
                            put("justice",showIp)
                        })
                        put("animate",JSONObject().apply {
                            put("gir_citygira",loadCity)
                            put("gir_cityzs",showCity)
                        })
                    }
                )
            }
        }
    }

    fun uploadPointEvent(key:String,time:Long=0){
        RequestUtil0529.getIp {
            GlobalScope.launch {
                RequestUtil0529.tbaEvent(
                    getCommonJson().apply {
                        put("needy",key)
                        if(time!=0L){
                            put("time|mafia",time)
                        }
                    }
                )
            }
        }
    }

    private fun getCommonJson()=JSONObject().apply {
        put("wig",JSONObject().apply {
            put("flank", Build.BRAND)
            put("supple", getScreenRes(giraffeApp))
            put("ludlow",RequestUtil0529.ip)
            put("stipple", stipple())
            put("juggle", giraffeApp.packageName)
            put("exterior", getOperator(giraffeApp))
            put("upheaval", UUID.randomUUID().toString())
            put("insular", Build.MANUFACTURER)
            put("platform", Build.VERSION.RELEASE)
            put("chine", getGaid(giraffeApp))
            put("meyer","arcturus")
            put("osiris", Locale.getDefault().country)
            put("rubbish", getSystemLanguage())
            put("plague", TimeZone.getDefault().rawOffset/3600/1000)
            put("carve",System.currentTimeMillis())
            put("lomb", getDistinctId(giraffeApp))
            put("endicott", Build.MODEL)
            put("err", getAndroidId(giraffeApp))
            put("cochran", getNetworkType(giraffeApp))
        })
    }

    fun stipple()=giraffeApp.packageManager.getPackageInfo(giraffeApp.packageName, PackageManager.GET_META_DATA).versionName

    fun getDistinctId(context: Context)= encrypt(getAndroidId(context))

    fun encrypt(raw: String): String {
        var md5Str = raw
        runCatching {
            val md = MessageDigest.getInstance("MD5")
            md.update(raw.toByteArray())
            val encryContext = md.digest()
            var i: Int
            val buf = StringBuffer("")
            for (offset in encryContext.indices) {
                i = encryContext[offset].toInt()
                if (i < 0) {
                    i += 256
                }
                if (i < 16) {
                    buf.append("0")
                }
                buf.append(Integer.toHexString(i))
            }
            md5Str = buf.toString()
        }
        return md5Str
    }

    fun getScreenRes(context: Context):String{
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.density.toString()
    }

    fun getNetworkType(context: Context):String{
        runCatching {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    return "wifi"
                } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    return "mobile"
                }
            } else {
                return "no"
            }
            return "no"
        }
        return "no"
    }

    fun getGaid(context: Context)=try {
        AdvertisingIdClient.getAdvertisingIdInfo(context).id
    }catch (e:Exception){
        ""
    }

    fun getAndroidId(context: Context): String {
        runCatching {
            val id: String = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
            )
            return if ("9774d56d682e549c" == id) "" else id ?: ""
        }
        return ""
    }

    fun getSystemLanguage():String{
        val default = Locale.getDefault()
        return "${default.language}_${default.country}"
    }

    fun getOperator(context: Context):String{
        runCatching {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return telephonyManager.networkOperator
        }
        return ""
    }

    fun getFirstInstallTime(context: Context):Long{
        runCatching {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.firstInstallTime
        }
        return System.currentTimeMillis()
    }

    fun getLastUpdateTime(context: Context):Long{
        runCatching {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            return packageInfo.lastUpdateTime
        }
        return System.currentTimeMillis()
    }

    fun installNoReferrerPrompt(){
        MMKV.defaultMMKV().encode("install_no_referrer_giraffe",1)
    }

    fun checkInstallNoReferrerPrompt()= MMKV.defaultMMKV().decodeInt("install_no_referrer_giraffe")==1

    fun installHasReferrerPrompt(){
        MMKV.defaultMMKV().encode("install_has_referrer_giraffe",1)
    }

    fun checkInstallHasReferrerPrompt()= MMKV.defaultMMKV().decodeInt("install_has_referrer_giraffe")==1

    private fun getAdNetWork(string: String):String{
        if(string.contains("facebook")) return "facebook"
        else if(string.contains("admob")) return "admob"
        return ""
    }

    private fun getAdFormat(adType: String):String{
        when(adType){
            "open"->return "open"
            "interstitial"->return "interstitial"
            "native"->return "native"
        }
        return ""
    }

    private fun getPrecisionType(precisionType:Int)=when(precisionType){
        1->"ESTIMATED"
        2->"PUBLISHER_PROVIDED"
        3->"PRECISE"
        else->"UNKNOWN"
    }
}
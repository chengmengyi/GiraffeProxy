package com.demo.giraffeproxy.util

import android.os.Build
import android.os.Bundle
import android.provider.Telephony.Carriers.SERVER
import android.util.Base64
import android.webkit.WebSettings
import android.webkit.WebView
import com.blankj.utilcode.util.DeviceUtils.getManufacturer
import com.demo.giraffeproxy.BuildConfig
import com.demo.giraffeproxy.giraffeApp
import com.demo.giraffeproxy.util.TbaInfo0529.getAndroidId
import com.demo.giraffeproxy.util.TbaInfo0529.installHasReferrerPrompt
import com.demo.giraffeproxy.util.TbaInfo0529.installNoReferrerPrompt
import com.demo.giraffeproxy.vpn0529.ConnectVpnUtil0529
import com.demo.giraffeproxy.vpn0529.VpnInfoUtil0529
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

object RequestUtil0529 {
    var ip=""
    var countryCode=""
    var isBlackCloak=true
    var loadingVpn=false

    private val tbaUrl=if (BuildConfig.DEBUG) "https://test-top.speedgiraffe.com/spindly/aviary" else "https://top.speedgiraffe.com/familiar/afoot/dennis/affable"
    private val vpnUrl=if (BuildConfig.DEBUG) "https://test.giraffeproxy.com" else "https://api.giraffeproxy.com"

    fun getIp(back:()->Unit){
        if(ip.isNotEmpty()){
            back.invoke()
            return
        }

        OkGo.get<String>("https://ipapi.co/json")
            .headers("User-Agent", WebSettings.getDefaultUserAgent(giraffeApp))
            .execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    runCatching {
                        val jsonObject = JSONObject(response?.body()?.toString())
                        countryCode=jsonObject.optString("country_code")
                        ip=jsonObject.optString("ip")
                    }
                    back.invoke()
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    back.invoke()
                }
            })
    }

    fun tbaEvent(jsonObject: JSONObject){
        val url="$tbaUrl?flank=${Build.BRAND}&err=${TbaInfo0529.getAndroidId(giraffeApp)}&carve=${System.currentTimeMillis()}"
        printGiraffe(url, tag = "qwertba")
        printGiraffe(jsonObject.toString(), tag = "qwertba")
        OkGo.post<String>(url)
            .retryCount(3)
            .headers("content-type","application/json")
            .upJson(jsonObject)
            .execute(object :StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    printGiraffe("=tba=onSuccess=${response?.body()?.toString()}", tag = "qwertba")
                    runCatching {
                        if(jsonObject.optString("needy")=="anise"){
                            MMKV.defaultMMKV().encode("install_giraffe","")
                            if (jsonObject.optString("charcoal").isEmpty()){
                                installNoReferrerPrompt()
                            }else{
                                installHasReferrerPrompt()
                            }
                        }
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    printGiraffe("=tba=onError=${response?.body()?.toString()}", tag = "qwertba")
                }
            })
    }

    fun requestCloak(){
        val s = MMKV.defaultMMKV().decodeString("giraffe_cloak") ?: ""
        if(s.isEmpty()){
            GlobalScope.launch {
                val gaid = TbaInfo0529.getGaid(giraffeApp)
                val url="https://glide.speedgiraffe.com/pizza/trunk/southey?" +
                        "lomb=${TbaInfo0529.getDistinctId(giraffeApp)}&" +
                        "carve=${System.currentTimeMillis()}&endicott=${Build.MODEL}&" +
                        "juggle=${giraffeApp.packageName}&" +
                        "platform=${Build.VERSION.RELEASE}&" +
                        "chine=$gaid&err=${getAndroidId(giraffeApp)}&" +
                        "meyer=arcturus&stipple=${TbaInfo0529.stipple()}&ludlow=${ip}&cochran=${TbaInfo0529.getNetworkType(giraffeApp)}"
                OkGo.get<String>(url)
                    .execute(object : StringCallback(){
                        override fun onSuccess(response: Response<String>?) {
                            //devoid
                            val result = response?.body()?.toString()
                            isBlackCloak=result=="devoid"
                            MMKV.defaultMMKV().encode("giraffe_cloak",result)
                            printGiraffe("==onSuccess==${result}==", tag = "qwercloak")
                        }

                        override fun onError(response: Response<String>?) {
                            super.onError(response)
                            printGiraffe("==onError==${response?.body()?.toString()}==", tag = "qwercloak")
                        }
                    })
            }
        }else{
            isBlackCloak=s=="devoid"
        }
    }

    fun getVpnList(){
        if(loadingVpn){
            return
        }
        loadingVpn=true
        val start = System.currentTimeMillis()
        FirePointUtil.setPoint("giraffpe_feeri")
        OkGo.get<String>("$vpnUrl/lDtAroV/zoJ/")
            .headers("AAVO", Locale.getDefault().country)
            .headers("NHF", giraffeApp.packageName)
            .headers("CXBS", getAndroidId(giraffeApp))
            .execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {
                    loadingVpn=false
                    FirePointUtil.setPoint("giraffpe_mkmk")
                    val bundle = Bundle()
                    bundle.putLong("time",(System.currentTimeMillis()-start)/1000)
                    FirePointUtil.setPoint("giraffpe_lpop",bundle)
                    runCatching {
                        val s = response?.body()?.toString() ?: ""
                        val string = String(Base64.decode(s.substring(38, s.length).reversed(), Base64.DEFAULT))
                        printGiraffe(string,"qwervpn")
                        VpnInfoUtil0529.parseVpnJsonStr(string)
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    loadingVpn=false
                }
            })
    }


    fun vpnHeartUpload(connect:Boolean){
        val url="https://${ConnectVpnUtil0529.getHeartUploadIp()}/OINAzFb/Cjy/?" +
                "PaEwtJdlG=${giraffeApp.packageName}&HXO=${TbaInfo0529.stipple()}&" +
                "ckNLObik=${getAndroidId(giraffeApp)}&Yhr=${if (connect) "khUOelATfR" else "VFSrHaBCJ"}&jOCfQq=ss"
        printGiraffe(url,"qwerheart")
        OkGo.get<String>(url)
            .headers("AAVO", Locale.getDefault().country)
            .headers("NHF", giraffeApp.packageName)
            .headers("CXBS", getAndroidId(giraffeApp))
            .execute(object : StringCallback(){
                override fun onSuccess(response: Response<String>?) {}

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                }
            })
    }
}
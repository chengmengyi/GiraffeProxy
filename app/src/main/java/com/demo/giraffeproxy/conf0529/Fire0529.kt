package com.demo.giraffeproxy.conf0529

import com.demo.giraffeproxy.BuildConfig
import com.demo.giraffeproxy.admob0529.LoadAd0529Util
import com.demo.giraffeproxy.util.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.util.*

object Fire0529 {
    var hasFireData=false
    var planTwo=false
    var isHotStart=false

    private var pb_skg="1"
    private var pb_rqd="100"
    private var gir_refgira="2"
    private var gir_cloakgira="1"

    fun readFireConf(){
        if (!BuildConfig.DEBUG){
            val remoteConfig = Firebase.remoteConfig
            remoteConfig.fetchAndActivate().addOnCompleteListener {
                if (it.isSuccessful){
                    parseGiraffpeProxy(remoteConfig.getString("giraffpe_proxy"))
                    adConfig(remoteConfig.getString("giraffeff_ad"))
                    hasFireData=true
                }
            }
        }
    }

    private fun parseGiraffpeProxy(string: String){
        runCatching {
            val jsonObject = JSONObject(string)
            pb_skg=jsonObject.optString("pb_skg")
            pb_rqd=jsonObject.optString("pb_rqd")
            gir_refgira=jsonObject.optString("gir_refgira")
            gir_cloakgira=jsonObject.optString("gir_cloakgira")
        }
    }

    private fun adConfig(string: String){
        AdLimit0529Util.setMaxNum(string)
        MMKV.defaultMMKV().encode("giraffeff_ad",string)
    }

    fun getAdConfig():String{
        val s = MMKV.defaultMMKV().decodeString("giraffeff_ad") ?: ""
        if(s.isEmpty()){
            return Local0529.localAdStr
        }
        return s
    }

    fun createPlanType(){
        planTwo=false
        if((!isHotStart&&pb_skg=="1")||pb_skg=="2"){
            val nextInt = Random().nextInt(100)
            planTwo = str2Int(pb_rqd)>=nextInt
        }
    }

    private fun getGirRefgiraShowInterAd():Boolean{
        return when(gir_refgira){
            "1"-> true
            "2"->Referrer0529Util.getReferrerBuyUser()
            "3"->Referrer0529Util.getReferrerFBUser()
            else-> false
        }
    }

    private fun getCloakShowInterAd():Boolean{
        if(gir_cloakgira=="1"&&RequestUtil0529.isBlackCloak){
            return false
        }
        return true
    }

    fun cannotShowInterAd(type:String):Boolean{
        if (type==LoadAd0529Util.CONNECT||type==LoadAd0529Util.BACK){
            return !getGirRefgiraShowInterAd()||!getCloakShowInterAd()
        }
        return false
    }

}
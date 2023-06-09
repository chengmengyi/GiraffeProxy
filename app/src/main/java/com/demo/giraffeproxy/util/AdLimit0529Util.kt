package com.demo.giraffeproxy.util

import com.demo.giraffeproxy.BuildConfig
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object AdLimit0529Util {
    var limitUser=false
    var fullAdShowing=false
    private var maxShow=30
    private var maxClick=5

    private var currentShow=0
    private var currentClick=0

    private var refreshNativeAdMap= hashMapOf<String,Boolean>()

    fun canRefresh(type:String) = refreshNativeAdMap[type]?:true

    fun setRefreshBool(type: String,boolean: Boolean){
        refreshNativeAdMap[type]=boolean
    }

    private fun resetNativeMap(){
        refreshNativeAdMap.clear()
    }

    fun setMaxNum(string: String){
        runCatching {
            val jsonObject = JSONObject(string)
            maxShow=jsonObject.optInt("giraffeff_zs")
            maxClick=jsonObject.optInt("giraffeff_dj")
        }
    }

    fun readCurrentNum(){
        resetNativeMap()
        currentShow=MMKV.defaultMMKV().decodeInt(numKey("currentShow"),0)
        currentClick=MMKV.defaultMMKV().decodeInt(numKey("currentClick"),0)
    }

    fun addClickNum(){
        currentClick++
        MMKV.defaultMMKV().encode(numKey("currentClick"),currentClick)
    }

    fun addShowNum(){
        currentShow++
        MMKV.defaultMMKV().encode(numKey("currentShow"),currentShow)
    }

    fun adNumLimit()=currentShow>= maxShow||currentClick>= maxClick

    private fun numKey(key:String)="${SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis()))}_${key}"

    fun checkLimitUser(){
        RequestUtil0529.getIp {
            if(!BuildConfig.DEBUG){
                limitUser=RequestUtil0529.countryCode.limitArea0529()
            }
        }
    }

}
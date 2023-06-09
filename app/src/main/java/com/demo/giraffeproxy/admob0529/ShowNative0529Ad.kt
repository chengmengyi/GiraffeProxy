package com.demo.giraffeproxy.admob0529

import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.util.AdLimit0529Util
import com.demo.giraffeproxy.util.printGiraffe
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.*

class ShowNative0529Ad (
    private val type:String,
    private val baseAc0529: BaseAc0529){

    private var loopJob:Job?=null
    private var lastNativeAd:NativeAd?=null

    fun loop(){
        if(!AdLimit0529Util.canRefresh(type)){
            return
        }
        LoadAd0529Util.preLoad(type)
        loopJob = GlobalScope.launch(Dispatchers.Main)  {
            delay(300L)
            if (baseAc0529.resume0529){
                while (true){
                    if(!isActive){
                        break
                    }
                    val adByType = LoadAd0529Util.getAdByType(type)
                    if(null!=adByType&&adByType is NativeAd){
                        cancel()
                        if(baseAc0529.resume0529){
                            lastNativeAd?.destroy()
                            lastNativeAd=adByType
                            LoadAd0529Util.showNativeAd(baseAc0529,adByType){
                                AdLimit0529Util.addShowNum()
                                LoadAd0529Util.removeAdByType(type)
                                LoadAd0529Util.preLoad(type)
                                AdLimit0529Util.setRefreshBool(type,false)
                            }
                        }
                    }
                    delay(1000L)
                }
            }
        }
    }

    fun endLoop(){
        loopJob?.cancel()
        loopJob=null
    }
}
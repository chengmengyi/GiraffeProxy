package com.demo.giraffeproxy.admob0529

import com.demo.giraffeproxy.bean.Admob0529Bean
import com.demo.giraffeproxy.conf0529.Fire0529
import com.demo.giraffeproxy.util.printGiraffe
import org.json.JSONObject

object LoadAd0529Util:BaseAdmob() {
    const val OPEN="giraffeff_kp"
    const val HOME="giraffeff_ys"
    const val RESULT="giraffeff_yse"
    const val CONNECT="giraffeff_cp"
    const val BACK="giraffeff_cpe"

    fun preLoad(type: String,retryNum:Int=0){
        if(adNumLimit(type)||adLoading(type)||adHasCache(type)||Fire0529.cannotShowInterAd(type)){
            return
        }
        val adListByType = getAdListByType(type)
        if(adListByType.isEmpty()){
            printGiraffe("$type ad is numpty")
            return
        }
        loopLoadAd(type,adListByType.iterator(),retryNum)
    }

    private fun loopLoadAd(type: String, iterator: Iterator<Admob0529Bean>, retryNum:Int){
        loadAd(type,iterator.next()){
            if(null==it){
                if(iterator.hasNext()){
                    loopLoadAd(type,iterator,retryNum)
                }else{
                    loadingAdList.remove(type)
                    if(retryNum>0){
                        preLoad(type, retryNum = 0)
                    }
                }
            }else{
                loadingAdList.remove(type)
                adResultMap[type]=it
            }
        }
    }

    private fun adLoading(type: String):Boolean{
        if (loadingAdList.contains(type)){
            printGiraffe("$type ad is loading")
            return true
        }
        return false
    }

    private fun adHasCache(type: String):Boolean{
        if(adResultMap.containsKey(type)){
            val resultAdBean = adResultMap[type]
            if(null!=resultAdBean?.ad){
                return if(resultAdBean.adExpired()){
                    removeAdByType(type)
                    false
                }else{
                    printGiraffe("$type ad has cache")
                    true
                }
            }
        }
        return false
    }

    private fun getAdListByType(type: String):List<Admob0529Bean>{
        val list= arrayListOf<Admob0529Bean>()
        runCatching {
            val jsonArray = JSONObject(Fire0529.getAdConfig()).getJSONArray(type)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                list.add(
                    Admob0529Bean(
                        jsonObject.optString("giraffeff_id"),
                        jsonObject.optString("giraffeff_origin"),
                        jsonObject.optString("giraffeff_type"),
                        jsonObject.optInt("giraffeff_rank"),
                    )
                )
            }
        }
        return list.filter { it.giraffeff_origin == "admob" }.sortedByDescending { it.giraffeff_rank }
    }

    fun preLoadAllAd(){
        preLoad(OPEN, retryNum = 1)
        preLoad(HOME)
        preLoad(RESULT)
        preLoad(CONNECT)
    }

    fun planTwoReloadAllAd(){
        arrayOf(BACK, CONNECT, HOME, RESULT).forEach {
            adResultMap.remove(it)
            loadingAdList.clear()
            preLoad(it)
        }
    }

    fun disconnectReloadAllAd(){
        arrayOf(BACK, CONNECT, HOME, RESULT).forEach {
            val adResult0529Bean = adResultMap[it]
            if(null==adResult0529Bean||adResult0529Bean.adExpired()){
                loadingAdList.clear()
                preLoad(it)
            }
        }
    }
}
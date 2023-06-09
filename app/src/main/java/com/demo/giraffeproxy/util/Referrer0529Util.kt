package com.demo.giraffeproxy.util

import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.demo.giraffeproxy.BuildConfig
import com.demo.giraffeproxy.giraffeApp
import com.tencent.mmkv.MMKV
import org.json.JSONObject

object Referrer0529Util {

    fun readReferrerClient(){
        TbaInfo0529.uploadSessionEvent()
        if(readLocalReferrer().isEmpty()){
            val referrerClient = InstallReferrerClient.newBuilder(giraffeApp).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    runCatching {
                        when (responseCode) {
                            InstallReferrerClient.InstallReferrerResponse.OK -> {
                                val installReferrer = referrerClient.installReferrer.installReferrer
                                MMKV.defaultMMKV().encode("giraffe_referrer",installReferrer)
                                uploadInstallEvent(referrerClient.installReferrer)
                            }
                            else->{
                                uploadInstallEvent(null)
                            }
                        }
                    }
                    runCatching {
                        referrerClient.endConnection()
                    }
                }
                override fun onInstallReferrerServiceDisconnected() {
                }
            })
        }else{
            val install_giraffe = MMKV.defaultMMKV().decodeString("install_giraffe") ?: ""
            if(install_giraffe.isNotEmpty()){
                runCatching {
                    RequestUtil0529.tbaEvent(JSONObject(install_giraffe))
                }
            }
        }
    }

    private fun uploadInstallEvent(installReferrer: ReferrerDetails?) {
        if(TbaInfo0529.checkInstallHasReferrerPrompt() && TbaInfo0529.checkInstallNoReferrerPrompt()){
            return
        }
        TbaInfo0529.uploadInstallEvent(installReferrer)
    }

    private fun readLocalReferrer() = MMKV.defaultMMKV().decodeString("giraffe_referrer")?:""

    fun getReferrerBuyUser():Boolean{
        if(BuildConfig.DEBUG){
            return true
        }
        return readLocalReferrer().buyUser()
    }

    fun getReferrerFBUser():Boolean{
        if(BuildConfig.DEBUG){
            return true
        }
        return readLocalReferrer().isFB()
    }

    private fun String.buyUser()=contains("fb4a")|| contains("gclid")|| contains("not%20set")|| contains("youtubeads")|| contains("%7B%22")

    private fun String.isFB()=contains("fb4a")|| contains("facebook")
}
package com.demo.giraffeproxy.vpn0529

import com.demo.giraffeproxy.bean.Vpn0529Bean
import com.demo.giraffeproxy.conf0529.Local0529
import com.demo.giraffeproxy.conf0529.Local0529.localVpnStr
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import org.json.JSONArray

object VpnInfoUtil0529 {
    private val localVpnList= arrayListOf<Vpn0529Bean>()
    private val fireVpnList= arrayListOf<Vpn0529Bean>()
    private val fastCityList= arrayListOf<String>()

    fun initLocalVpn(){
        runCatching {
            localVpnList.clear()
            val jsonArray = JSONArray(localVpnStr)
            for (index in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(index)
                val vpn0529Bean = Vpn0529Bean(
                    giraffe_pwd = jsonObject.optString("giraffe_pwd"),
                    giraffe_account = jsonObject.optString("giraffe_account"),
                    giraffe_port = jsonObject.optInt("giraffe_port"),
                    giraffe_country = jsonObject.optString("giraffe_country"),
                    giraffe_city = jsonObject.optString("giraffe_city"),
                    giraffe_ip = jsonObject.optString("giraffe_ip"),
                )
                vpn0529Bean.writeVpnId()
                localVpnList.add(vpn0529Bean)
            }
        }
    }

    fun getVpnList()=fireVpnList.ifEmpty { localVpnList }

    fun getSmartVpn()=localVpnList.random()

    fun Vpn0529Bean.isFastVpn()=giraffe_country=="Super Fast Server"&&giraffe_ip.isEmpty()

    fun Vpn0529Bean.getVpnId():Long{
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.host==giraffe_ip&&it.remotePort==giraffe_port){
                return it.id
            }
        }
        return 0L
    }

    fun Vpn0529Bean.writeVpnId(){
        val profile = Profile(
            id = 0L,
            name = "$giraffe_country - $giraffe_city",
            host = giraffe_ip,
            remotePort = giraffe_port,
            password = giraffe_pwd,
            method = giraffe_account
        )

        var id:Long?=null
        ProfileManager.getActiveProfiles()?.forEach {
            if (it.remotePort==profile.remotePort&&it.host==profile.host){
                id=it.id
                return@forEach
            }
        }
        if (null==id){
            ProfileManager.createProfile(profile)
        }else{
            profile.id=id!!
            ProfileManager.updateProfile(profile)
        }
    }
}
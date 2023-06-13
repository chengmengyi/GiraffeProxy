package com.demo.giraffeproxy.vpn0529

import androidx.fragment.app.FragmentManager
import com.demo.giraffeproxy.Loading
import com.demo.giraffeproxy.bean.Vpn0529Bean
import com.demo.giraffeproxy.util.RequestUtil0529
import com.github.shadowsocks.database.Profile
import com.github.shadowsocks.database.ProfileManager
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

object VpnInfoUtil0529 {
    val allVpnList= arrayListOf<Vpn0529Bean>()
    val fastVpnList= arrayListOf<Vpn0529Bean>()

    fun parseVpnJsonStr(string: String){
        runCatching {
            val jsonObject = JSONObject(string)
            if (jsonObject.optInt("code")==200){
                val data = jsonObject.getJSONObject("data")
                val fast=data.getJSONArray("oSJWLeTvOW")
                val all=data.getJSONArray("MCuhvy")
                parseVpnData(fast, fastVpnList)
                parseVpnData(all, allVpnList)
            }
        }
    }

    private fun parseVpnData(json: JSONArray, list: ArrayList<Vpn0529Bean>) {
        if(json.length()>0){
            list.clear()
            for (index in 0 until json.length()){
                val jsonObject = json.getJSONObject(index)
                val serverBean = Vpn0529Bean(
                    giraffe_pwd = jsonObject.optString("rupdVQdLf"),
                    giraffe_account = jsonObject.optString("CyEnUzu"),
                    giraffe_port = jsonObject.optInt("dKeNuE"),
                    giraffe_country = jsonObject.optString("myTAzb"),
                    giraffe_city = jsonObject.optString("lJmhshAJzj"),
                    giraffe_ip = jsonObject.optString("dIXBKjlDC")
                )
                serverBean.writeVpnId()
                list.add(serverBean)
            }
        }
    }

    fun getSmartVpn():Vpn0529Bean?{
        if (fastVpnList.isEmpty()){
            return null
        }
        return fastVpnList.random(Random(System.currentTimeMillis()))
    }

    fun checkHasFastVpn(manager: FragmentManager, hasFast:(has:Boolean)->Unit){
        if(!ConnectVpnUtil0529.connectedVpnBean.isFastVpn()||fastVpnList.isNotEmpty()){
            hasFast.invoke(true)
            return
        }
        RequestUtil0529.getVpnList()
        Loading{ hasFast.invoke(false) }.show(manager,"Loading")
    }

    fun checkCanJumpVpnListAc(manager: FragmentManager, jump:()->Unit){
        if(allVpnList.isNotEmpty()){
            jump.invoke()
            return
        }
        if(RequestUtil0529.loadingVpn){
            Loading{}.show(manager,"Loading")
            return
        }
        RequestUtil0529.getVpnList()
        Loading{ jump.invoke() }.show(manager,"Loading")
    }

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
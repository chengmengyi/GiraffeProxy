package com.demo.giraffeproxy.ac0529

import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.R
import com.demo.giraffeproxy.util.ConnectTimeCallback
import com.demo.giraffeproxy.util.getVpnLogo
import com.demo.giraffeproxy.vpn0529.ConnectVpnUtil0529
import com.demo.giraffeproxy.vpn0529.ConnectVpnUtil0529.lastVpnBean
import com.demo.giraffeproxy.vpn0529.VpnConnectTimeUtil0529
import com.demo.giraffeproxy.vpn0529.VpnConnectTimeUtil0529.time
import kotlinx.android.synthetic.main.activity_reuslt.*
import java.lang.Exception

class ResultAc0529:BaseAc0529(R.layout.activity_reuslt), ConnectTimeCallback {
    private var connect=false

    override fun init0529View() {
        immersionBar.statusBarView(view_top).init()

        iv_back.setOnClickListener { finish() }

        tv_name.text=lastVpnBean.giraffe_country
        iv_logo.setImageResource(getVpnLogo(lastVpnBean.giraffe_country))

        connect=intent.getBooleanExtra("connect",false)
        if (connect){
            tv_time.isSelected=true
            VpnConnectTimeUtil0529.setConnectTimeCallback(this)
        }else{
            tv_time.isSelected=false
            tv_time.text=transTime(time)
            tv_result_center.text="Disconnected！"
            iv_result_bg.setImageResource(R.drawable.result3)
            iv_result_center.setImageResource(R.drawable.result4)
        }
    }

    override fun connectTime(time: Long) {
        tv_time.text=transTime(time)
    }

    override fun onDestroy() {
        super.onDestroy()
        VpnConnectTimeUtil0529.setConnectTimeCallback(null)
    }

    private fun transTime(t:Long):String{
        try {
            val shi=t/3600
            val fen= (t % 3600) / 60
            val miao= (t % 3600) % 60
            val s=if (shi<10) "0${shi}" else shi
            val f=if (fen<10) "0${fen}" else fen
            val m=if (miao<10) "0${miao}" else miao
            return "${s}:${f}:${m}"
        }catch (e: Exception){}
        return "00:00:00"
    }
}
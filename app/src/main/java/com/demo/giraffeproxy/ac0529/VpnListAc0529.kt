package com.demo.giraffeproxy.ac0529

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.R
import com.demo.giraffeproxy.adapter.VpnList0529Adapter
import com.demo.giraffeproxy.bean.Vpn0529Bean
import com.demo.giraffeproxy.util.showDisconnectDialog
import com.demo.giraffeproxy.vpn0529.ConnectVpnUtil0529
import kotlinx.android.synthetic.main.activity_vpn_list.*

class VpnListAc0529:BaseAc0529(R.layout.activity_vpn_list) {

    override fun init0529View() {
        immersionBar.statusBarView(view_top).init()

        iv_back.setOnClickListener { onBackPressed() }

        rv_list.apply {
            layoutManager=LinearLayoutManager(this@VpnListAc0529)
            adapter=VpnList0529Adapter(this@VpnListAc0529){ click(it) }
        }
    }

    private fun click(vpn0529Bean: Vpn0529Bean){
        val connectedVpn = ConnectVpnUtil0529.connectedVpn()
        val connectedVpnBean = ConnectVpnUtil0529.connectedVpnBean
        if(connectedVpn&&connectedVpnBean.giraffe_ip!=vpn0529Bean.giraffe_ip){
           showDisconnectDialog { chooseVpnBack("giraffe_dis",vpn0529Bean) }
        }else{
            if (connectedVpn){
                chooseVpnBack("",vpn0529Bean)
            }else{
                chooseVpnBack("giraffe_con",vpn0529Bean)
            }
        }
    }

    private fun chooseVpnBack(back:String,vpn0529Bean: Vpn0529Bean){
        ConnectVpnUtil0529.connectedVpnBean=vpn0529Bean
        setResult(529, Intent().apply {
            putExtra("back",back)
        })
        finish()
    }

    override fun onBackPressed() {
        finish()
    }
}
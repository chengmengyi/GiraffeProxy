package com.demo.giraffeproxy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.demo.giraffeproxy.R
import com.demo.giraffeproxy.bean.Vpn0529Bean
import com.demo.giraffeproxy.util.getVpnLogo
import com.demo.giraffeproxy.util.show
import com.demo.giraffeproxy.vpn0529.ConnectVpnUtil0529.connectedVpnBean
import com.demo.giraffeproxy.vpn0529.VpnInfoUtil0529
import com.demo.giraffeproxy.vpn0529.VpnInfoUtil0529.isFastVpn
import kotlinx.android.synthetic.main.item_vpn.view.*

class VpnList0529Adapter(
    private val context: Context,
    private val click:(bean:Vpn0529Bean)->Unit
):Adapter<VpnList0529Adapter.VpnView0529>() {

    private val list= arrayListOf<Vpn0529Bean>()

    init {
        list.add(Vpn0529Bean())
        list.addAll(VpnInfoUtil0529.getVpnList())
    }

    inner class VpnView0529(view:View):ViewHolder(view){
        init {
            view.setOnClickListener { click.invoke(list[layoutPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VpnView0529 {
        return VpnView0529(LayoutInflater.from(context).inflate(R.layout.item_vpn,parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: VpnView0529, position: Int) {
        with(holder.itemView){
            val vpn0529Bean = list[position]
            tv_name.text=if (vpn0529Bean.isFastVpn()){
                "Super Fast Server"
            }else{
                "${vpn0529Bean.giraffe_country} - ${vpn0529Bean.giraffe_city}"
            }
            iv_logo.setImageResource(getVpnLogo(vpn0529Bean.giraffe_country))

            val b = connectedVpnBean.giraffe_ip == vpn0529Bean.giraffe_ip
            iv_gou.show(b)
            item_layout.isSelected=b
        }
    }
}
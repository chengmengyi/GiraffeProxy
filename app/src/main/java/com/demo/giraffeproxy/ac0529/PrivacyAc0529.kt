package com.demo.giraffeproxy.ac0529

import com.demo.giraffeproxy.BaseAc0529
import com.demo.giraffeproxy.R
import kotlinx.android.synthetic.main.activity_privacy.*

class PrivacyAc0529:BaseAc0529(R.layout.activity_privacy){
    override fun init0529View() {
        immersionBar.statusBarView(view_top).init()
        iv_back.setOnClickListener { finish() }
        web.apply {
            settings.javaScriptEnabled=true
            loadUrl("https://sites.google.com/view/giraffe-proxy/%E9%A6%96%E9%A0%81")
        }
    }
}
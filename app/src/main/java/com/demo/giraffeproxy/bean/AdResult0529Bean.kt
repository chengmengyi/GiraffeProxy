package com.demo.giraffeproxy.bean

class AdResult0529Bean(
    val ad:Any?=null,
    val time:Long=0L
) {
    fun adExpired()=(System.currentTimeMillis() - time) >=3600L*1000
}
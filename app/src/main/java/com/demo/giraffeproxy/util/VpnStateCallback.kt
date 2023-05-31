package com.demo.giraffeproxy.util

interface VpnStateCallback {
    fun vpnConnected()
    fun vpnDisconnected()
}
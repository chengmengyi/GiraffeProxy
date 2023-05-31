package com.demo.giraffeproxy.util

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.demo.giraffeproxy.BuildConfig
import com.demo.giraffeproxy.R


fun printGiraffe(string: String){
    if(BuildConfig.DEBUG){
        Log.e("qwer",string)
    }
}

fun Context.showToast(string: String){
    Toast.makeText(this,string, Toast.LENGTH_LONG).show()
}

fun View.show(show:Boolean){
    visibility=if (show) View.VISIBLE else View.GONE
}

private fun Context.getNetworkStatus(): Int {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
        if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
            return 2
        } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
            return 0
        }
    } else {
        return 1
    }
    return 1
}

fun AppCompatActivity.checkHasNetWork():Boolean{
    if(getNetworkStatus()==1){
        AlertDialog.Builder(this).apply {
            setMessage("Network request timed out. Please make sure your network is connected")
            setPositiveButton("OK", null)
            show()
        }
        return false
    }
    return true
}

fun getVpnLogo(name:String)=when(name.replace(" ".toRegex(), "").toLowerCase()){
    "australia"-> R.drawable.australia
    "belgium"-> R.drawable.belgium
    "brazil"-> R.drawable.brazil
    "canada"-> R.drawable.canada
    "france"-> R.drawable.france
    "hongkong"-> R.drawable.hongkong
    "germany"-> R.drawable.germany
    "india"-> R.drawable.india
    "ireland"-> R.drawable.ireland
    "italy"-> R.drawable.italy
    "koreasouth"-> R.drawable.koreasouth
    "netherlands"-> R.drawable.netherlands
    "newzealand"-> R.drawable.newzealand
    "norway"-> R.drawable.norway
    "singapore"-> R.drawable.singapore
    "sweden"-> R.drawable.sweden
    "switzerland"-> R.drawable.switzerland
    "turkey"-> R.drawable.turkey
    "unitedkingdom"-> R.drawable.unitedkingdom
    "unitedstates"-> R.drawable.unitedstates
    "japan"-> R.drawable.japan
    else-> R.drawable.fast
}


fun AppCompatActivity.showDisconnectDialog(sure:()->Unit){
    AlertDialog.Builder(this).apply {
        setMessage("If you want to connect to another VPN, you need to disconnect the current connection first. Do you want to disconnect the current connection?")
        setPositiveButton("sure") { _, _ ->
            sure.invoke()
        }
        setNegativeButton("cancel",null)
        show()
    }
}
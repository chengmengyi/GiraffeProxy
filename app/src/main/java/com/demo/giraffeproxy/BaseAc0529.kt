package com.demo.giraffeproxy

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar

abstract class BaseAc0529(private val layoutId:Int) : AppCompatActivity(){
    var resume0529=false
    lateinit var immersionBar: ImmersionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        height0529()
        setContentView(layoutId)
        immersionBar= ImmersionBar.with(this).apply {
            statusBarAlpha(0f)
            autoDarkModeEnable(true)
            statusBarDarkFont(false)
            init()
        }
        init0529View()
    }

    abstract fun init0529View()

    override fun onResume() {
        super.onResume()
        resume0529=true
    }

    override fun onPause() {
        super.onPause()
        resume0529=false
    }

    override fun onStop() {
        super.onStop()
        resume0529=false
    }

    private fun height0529(){
        val metrics: DisplayMetrics = resources.displayMetrics
        val td = metrics.heightPixels / 760f
        val dpi = (160 * td).toInt()
        metrics.density = td
        metrics.scaledDensity = td
        metrics.densityDpi = dpi
    }
}
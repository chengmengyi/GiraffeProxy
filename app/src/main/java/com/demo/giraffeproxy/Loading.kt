package com.demo.giraffeproxy

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.layout_loading.*
import kotlinx.coroutines.*

class Loading(private val closeDialog:()->Unit):DialogFragment() {
    private var objectAnimator: ObjectAnimator?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.layout_loading,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCancelable(false)
        imm()
        startAnimator()
        delayDismiss()
    }

    private fun imm(){
        ImmersionBar.with(this).apply {
            statusBarAlpha(0f)
            autoDarkModeEnable(true)
            statusBarDarkFont(false)
            init()
        }

    }
    private fun delayDismiss(){
        GlobalScope.launch {
            delay(2000L)
            withContext(Dispatchers.Main){
                dismiss()
                closeDialog.invoke()
            }
        }
    }

    private fun startAnimator(){
        objectAnimator= ObjectAnimator.ofFloat(iv_loading, "rotation", 0f, 360f).apply {
            duration=1000L
            repeatCount= ValueAnimator.INFINITE
            repeatMode= ObjectAnimator.RESTART
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        objectAnimator?.removeAllUpdateListeners()
        objectAnimator?.cancel()
    }
}
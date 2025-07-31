package com.kitsuri.ImDroid


import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams

class HelperActivity : Activity() {

    companion object {
        init {
            System.loadLibrary("ImDroid")
        }

        lateinit var manager: WindowManager
        lateinit var vParams: LayoutParams
        lateinit var vTouch: View
        lateinit var windowManager: WindowManager
        lateinit var xfqManager: WindowManager

        fun start(context: Context) {
            manager = (context as Activity).windowManager
            vParams = getAttributes(false)
            val wParams = getAttributes(true)
            val display = NativeBridge(context)
            vTouch = View(context)
            manager.addView(vTouch, vParams)
            manager.addView(display, wParams)

            vTouch.setOnTouchListener { _, event ->
                val action = event.action
                when (action) {
                    MotionEvent.ACTION_MOVE,
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_UP -> {
                        NativeBridge.motionEventClick(action != MotionEvent.ACTION_UP, event.rawX, event.rawY)
                    }
                }
                false
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        val rect = NativeBridge.getWindowRect().split("|")
                        vParams.x = rect[0].toInt()
                        vParams.y = rect[1].toInt()
                        vParams.width = rect[2].toInt()
                        vParams.height = rect[3].toInt()
                        manager.updateViewLayout(vTouch, vParams)
                    } catch (e: Exception) {
                        // Handle exception silently as in original
                    }
                    handler.postDelayed(this, 20)
                }
            }, 20)
        }

        private fun getAttributes(isWindow: Boolean): LayoutParams {
            val params = LayoutParams().apply {
                width = if (isWindow) LayoutParams.MATCH_PARENT else LayoutParams.WRAP_CONTENT
                height = if (isWindow) LayoutParams.MATCH_PARENT else LayoutParams.WRAP_CONTENT
                x = 0
                y = 100
                type = LayoutParams.TYPE_APPLICATION
                flags = LayoutParams.FLAG_FULLSCREEN or
                        LayoutParams.FLAG_TRANSLUCENT_STATUS or
                        LayoutParams.FLAG_TRANSLUCENT_NAVIGATION or
                        LayoutParams.FLAG_NOT_FOCUSABLE

                if (isWindow) {
                    flags = flags or LayoutParams.FLAG_NOT_TOUCH_MODAL or LayoutParams.FLAG_NOT_TOUCHABLE
                }

                format = PixelFormat.RGBA_8888
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode = LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
                gravity = Gravity.LEFT or Gravity.TOP
                x = 0
                y = 0
            }
            return params
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start(this)
    }
}
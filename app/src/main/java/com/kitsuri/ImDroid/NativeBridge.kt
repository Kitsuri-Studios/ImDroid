package com.kitsuri.ImDroid

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class NativeBridge(context: Context) : GLSurfaceView(context), GLSurfaceView.Renderer {

    companion object {
        var fontData: ByteArray? = null

        external fun init()
        external fun resize(width: Int, height: Int)
        external fun step()
        external fun shutdownWindow()
        external fun motionEventClick(down: Boolean, posX: Float, posY: Float)
        external fun getWindowRect(): String
    }

    init {
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.RGB_888)
        setEGLContextClientVersion(3)
        setRenderer(this)
    }

    override fun onDrawFrame(gl: GL10) {
        step()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        resize(width, height)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        init()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        shutdownWindow()
    }
}
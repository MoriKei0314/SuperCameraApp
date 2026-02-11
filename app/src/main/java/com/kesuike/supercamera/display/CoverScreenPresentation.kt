package com.kesuike.supercamera.display

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.camera.view.PreviewView

/**
 * 背面（外側）ディスプレイにプレビューを表示するためのPresentation
 */
class CoverScreenPresentation(outerDisplay: Display, context: Context) : 
    Presentation(context, outerDisplay) {

    private lateinit var previewView: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // シンプルなFrameLayoutの中にPreviewViewを配置
        val root = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        previewView = PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        root.addView(previewView)
        setContentView(root)
    }

    fun getPreviewView(): PreviewView = previewView
}

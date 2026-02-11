package com.kesuike.supercamera.ui

import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraScreen(onPreviewViewCreated: (PreviewView) -> Unit) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                onPreviewViewCreated(this)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

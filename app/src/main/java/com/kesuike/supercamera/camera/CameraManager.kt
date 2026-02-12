package com.kesuike.supercamera.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * CameraXのセットアップとライフサイクル管理を行うクラス
 */
class CameraManager(private val context: Context) {

    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        mainPreviewView: PreviewView,
        coverPreviewView: PreviewView? = null
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // メインディスプレイ用プレビューUseCase
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(mainPreviewView.surfaceProvider)
            }

            // カバーディスプレイ用プレビューUseCase（存在する場合）
            val coverPreview = coverPreviewView?.let {
                Preview.Builder().build().also { cp ->
                    cp.setSurfaceProvider(it.surfaceProvider)
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // 既存のバインドを解除
                cameraProvider.unbindAll()

                // デバイスの状態に合わせてUseCaseをバインド
                if (coverPreview != null) {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        coverPreview
                    )
                } else {
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                }
            } catch (exc: Exception) {
                // ログ出力等のエラー処理
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }
}

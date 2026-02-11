package com.kesuike.supercamera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kesuike.supercamera.display.CoverScreenPresentation
import com.kesuike.supercamera.display.FoldableManager
import com.kesuike.supercamera.ui.CameraScreen

class MainActivity : ComponentActivity(), FoldableManager.FoldStateListener {

    private lateinit var foldableManager: FoldableManager
    private var coverPresentation: CoverScreenPresentation? = null
    private lateinit var displayManager: DisplayManager

    // パーミッションリクエスト用
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // カメラ使用可能
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        foldableManager = FoldableManager(this)
        foldableManager.setListener(this)

        checkCameraPermission()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CameraScreen()
                }
            }
        }

        // 折りたたみ状態の監視開始
        foldableManager.watchFoldState(lifecycleScope)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != 
            PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onFoldStateChanged(isFolded: Boolean, isHalfOpened: Boolean) {
        // デバイスが開かれた（または半分開いた）ときに背面ディスプレイを探す
        if (!isFolded) {
            showCoverDisplayPresentation()
        } else {
            hideCoverDisplayPresentation()
        }
    }

    private fun showCoverDisplayPresentation() {
        if (coverPresentation != null) return

        val displays = displayManager.displays
        // 背面ディスプレイ（通常はインデックス1以降や、特定のフラグを持つもの）を検索
        // 簡易的に、メイン（Display.DEFAULT_DISPLAY）以外のディスプレイを選択
        val secondaryDisplay = displays.firstOrNull { it.displayId != Display.DEFAULT_DISPLAY }

        secondaryDisplay?.let {
            coverPresentation = CoverScreenPresentation(it, this)
            coverPresentation?.show()
            // ここでカバー側のPreviewViewをCameraManager等に渡してバインドする処理が必要
        }
    }

    private fun hideCoverDisplayPresentation() {
        coverPresentation?.dismiss()
        coverPresentation = null
    }

    override fun onStop() {
        super.onStop()
        hideCoverDisplayPresentation()
    }
}

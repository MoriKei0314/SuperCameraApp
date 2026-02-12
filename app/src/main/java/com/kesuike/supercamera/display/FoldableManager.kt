package com.kesuike.supercamera.display

import android.content.Context
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.collect

/**
 * デバイスの折りたたみ状態（FLAT, HALF_OPENED等）を監視するクラス
 */
class FoldableManager(private val context: Context) {

    interface FoldStateListener {
        fun onFoldStateChanged(isFolded: Boolean, isHalfOpened: Boolean)
    }

    private var listener: FoldStateListener? = null

    fun setListener(listener: FoldStateListener) {
        this.listener = listener
    }

    /**
     * WindowManagerを使用して状態監視を開始
     */
    fun watchFoldState(scope: CoroutineScope) {
        scope.launch {
            WindowInfoTracker.getOrCreate(context)
                .windowLayoutInfo(context as android.app.Activity)
                .distinctUntilChanged()
                .collect { layoutInfo ->
                    handleWindowLayoutInfo(layoutInfo)
                }
        }
    }

    private fun handleWindowLayoutInfo(layoutInfo: WindowLayoutInfo) {
        val foldingFeature = layoutInfo.displayFeatures
            .filterIsInstance<FoldingFeature>()
            .firstOrNull()

        if (foldingFeature != null) {
            val isHalfOpened = foldingFeature.state == FoldingFeature.State.HALF_OPENED
            val isFlat = foldingFeature.state == FoldingFeature.State.FLAT
            // デバイスが開いている状態（FLAT）または半分開いている状態（HALF_OPENED）
            listener?.onFoldStateChanged(isFolded = false, isHalfOpened = isHalfOpened)
        } else {
            // foldingFeatureがない場合は、通常のスマホ（または完全に閉じている状態）
            listener?.onFoldStateChanged(isFolded = true, isHalfOpened = false)
        }
    }
}

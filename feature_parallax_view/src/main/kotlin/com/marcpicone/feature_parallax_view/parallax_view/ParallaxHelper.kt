package com.marcpicone.feature_parallax_view.parallax_view

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex

/**
 * Applies the parallax transforms—translation, rotation and cameraDistance—plus zIndex.
 *
 * @param state            State container for the current drag offset.
 * @param zFactor          Layer depth factor (0…1).
 */
internal fun Modifier.parallaxLayerImpl(
    state: ParallaxState,
    zFactor: Float
): Modifier {
    return composed {
        val density = LocalDensity.current.density
        val cameraDistance = 8 * density
        val offset = state.offset.value
        val normalizedX = (offset.x / state.maxDragXPx).coerceIn(-1f, 1f)
        val normalizedY = (offset.y / state.maxDragYPx).coerceIn(-1f, 1f)
        val rotationY = normalizedX * state.maxRotationAngleDegree
        val rotationX = -normalizedY * state.maxRotationAngleDegree

        graphicsLayer {
            translationX = offset.x * zFactor
            translationY = offset.y * zFactor
            this.rotationY = rotationY
            this.rotationX = rotationX
            this.cameraDistance = cameraDistance
        }.zIndex(zFactor)
    }
}

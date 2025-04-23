package com.marcpicone.feature_parallax_view.parallax_view

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

/**
 * Extension for Modifier to install a drag gesture detector that updates the provided [ParallaxState].
 *
 * This gesture must be applied only once on a parent container to avoid multiple simultaneous listeners.
 *
 * @param state The [ParallaxState] whose onDrag and onRelease will be called during gesture.
 * @return A [Modifier] that intercepts pointer input and forwards drag events to [state].
 */
fun Modifier.parallaxGesture(
    state: ParallaxState
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    pointerInput(state) {
        detectDragGestures(
            onDrag = { _, dragAmount -> coroutineScope.launch { state.onDrag(dragAmount) } },
            onDragEnd = { coroutineScope.launch { state.onRelease() } }
        )
    }
}

/**
 * Extension for Modifier to apply a parallax transform at a given depth based on [ParallaxState].
 *
 * @param state The [ParallaxState] supplying the current drag offset and configuration.
 * @param zFactor Normalized depth factor: 0f = background (the least movement), 1f = foreground (full movement).
 *                Values outside 0f…1f will be coerced internally.
 * @return A [Modifier] that applies translation, rotation and z-index based on [state] and [zFactor].
 */
fun Modifier.parallaxLayer(
    state: ParallaxState,
    @FloatRange(from = 0.0, to = 1.0)
    zFactor: Float
): Modifier = composed {
    check(zFactor in 0f..1f) { "zFactor should be in 0f..1f" }
    val density = LocalDensity.current.density
    graphicsLayer {
        val offset = state.offset.value
        val normalizedX = (offset.x / state.maxDragXPx).coerceIn(-1f, 1f)
        val normalizedY = (offset.y / state.maxDragYPx).coerceIn(-1f, 1f)
        translationX = offset.x * zFactor
        translationY = offset.y * zFactor
        rotationY = normalizedX * state.maxRotationAngleDegree
        rotationX = -normalizedY * state.maxRotationAngleDegree
        cameraDistance = 8 * density
    }.zIndex(zFactor)
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF101010
)
@Composable
private fun ParallaxViewPreview() {
    val state: ParallaxState = rememberParallaxState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .parallaxGesture(state),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .parallaxLayer(state, zFactor = 0f)
                .fillMaxSize()
                .background(Color.Red)
        )
        Box(
            modifier = Modifier
                .parallaxLayer(state, zFactor = 0.5f)
                .fillMaxSize(0.7f)
                .background(Color.Black.copy(alpha = 0.3f))
        )
        Box(
            modifier = Modifier
                .parallaxLayer(state, zFactor = 1f)
                .fillMaxSize(0.6f)
                .background(Color.Blue)
        )
    }
}

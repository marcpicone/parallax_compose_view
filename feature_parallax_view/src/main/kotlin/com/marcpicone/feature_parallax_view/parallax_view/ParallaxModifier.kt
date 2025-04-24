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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.marcpicone.feature_parallax_view.parallax_view.internal.parallaxLayerImpl
import kotlinx.coroutines.launch

/**
 * Installs a drag gesture detector on this [Modifier], forwarding drag events to the given [ParallaxState].
 *
 * Use once at the highest container that should respond to pointer drags. This detector will
 * capture pointer input and invoke [ParallaxState.onDrag] and [ParallaxState.onRelease] accordingly.
 *
 * **Example:**
 * ```kotlin
 * Box(
 *   Modifier
 *     .fillMaxSize()
 *     .parallaxGesture(state)
 * ) {
 *   // child layers with .parallaxLayer()
 * }
 * ```
 *
 * @receiver The [Modifier] on which to install the gesture detector.
 * @param state The [ParallaxState] that receives drag deltas and triggers spring-back.
 * @return A [Modifier] that intercepts pointer input and updates [state].
 */
fun Modifier.parallaxGesture(
    state: ParallaxState
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    pointerInput(state) {
        detectDragGestures(
            onDrag = { _, dragAmount ->
                coroutineScope.launch { state.onDrag(dragAmount) }
            },
            onDragEnd = {
                coroutineScope.launch { state.onRelease() }
            }
        )
    }
}

/**
 * Applies a parallax transform to this [Modifier] based on the provided [ParallaxState] and depth factor.
 *
 * Attach child layers inside a gesture-capturing container for this effect to work correctly.
 * Layers closer to the front should use higher [zFactor] values.
 *
 * **Example:**
 * ```kotlin
 * Box(
 *   Modifier
 *     .fillMaxSize()
 *     .parallaxGesture(state)
 * ) {
 *   Box(Modifier.parallaxLayer(state, zFactor = 0f)) // background
 *   Box(Modifier.parallaxLayer(state, zFactor = 0.5f)) // middle
 *   Box(Modifier.parallaxLayer(state, zFactor = 1f)) // foreground
 * }
 * ```
 *
 * @receiver The [Modifier] to which the parallax transform is applied.
 * @param state The [ParallaxState] supplying current offset and configuration.
 * @param zFactor Normalized depth (0f = back, 1f = front). Values outside this range will throw.
 * @return A [Modifier] that applies translation, rotation, cameraDistance, and zIndex transforms.
 * @throws IllegalArgumentException if [zFactor] is not in the range 0f…1f.
 */
fun Modifier.parallaxLayer(
    state: ParallaxState,
    @FloatRange(from = 0.0, to = 1.0)
    zFactor: Float
): Modifier = composed {
    require(zFactor in 0f..1f) { "zFactor should be in 0f..1f" }

    this.parallaxLayerImpl(
        state = state,
        zFactor = zFactor
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF101010
)
@Composable
private fun ParallaxModifierPreview() {
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

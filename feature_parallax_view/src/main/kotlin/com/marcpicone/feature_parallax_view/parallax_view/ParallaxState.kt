package com.marcpicone.feature_parallax_view.parallax_view

import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * State container for ParallaxView, managing the animated offset and effect configuration.
 *
 * Use [rememberParallaxState] to obtain a remembered instance tied to composition lifecycle.
 * The state survives recompositions and is cancelled automatically when the host composable
 * leaves the composition.
 *
 * @param maxDragXPx              Maximum horizontal drag distance **in pixels** (converted from Dp).
 * @param maxDragYPx              Maximum vertical drag distance **in pixels** (converted from Dp).
 * @param maxRotationAngleDegree  Maximum rotation angle applied to each layer, in degrees.
 * @param smoothingX              Horizontal smoothing factor (0f…1f),0 means no smoothing (raw drag), closer to 1f means more smoothing, 1f means axis rotation locked.
 * @param smoothingY              Vertical smoothing factor (0f…1f),0 means no smoothing (raw drag), closer to 1f means more smoothing, 1f means axis rotation locked.
 * @param releaseAnimationSpec    [AnimationSpec]<Offset> used for animation when drag is released.
 *
 * @property offset               Current drag offset as a [State]<Offset>, updated during gestures.
 *
 * @see onDrag
 * @see onRelease
 */
class ParallaxState internal constructor(
    @Px internal val maxDragXPx: Float,
    @Px internal val maxDragYPx: Float,
    @FloatRange(from = 0.0, to = 360.0)
    internal val maxRotationAngleDegree: Float,
    @FloatRange(from = 0.0, to = 1.0)
    internal val smoothingX: Float,
    @FloatRange(from = 0.0, to = 1.0)
    internal val smoothingY: Float,
    internal val releaseAnimationSpec: AnimationSpec<Offset>
) {
    init {
        require(maxRotationAngleDegree in 0f..360f) { "Rotation angle must be between 0 and 360" }
        require(smoothingX in 0f..1f) { "Smoothing factor must be between 0 and 1" }
        require(smoothingY in 0f..1f) { "Smoothing factor must be between 0 and 1" }
    }
    val offset: State<Offset> get() = offsetAnimatable.asState()

    private val offsetAnimatable = Animatable(
        initialValue = Offset.Zero,
        typeConverter = Offset.VectorConverter
    )

    suspend fun onDrag(drag: Offset) {
        val smoothX = drag.x * smoothingX
        val smoothY = drag.y * smoothingY
        val newOffset = offsetAnimatable.value + drag
        val coerced = Offset(
            x = (newOffset.x - smoothX).coerceIn(-maxDragXPx, maxDragXPx),
            y = (newOffset.y - smoothY).coerceIn(-maxDragYPx, maxDragYPx)
        )
        offsetAnimatable.snapTo(coerced)
    }

    suspend fun onRelease() {
        offsetAnimatable.animateTo(
            targetValue = Offset.Zero,
            animationSpec = releaseAnimationSpec
        )
    }
}

/**
 * Creates and remembers a [ParallaxState] tied to the composition lifecycle.
 *
 * This state survives recompositions as long as the calling composable remains in the
 * composition. When the composable leaves the composition tree, any ongoing animations
 * or spring-back jobs are automatically cancelled.
 *
 * Use this helper to hoist drag-and-release handling out of your UI layer, or to share
 * the same parallax state across multiple components.
 *
 * **Example:**
 * ```kotlin
 * @Composable
 * fun MyParallaxScreen() {
 *   val state = rememberParallaxState(
 *     maxDragXDp             = 80.dp,
 *     maxDragYDp             = 8.dp,
 *     maxRotationAngleDegree = 20f,
 *     smoothingX             = 0.5f,
 *     smoothingY             = 0.95f
 *   )
 *   ParallaxView(state) {
 *     layer { /* ... */ }
 *   }
 * }
 * ```
 *
 * @param maxDragXDp             Maximum horizontal drag distance, in DP.
 * @param maxDragYDp             Maximum vertical drag distance, in DP.
 * @param maxRotationAngleDegree Max rotation on X/Y axes, in degrees.
 * @param smoothingX              Horizontal smoothing factor (0f…1f), 0 means no smoothing (raw drag), closer to 1f means more smoothing, 1f means axis rotation locked.
 * @param smoothingY              Vertical smoothing factor (0f…1f), 0 means no smoothing (raw drag), closer to 1f means more smoothing, 1f means axis rotation locked.
 * @param releaseAnimationSpec   `AnimationSpec<Offset>` for the spring-back behavior.
 * @return The remembered [ParallaxState] instance, cancelled when the composable leaves the composition.
 * @sample com.marcpicone.feature_parallax_view.parallax_view.ParallaxViewPreview
 */
@Composable
fun rememberParallaxState(
    maxDragXDp: Dp = 80.dp,
    maxDragYDp: Dp = 8.dp,
    @FloatRange(from = 0.0, to = 360.0)
    maxRotationAngleDegree: Float = 20f,
    @FloatRange(from = 0.0, to = 1.0)
    smoothingX: Float = 0.5f,
    @FloatRange(from = 0.0, to = 1.0)
    smoothingY: Float = 0.95f,
    releaseAnimationSpec: AnimationSpec<Offset> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
): ParallaxState {
    val density = LocalDensity.current
    val maxDragXPx = with(density) { maxDragXDp.toPx() }
    val maxDragYPx = with(density) { maxDragYDp.toPx() }

    return remember {
        ParallaxState(
            maxDragXPx = maxDragXPx,
            maxDragYPx = maxDragYPx,
            maxRotationAngleDegree = maxRotationAngleDegree,
            smoothingX = smoothingX,
            smoothingY = smoothingY,
            releaseAnimationSpec = releaseAnimationSpec
        )
    }
}

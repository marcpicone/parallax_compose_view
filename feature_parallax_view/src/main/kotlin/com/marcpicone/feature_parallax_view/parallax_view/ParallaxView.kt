package com.marcpicone.feature_parallax_view.parallax_view

import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

@DslMarker
annotation class ParallaxDsl

/**
 * DSL scope for declaring parallax layers.
 *
 * Each call to `layer { ... }` appends a layer in stacking order:
 * the first layer is rendered at the back, the last at the front.
 */
@ParallaxDsl
class ParallaxScope internal constructor() {
    internal val layers = mutableListOf<ParallaxLayer>()

    /**
     * Adds a parallax layer with custom drawing logic.
     * @param content Composable extension on Modifier where `this` refers to the combined modifier.
     */
    fun layer(content: ParallaxLayer) {
        layers += content
    }
}

typealias ParallaxLayer = @Composable Modifier.() -> Unit

/**
 * A parallax container using a Kotlin DSL to define multiple layers.
 *
 * @param state     Hoisted [ParallaxState] controlling drag limits, rotation angles, and spring animation.
 * @param modifier  Modifier applied to the parent container capturing drag gestures.
 * @param content   DSL block where you declare each layer by calling `layer { ... }`.
 *                  Layers are rendered in the order they are added: first is back, last is front.
 */
@Composable
fun ParallaxView(
    state: ParallaxState,
    modifier: Modifier = Modifier,
    content: ParallaxScope.() -> Unit
) {
    val parallaxScope = remember(content) { ParallaxScope().apply(content) }
    require(parallaxScope.layers.isNotEmpty()) { "ParallaxView must have at least one layer" }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures(
                onDrag = { _, drag -> coroutineScope.launch { state.onDrag(drag) } },
                onDragEnd = { coroutineScope.launch { state.onRelease() } }
            )
        },
        contentAlignment = Alignment.Center
    ) {
        ParallaxLayersView(
            parallaxLayers = parallaxScope.layers,
            state = state
        )
    }
}

@Composable
private fun ParallaxLayersView(
    parallaxLayers: List<@Composable ((Modifier) -> Unit)>,
    state: ParallaxState
) {
    val cameraDistance = 8 * getDensity().density
    val count = parallaxLayers.size
    val offset = state.offset.value
    val normalizedX = (offset.x / state.maxDragXPx).coerceIn(-1f, 1f)
    val normalizedY = (offset.y / state.maxDragYPx).coerceIn(-1f, 1f)
    val rotationY = normalizedX * state.maxRotationAngleDegree
    val rotationX = -normalizedY * state.maxRotationAngleDegree

    parallaxLayers.forEachIndexed { index, layer ->
        val zFactor = (index + 1) / count.toFloat()
        val modifier = Modifier.graphicsLayer {
            translationX = offset.x * zFactor
            translationY = offset.y * zFactor
            this.rotationY = rotationY
            this.rotationX = rotationX
            this.cameraDistance = cameraDistance
        }.zIndex(zFactor)

        layer(modifier)
    }
}

/**
 * @param maxDragXPx              Maximum horizontal drag distance in Px.
 * @param maxDragYPx              Maximum vertical drag distance in Px.
 * @param maxRotationAngleDegree  Maximum rotation angle in degrees.
 * @param smoothingX              Horizontal smoothing factor (0f…1f).
 * @param smoothingY              Vertical smoothing factor (0f…1f).
 * @param releaseAnimationSpec    AnimationSpec for spring-back behavior.
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
 * Creates and remembers a [ParallaxState] with configurable parameters.
 *
 * @param maxDragXDp              Maximum horizontal drag distance in Dp.
 * @param maxDragYDp              Maximum vertical drag distance in Dp.
 * @param maxRotationAngleDegree  Maximum rotation angle in degrees.
 * @param smoothingX              Horizontal smoothing factor (0f…1f).
 * @param smoothingY              Vertical smoothing factor (0f…1f).
 * @param releaseAnimationSpec    AnimationSpec for spring-back behavior.
 * @return A remembered [ParallaxState] instance.
 *
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
    val density = getDensity()
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

@Composable
private fun getDensity(): Density = LocalDensity.current

@Preview(
    showBackground = true,
    backgroundColor = 0xFF101010
)
@Composable
private fun ParallaxViewPreview() {
    ParallaxView(
        state = rememberParallaxState(),
        modifier = Modifier.size(200.dp)
    ) {
        layer {
            Box(
                modifier = fillMaxSize()
                    .background(Color.Red)
            )
        }
        layer {
            Box(
                modifier = fillMaxSize(0.7f)
                    .background(Color.Black.copy(alpha = 0.3f))
            )
        }
        layer {
            Box(
                modifier = fillMaxSize(0.6f)
                    .background(Color.Blue)
            )
        }
    }
}

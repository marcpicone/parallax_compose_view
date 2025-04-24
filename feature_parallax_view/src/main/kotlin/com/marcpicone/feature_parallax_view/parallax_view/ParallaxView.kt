package com.marcpicone.feature_parallax_view.parallax_view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marcpicone.feature_parallax_view.parallax_view.internal.parallaxLayerImpl
import kotlinx.coroutines.launch

/**
 * A parallax container using a Kotlin DSL to define multiple layers.
 *
 * @param state     Hoisted [ParallaxState] controlling drag limits, rotation angles, and spring animation.
 * @param modifier  Modifier applied to the parent Box container capturing drag gestures.
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
    val coroutineScope = rememberCoroutineScope()
    require(parallaxScope.layers.isNotEmpty()) { "ParallaxView must have at least one layer" }

    Box(
        modifier = modifier.pointerInput(state) {
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
    val count = parallaxLayers.size

    parallaxLayers.forEachIndexed { index, layer ->
        val zFactor = (index + 1) / count.toFloat()
        val modifier = Modifier.parallaxLayerImpl(state = state, zFactor = zFactor)
        layer(modifier)
    }
}

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

package com.marcpicone.feature_parallax_view.parallax_view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@DslMarker
annotation class ParallaxDsl

typealias ParallaxLayer = @Composable Modifier.() -> Unit

/**
 * DSL scope for declaring parallax layers within a [ParallaxView].
 *
 * Inside the `ParallaxView { ... }` block, invoke `layer { ... }` for each layer.
 * Layers are rendered in the order they are added:
 * the first layer becomes the background, the last becomes the foreground.
 *
 */
@ParallaxDsl
class ParallaxScope {
    internal val layers = mutableListOf<ParallaxLayer>()

    /**
     * Adds a single parallax layer with custom drawing logic.
     *
     * **Note:** Use the **provided** `Modifier` in this block so the built-in parallax
     * transforms are applied correctly. Do **not** start a new `Modifier` chain (e.g.
     * `Modifier.fillMaxSize()`), as that would override or skip the effect entirely.
     *
     * @param content The drawing logic for this layer.
     * @receiver The Modifier passed by ParallaxView for this layer.
     *
     * **Example:**
     * ```kotlin
     * ParallaxView(state) {
     *   layer {
     *     Box(
     *         // DO : Use the Modifier of the layer
     *         modifier = fillMaxSize()
     *             .background(Color.Red)
     *     )
     *   }
     *   layer {
     *     Box(
     *         // DO NOT : Start a new Modifier chain
     *        modifier = Modifier.fillMaxSize(0.7f)
     *            .background(Color.Black.copy(alpha = 0.3f))
     *     )
     *   }
     * }
     * ```
     */
    fun layer(content: ParallaxLayer) {
        layers += content
    }
}

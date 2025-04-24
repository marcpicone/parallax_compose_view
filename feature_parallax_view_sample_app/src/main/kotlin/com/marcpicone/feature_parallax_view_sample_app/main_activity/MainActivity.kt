package com.marcpicone.feature_parallax_view_sample_app.main_activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marcpicone.feature_parallax_view.parallax_view.ParallaxView
import com.marcpicone.feature_parallax_view.parallax_view.parallaxGesture
import com.marcpicone.feature_parallax_view.parallax_view.parallaxLayer
import com.marcpicone.feature_parallax_view.parallax_view.rememberParallaxState
import com.marcpicone.feature_parallax_view_sample_app.R
import com.marcpicone.feature_parallax_view_sample_app.theme.FeatureParallaxViewSampleAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FeatureParallaxViewSampleAppTheme {
                Surface {
                    Content()
                }
            }
        }
    }
}

@Composable
private fun Content() {
    val parallaxResources: List<Int> = listOf(
        R.drawable.gorilla_background,
        R.drawable.gorilla_abim_shadow,
        R.drawable.gorilla_abim,
        R.drawable.gorilla_gorilla_shadow,
        R.drawable.gorilla_gorilla
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ParallaxViewExample(parallaxResources)
        ParallaxModifierExample(parallaxResources)
    }
}

@Composable
private fun ColumnScope.ParallaxModifierExample(parallaxResources: List<Int>) {
    val modifierState = rememberParallaxState()
    Box(
        modifier = Modifier
            .weight(1f)
            .parallaxGesture(modifierState),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Modifier usage",
            modifier = Modifier
                .parallaxLayer(modifierState, zFactor = 1f)
                .align(Alignment.Center),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        parallaxResources.forEachIndexed { index, resourceId ->
            val zFactor = ((index + 1) / parallaxResources.size.toFloat()).coerceAtMost(0.8f) // to be able to have text on top z position
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .parallaxLayer(modifierState, zFactor = zFactor)
                    .aspectRatio(1f)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun ColumnScope.ParallaxViewExample(
    parallaxResources: List<Int>
) {
    val parallaxViewState = rememberParallaxState(
        maxDragXDp = 100.dp,
        maxDragYDp = 30.dp,
        maxRotationAngleDegree = 50f,
        smoothingX = 0.8f,
        smoothingY = 0.9f,
        releaseAnimationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessHigh
        )
    )
    ParallaxView(
        state = parallaxViewState,
        modifier = Modifier.weight(1f)
    ) {
        parallaxResources.forEach { resourceId ->
            layer {
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = aspectRatio(1f)
                )
            }
        }
        layer {
            Text(
                text = "DSL usage",
                modifier = align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF101010
)
@Composable
private fun ContentPreview() {
    FeatureParallaxViewSampleAppTheme {
        Content()
    }
}

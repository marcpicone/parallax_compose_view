package com.marcpicone.feature_parallax_view_sample_app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val theme_light_primary = Color(0xFFFCBDC6)
private val theme_light_primary_container = Color(0xFFD16536)
private val theme_light_onPrimary = Color(0xFF000000)
private val theme_light_secondary = Color(0xFF1E1D1D)
private val theme_light_onSecondary = Color(0xFFFFFFFF)
private val theme_light_error = Color(0XFF9F2619)
private val theme_light_onError = Color(0xFFFFFFFF)
private val theme_light_background = Color(0xFF101010)
private val theme_light_onBackground = Color(0xFFFFFFFF)
private val theme_light_surface = Color(0xFF000000)
private val theme_light_onSurface = Color(0xFFABABAC)

private val LightColors = lightColorScheme(
    primary = theme_light_primary,
    primaryContainer = theme_light_primary_container,
    onPrimary = theme_light_onPrimary,
    secondary = theme_light_secondary,
    onSecondary = theme_light_onSecondary,
    error = theme_light_error,
    onError = theme_light_onError,
    background = theme_light_background,
    onBackground = theme_light_onBackground,
    surface = theme_light_surface,
    onSurface = theme_light_onSurface
)

@Composable
fun FeatureParallaxViewSampleAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}

# Feature Parallax View

A lightweight Android Jetpack Compose library providing a customizable parallax effect.

This library is distributed as source code. To use it, simply clone or download the repository and include it in your project.

---

## 1. Include as Module

In your app’s `settings.gradle` (or `settings.gradle.kts`):
```groovy
include ":feature_parallax_view"
```

In your app module’s `build.gradle.kts` dependencies:
```kotlin
dependencies {
    implementation(project(":feature_parallax_view"))
}
```

---

## 2. Enable Compose

Make sure Compose is enabled in your **library** and **application** modules:

```kotlin
android {
  buildFeatures {
    compose = true
  }
}
```

---

## 3. Sample Usage

### DSL-based API

```kotlin
@Composable
fun MyParallaxScreen() {
    ParallaxView(
        state = rememberParallaxState(),
        modifier = Modifier.size(200.dp)
    ) {
        // Layers are rendered in the order they are added: first is back, last is front
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
```

### Modifier-based API

```kotlin
@Composable
fun MyParallaxBoxes() {
  val state = rememberParallaxState()

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
```

---

## 4. Configuration

Use `rememberParallaxState(...)` to customize:
```kotlin
val state = rememberParallaxState(
  maxDragXDp = 80.dp,
  maxDragYDp = 8.dp,
  maxRotationAngleDegree = 20f,
  smoothingX = 0.5f,
  smoothingY = 0.95f,
  releaseAnimationSpec = spring(
      dampingRatio = Spring.DampingRatioHighBouncy,
      stiffness = Spring.StiffnessHigh
  )
)
```

---

## License

This library is released under the MIT License © Marc Picone.


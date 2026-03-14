package com.rifsxd.ksunext.ui.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PRIMARY,
    secondary = PRIMARY_DARK,
    tertiary = SECONDARY_DARK
)

private val LightColorScheme = lightColorScheme(
    primary = PRIMARY,
    secondary = PRIMARY_LIGHT,
    tertiary = SECONDARY_LIGHT
)

val LocalLiquidGlassMode = staticCompositionLocalOf { false }

fun Color.blend(other: Color, ratio: Float): Color {
    val inverse = 1f - ratio
    return Color(
        red = red * inverse + other.red * ratio,
        green = green * inverse + other.green * ratio,
        blue = blue * inverse + other.blue * ratio,
        alpha = alpha
    )
}

@Composable
fun KernelSUTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    amoledMode: Boolean = false,
    liquidGlassMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseColorScheme = when {
        amoledMode && darkTheme && dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val dynamicScheme = dynamicDarkColorScheme(context)
            dynamicScheme.copy(
                background = AMOLED_BLACK,
                surface = AMOLED_BLACK,
                surfaceVariant = dynamicScheme.surfaceVariant.blend(AMOLED_BLACK, 0.6f),
                surfaceContainer = dynamicScheme.surfaceContainer.blend(AMOLED_BLACK, 0.6f),
                surfaceContainerLow = dynamicScheme.surfaceContainerLow.blend(AMOLED_BLACK, 0.6f),
                surfaceContainerLowest = dynamicScheme.surfaceContainerLowest.blend(AMOLED_BLACK, 0.6f),
                surfaceContainerHigh = dynamicScheme.surfaceContainerHigh.blend(AMOLED_BLACK, 0.6f),
                surfaceContainerHighest = dynamicScheme.surfaceContainerHighest.blend(AMOLED_BLACK, 0.6f)
            )
        }
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        amoledMode && darkTheme -> {
            DarkColorScheme.copy(
                background = AMOLED_BLACK,
                surface = AMOLED_BLACK,
                surfaceVariant = DARK_GREY.blend(AMOLED_BLACK, 0.8f),
                surfaceContainer = DARK_GREY.blend(AMOLED_BLACK, 0.8f),
                surfaceContainerLow = DARK_GREY.blend(AMOLED_BLACK, 0.8f),
                surfaceContainerLowest = DARK_GREY.blend(AMOLED_BLACK, 0.8f),
                surfaceContainerHigh = DARK_GREY.blend(AMOLED_BLACK, 0.8f),
                surfaceContainerHighest = DARK_GREY.blend(AMOLED_BLACK, 0.8f),
            )
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val animationSpec = tween<Float>(durationMillis = 600, easing = LinearOutSlowInEasing)
    
    val glassAlpha by animateFloatAsState(
        targetValue = if (liquidGlassMode) 0.35f else 1.0f,
        animationSpec = animationSpec,
        label = "glassAlpha"
    )
    
    val containerAlpha by animateFloatAsState(
        targetValue = if (liquidGlassMode) 0.45f else 1.0f,
        animationSpec = animationSpec,
        label = "containerAlpha"
    )

    val backgroundAlpha by animateFloatAsState(
        targetValue = if (liquidGlassMode) 0.0f else 1.0f,
        animationSpec = animationSpec,
        label = "backgroundAlpha"
    )

    val colorScheme = baseColorScheme.copy(
        surface = baseColorScheme.surface.copy(alpha = glassAlpha),
        surfaceVariant = baseColorScheme.surfaceVariant.copy(alpha = containerAlpha),
        surfaceContainer = baseColorScheme.surfaceContainer.copy(alpha = glassAlpha),
        surfaceContainerLow = baseColorScheme.surfaceContainerLow.copy(alpha = glassAlpha),
        surfaceContainerLowest = baseColorScheme.surfaceContainerLowest.copy(alpha = glassAlpha),
        surfaceContainerHigh = baseColorScheme.surfaceContainerHigh.copy(alpha = containerAlpha),
        surfaceContainerHighest = baseColorScheme.surfaceContainerHighest.copy(alpha = containerAlpha),
        background = baseColorScheme.background.copy(alpha = backgroundAlpha),
        surfaceTint = Color.Transparent
    )

    SystemBarStyle(darkMode = darkTheme)

    CompositionLocalProvider(LocalLiquidGlassMode provides liquidGlassMode) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography
        ) {
            Box(modifier = Modifier.fillMaxSize().background(baseColorScheme.background)) {
                val effectAlpha by animateFloatAsState(
                    targetValue = if (liquidGlassMode) 1f else 0f,
                    animationSpec = animationSpec,
                    label = "effectAlpha"
                )

                if (effectAlpha > 0f) {
                    val infiniteTransition = rememberInfiniteTransition(label = "liquid")
                    val color1 by infiniteTransition.animateColor(
                        initialValue = baseColorScheme.primary.copy(alpha = 0.25f),
                        targetValue = baseColorScheme.tertiary.copy(alpha = 0.25f),
                        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
                        label = "c1"
                    )
                    val color2 by infiniteTransition.animateColor(
                        initialValue = baseColorScheme.secondary.copy(alpha = 0.2f),
                        targetValue = baseColorScheme.primary.copy(alpha = 0.2f),
                        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Reverse),
                        label = "c2"
                    )

                    Box(modifier = Modifier.fillMaxSize()
                        .graphicsLayer(alpha = effectAlpha)
                        .background(Brush.linearGradient(listOf(color1, color2, color1)))
                    )
                    Box(modifier = Modifier.fillMaxSize()
                        .graphicsLayer(alpha = effectAlpha)
                        .background(Brush.radialGradient(listOf(color2.copy(alpha = 0.35f), Color.Transparent)))
                    )
                }
                content()
            }
        }
    }
}

@Composable
private fun SystemBarStyle(
    darkMode: Boolean,
    statusBarScrim: Color = Color.Transparent,
    navigationBarScrim: Color = Color.Transparent,
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    if (activity != null) {
        SideEffect {
            activity.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    statusBarScrim.toArgb(),
                    statusBarScrim.toArgb(),
                ) { darkMode },
                navigationBarStyle = when {
                    darkMode -> SystemBarStyle.dark(
                        navigationBarScrim.toArgb()
                    )

                    else -> SystemBarStyle.light(
                        navigationBarScrim.toArgb(),
                        navigationBarScrim.toArgb(),
                    )
                }
            )
        }
    }
}
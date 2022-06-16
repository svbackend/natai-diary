package com.svbackend.natai.android.ui

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.compose.ui.graphics.Color
import com.svbackend.natai.android.viewmodel.NoteViewModel

enum class UserTheme {
    Dynamic, Default, Pink;

    companion object {
        fun strToTheme(str: String): UserTheme {
            return when (str) {
                "Dynamic" -> Dynamic
                "Default" -> Default
                "Pink" -> Pink
                else -> Default
            }
        }
    }
}

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightPinkColorScheme = darkColorScheme(
    primary = GramotnyiRozovyi40,
    primaryContainer = Lavanda100,
    onPrimaryContainer = Color(0xFFfff1ff),
    secondary = GramotnyiRozovyiPink40,
    tertiary = GramotnyiRozovyiGrey40,
    onPrimary = Color(0xFFffebee),
    background = GramotnyiRozovyi40,
    surface = Color(0xFFFFFBFE),
    surfaceVariant = Color(0xFFFFFBFE),
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    onSurfaceVariant = Color(0x651C1B1F),
    inverseOnSurface = Color(0xFFFFFFFF),
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun NataiTheme(
    vm: NoteViewModel,
    userTheme: UserTheme,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val themeName = vm.currentTheme.collectAsState(initial = UserTheme.Default).value

    val colorScheme = when {
        themeName == UserTheme.Default -> if (darkTheme) DarkColorScheme else LightColorScheme
        themeName == UserTheme.Pink -> LightPinkColorScheme
        themeName == UserTheme.Dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
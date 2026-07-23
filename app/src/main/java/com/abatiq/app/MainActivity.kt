package com.abatiq.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.abatiq.app.ui.navigation.AbatiQNavGraph
import com.abatiq.app.ui.theme.AbatiQTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AbatiQTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AbatiQAppRoot()
                }
            }
        }
    }
}

@Composable
private fun AbatiQAppRoot() {
    AbatiQNavGraph(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    )
}

/**
 * Helper that flips the host activity into landscape while the caller is in the
 * composition, and restores the previous orientation on disposal. Used by
 * [com.abatiq.app.ui.game.GameScreen] so the playfield fills the wide screen
 * without locking other destinations (Home, Settings, Result) to landscape.
 */
@Composable
fun LandscapeOnlyEffect() {
    val context = LocalContext.current
    DisposableEffect(context) {
        val activity = context.findActivity()
        val previous = activity?.requestedOrientation
        if (activity != null) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
        onDispose {
            if (activity != null) {
                activity.requestedOrientation =
                    previous ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }
}

private fun android.content.Context.findActivity(): ComponentActivity? {
    var current = this
    while (current is android.content.ContextWrapper) {
        if (current is ComponentActivity) return current
        current = current.baseContext
    }
    return null
}

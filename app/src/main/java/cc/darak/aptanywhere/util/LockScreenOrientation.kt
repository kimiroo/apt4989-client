package cc.darak.aptanywhere.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current

    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}

        // Save the original orientation to restore it later
        val originalOrientation = activity.requestedOrientation

        // Lock the orientation
        activity.requestedOrientation = orientation

        onDispose {
            // Restore back to original or unspecified when leaving this screen
            activity.requestedOrientation = originalOrientation
        }
    }
}

// Helper function to find the Activity from Context
private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
package cc.darak.aptanywhere.ui.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import cc.darak.aptanywhere.ui.theme.AppTheme
import cc.darak.aptanywhere.viewmodel.PermissionViewModel
import kotlin.getValue

class SettingsActivity : ComponentActivity() {

    private val viewModel: PermissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                SettingsScreen(onBack = { finish() })
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.checkAllPermissions(this)
    }
}
package cc.darak.aptanywhere.ui.init

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import cc.darak.aptanywhere.ui.main.MainActivity
import cc.darak.aptanywhere.ui.theme.AppTheme
import cc.darak.aptanywhere.util.PreferencesHelper.saveApiSettings
import cc.darak.aptanywhere.viewmodel.PermissionViewModel

class InitActivity : ComponentActivity() {

    private val viewModel: PermissionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme() {
                SetupScreen(
                    onComplete = { url, key ->
                        saveApiSettings(this, url, key)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onBack = { finish() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.checkAllPermissions(this)
    }
}
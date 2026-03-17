package cc.darak.aptanywhere.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cc.darak.aptanywhere.service.PhoneMonitorService
import cc.darak.aptanywhere.ui.init.InitActivity
import cc.darak.aptanywhere.ui.search.lookup.LookupActivity
import cc.darak.aptanywhere.ui.settings.SettingsActivity
import cc.darak.aptanywhere.ui.theme.AppTheme
import cc.darak.aptanywhere.util.PreferencesHelper.isSetupComplete

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isSetupComplete(this)) {
            startActivity(Intent(this, InitActivity::class.java))
            finish()
        } else {
            val intent = Intent(this, PhoneMonitorService::class.java)
            startForegroundService(intent)
        }

        setContent {
            AppTheme() {
                MainScreen(
                    onNavigateToSearch = { type ->
                        val intent = Intent(this, LookupActivity::class.java).apply {
                            putExtra("SEARCH_TYPE", type)
                        }
                        startActivity(intent)
                    },
                    onNavigateToSettings = {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                )
            }
        }
    }
}
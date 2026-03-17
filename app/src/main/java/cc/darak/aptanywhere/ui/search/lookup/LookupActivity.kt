package cc.darak.aptanywhere.ui.search.lookup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cc.darak.aptanywhere.data.model.SearchType
import cc.darak.aptanywhere.ui.theme.AppTheme

class LookupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Get SearchType from intent
        val searchType =
            intent.getSerializableExtra("SEARCH_TYPE", SearchType::class.java)
                ?: SearchType.PHONE // Fallback to PHONE if null

        setContent {
            AppTheme {
                // 2. Pass the dynamic searchType to your screen
                LookupScreen(
                    searchType = searchType,
                    onBack = { finish() }
                )
            }
        }
    }
}
package cc.darak.aptanywhere.ui.search.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cc.darak.aptanywhere.data.model.AssetInfo
import cc.darak.aptanywhere.data.model.IntentExtraKeys
import cc.darak.aptanywhere.ui.theme.AppTheme

class AssetDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get parcelable data safely
        val assetInfo = intent.getParcelableExtra(IntentExtraKeys.ASSET_INFO, AssetInfo::class.java)

        setContent {
            AppTheme {
                if (assetInfo != null) {
                    AssetDetailScreen(
                        info = assetInfo,
                        onBack = { finish() }
                    )
                } else {
                    // Handle error or finish if info is null
                    finish()
                }
            }
        }
    }
}
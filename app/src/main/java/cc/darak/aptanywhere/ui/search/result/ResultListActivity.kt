package cc.darak.aptanywhere.ui.search.result

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cc.darak.aptanywhere.data.model.AssetInfo
import cc.darak.aptanywhere.data.model.IntentExtraKeys
import cc.darak.aptanywhere.ui.theme.AppTheme

class ResultListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the list from intent
        val rawList = intent.getParcelableArrayListExtra(
            IntentExtraKeys.ASSET_LIST,
            AssetInfo::class.java
        )
        val assetList: List<AssetInfo> = rawList ?: emptyList()

        setContent {
            AppTheme {
                ResultListScreen(
                    assetList = assetList,
                    onBack = { finish() }
                )
            }
        }
    }
}
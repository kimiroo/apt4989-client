package cc.darak.aptanywhere.ui.search.lookup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import cc.darak.aptanywhere.data.model.SearchType
import cc.darak.aptanywhere.ui.theme.AppTheme
import androidx.compose.runtime.getValue
import cc.darak.aptanywhere.data.model.AssetInfo
import cc.darak.aptanywhere.data.model.IntentExtraKeys
import cc.darak.aptanywhere.ui.search.result.ResultListActivity
import cc.darak.aptanywhere.viewmodel.LookupViewModel

class LookupActivity : ComponentActivity() {

    private val viewModel: LookupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Safely get SearchType from intent
        val searchType =
            intent.getSerializableExtra(IntentExtraKeys.SEARCH_TYPE, SearchType::class.java)
                ?: SearchType.PHONE

        setContent {
            // Observe search results from ViewModel
            val results by viewModel.searchResults

            // 3. Navigate when results are available
            LaunchedEffect(results) {
                results?.let { list ->
                    if (list.isNotEmpty()) {
                        moveToResultList(list)
                        viewModel.consumeResults() // Clear state after navigation
                    }
                }
            }

            AppTheme {
                LookupScreen(
                    viewModel = viewModel,
                    searchType = searchType,
                    onBack = { finish() }
                )
            }
        }
    }

    private fun moveToResultList(results: List<AssetInfo>) {
        // Note: Make sure AssetInfo implements Parcelable
        val intent = Intent(this, ResultListActivity::class.java).apply {
            putParcelableArrayListExtra(IntentExtraKeys.ASSET_LIST, ArrayList(results))
        }
        startActivity(intent)
    }
}
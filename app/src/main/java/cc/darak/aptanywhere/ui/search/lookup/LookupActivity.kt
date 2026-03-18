package cc.darak.aptanywhere.ui.search.lookup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import cc.darak.aptanywhere.data.model.SearchType
import cc.darak.aptanywhere.ui.theme.AppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.AssetInfo
import cc.darak.aptanywhere.data.model.IntentExtraKeys
import cc.darak.aptanywhere.ui.search.result.ResultListActivity
import cc.darak.aptanywhere.viewmodel.LookupViewModel

@OptIn(ExperimentalMaterial3Api::class)
class LookupActivity : ComponentActivity() {
    private val viewModel: LookupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Safely get SearchType from intent
        val searchType =
            intent.getSerializableExtra(IntentExtraKeys.SEARCH_TYPE, SearchType::class.java)
                ?: SearchType.PHONE

        setContent {
            // Observe search results from ViewModel
            val results by viewModel.searchResults

            var showNoResultDialog by remember { mutableStateOf(false) }

            // 3. Navigate when results are available
            LaunchedEffect(results) {
                results?.let { list ->
                    if (list.isNotEmpty()) {
                        moveToResultList(list)
                        viewModel.consumeResults() // Clear state after navigation
                    } else {
                        showNoResultDialog = true
                    }
                }
            }

            AppTheme {
                // Show dialog if there's no result
                if (showNoResultDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showNoResultDialog = false
                            viewModel.consumeResults() // Empty result to prevent dialog spam
                        },
                        title = { Text(stringResource(R.string.dialog_title_no_result)) },
                        text = { Text(stringResource(R.string.dialog_message_no_result)) },
                        confirmButton = {
                            TextButton(onClick = {
                                showNoResultDialog = false
                                viewModel.consumeResults()
                            }) {
                                Text(stringResource(R.string.btn_ok))
                            }
                        }
                    )
                }

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
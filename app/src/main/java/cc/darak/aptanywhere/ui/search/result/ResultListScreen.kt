package cc.darak.aptanywhere.ui.search.result

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.AssetInfo
import cc.darak.aptanywhere.data.model.IntentExtraKeys
import cc.darak.aptanywhere.ui.components.CommonLayout
import cc.darak.aptanywhere.ui.components.search.result.AssetItem
import cc.darak.aptanywhere.ui.search.detail.AssetDetailActivity

@Composable
fun ResultListScreen(
    assetList: List<AssetInfo>,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    CommonLayout(
        title = stringResource(R.string.title_search_result),
        showBack = true,
        applySidePadding = true,
        onBackClick = onBack
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(assetList) { index, info ->
                AssetItem(
                    info = info,
                    isLastItem = index == assetList.size - 1,
                    onClick = {
                        val intent = Intent(context, AssetDetailActivity::class.java).apply {
                            putExtra(IntentExtraKeys.ASSET_INFO, info)
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}
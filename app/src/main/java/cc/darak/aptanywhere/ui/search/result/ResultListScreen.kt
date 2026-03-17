package cc.darak.aptanywhere.ui.search.result

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.AssetInfo
import cc.darak.aptanywhere.ui.components.CommonLayout
import cc.darak.aptanywhere.ui.components.search.result.AssetItem

@Composable
fun ResultListScreen(
    assetList: List<AssetInfo>,
    onBack: () -> Unit
) {
    CommonLayout(
        title = stringResource(R.string.title_search_result),
        showBack = true,
        applySidePadding = true,
        onBackClick = onBack
    ) {
        // The 'Surface' inside CommonLayout already provides the white arched background
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            // Remove outer padding to let the list touch the edges of the arch if desired,
            // or keep small padding for breathing room.
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(assetList) { index, info ->
                AssetItem(
                    info = info,
                    isLastItem = index == assetList.size - 1,
                    onClick = { /* Handle Click */ }
                )
            }
        }
    }
}
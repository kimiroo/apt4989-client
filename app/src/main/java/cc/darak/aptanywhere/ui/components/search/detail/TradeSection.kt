package cc.darak.aptanywhere.ui.components.search.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.AssetInfo

@Composable
fun TradeSection(info: AssetInfo) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PriceCard(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.label_sale),
            state = info.saleState,
            price = info.salePrice
        )
        PriceCard(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.label_jeonse),
            state = info.jeonseState,
            price = info.jeonsePrice
        )
    }
}
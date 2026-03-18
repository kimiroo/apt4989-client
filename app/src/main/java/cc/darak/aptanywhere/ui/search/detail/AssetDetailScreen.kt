package cc.darak.aptanywhere.ui.search.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.AssetInfo
import cc.darak.aptanywhere.ui.components.CommonLayout
import cc.darak.aptanywhere.ui.components.search.detail.AdditionalInfoSection
import cc.darak.aptanywhere.ui.components.search.detail.ContactSection
import cc.darak.aptanywhere.ui.components.search.detail.HeaderSection
import cc.darak.aptanywhere.ui.components.search.detail.RentDetailSection
import cc.darak.aptanywhere.ui.components.search.detail.TradeSection

@Composable
fun AssetDetailScreen(
    info: AssetInfo,
    onBack: () -> Unit
) {
    val title = stringResource(
        R.string.asset_detail_title,
        info.complex, info.unit, info.bld
    )

    CommonLayout(
        title = title,
        showBack = true,
        applySidePadding = true,
        onBackClick = onBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            HeaderSection(info)
            ContactSection(info)
            TradeSection(info)
            RentDetailSection(info)
            AdditionalInfoSection(info)

            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}
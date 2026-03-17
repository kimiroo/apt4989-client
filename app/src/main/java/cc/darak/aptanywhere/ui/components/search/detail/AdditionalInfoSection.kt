package cc.darak.aptanywhere.ui.components.search.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.AssetInfo

@Composable
fun AdditionalInfoSection(info: AssetInfo) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        InfoBlock(
            label = stringResource(R.string.label_expiration_date),
            content = info.expirationDate
        )
        InfoBlock(
            label = stringResource(R.string.label_features),
            content = info.features
        )
        InfoBlock(
            label = stringResource(R.string.label_remarks),
            content = info.remarks,
            isLog = true
        )
        InfoBlock(
            label = stringResource(R.string.label_consult_log),
            content = info.consultLog,
            isLog = true
        )
    }
}
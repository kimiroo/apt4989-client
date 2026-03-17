package cc.darak.aptanywhere.ui.components.search.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.AssetInfo

@Composable
fun ContactSection(info: AssetInfo) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ContactRow(
                label = stringResource(R.string.label_owner),
                name = info.ownerName,
                phone = info.ownerNumber
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            ContactRow(
                label = stringResource(R.string.label_tenant),
                name = info.tenantName,
                phone = info.tenantNumber
            )
        }
    }
}
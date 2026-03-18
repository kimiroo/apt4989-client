package cc.darak.aptanywhere.ui.components.search.result

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.AssetInfo

@Composable
fun AssetItem(
    info: AssetInfo,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top // Ensures all columns start from the top
        ) {
            // Left Side: Building Info (Stacked)
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = info.complex,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(
                        R.string.search_result_row_title,
                        info.bld, info.unit
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${info.area} / ${info.type}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Right Side: People Info (Top & Left Aligned)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Owner Info
                PersonInfoColumn(
                    label = stringResource(R.string.label_owner),
                    name = info.ownerName ?: stringResource(R.string.label_no_data),
                    modifier = Modifier.weight(1f)
                )

                // Tenant Info
                PersonInfoColumn(
                    label = stringResource(R.string.label_tenant),
                    name = info.tenantName ?: stringResource(R.string.label_no_data),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
    }
}
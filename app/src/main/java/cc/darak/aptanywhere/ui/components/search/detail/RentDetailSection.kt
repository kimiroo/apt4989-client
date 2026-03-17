package cc.darak.aptanywhere.ui.components.search.detail

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.AssetInfo

@Composable
fun RentDetailSection(info: AssetInfo) {
    val deposits = info.rentDeposits?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
    val prices = info.rentPrice?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.label_rent),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            info.rentState?.let {
                Text(
                    text = " ($it)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (deposits.isEmpty() && prices.isEmpty()) {
            Text(stringResource(R.string.label_no_data), style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        } else {
            Row(modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp)) {
                Text(
                    "#",
                    modifier = Modifier.weight(0.5f),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    stringResource(R.string.label_rent_price),
                    modifier = Modifier.weight(1.5f),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    stringResource(R.string.label_rent_deposit),
                    modifier = Modifier.weight(1.5f),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            val rowCount = maxOf(deposits.size, prices.size)
            for (i in 0 until rowCount) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)) {
                    Text("${i + 1}", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.bodyMedium)
                    Text(prices.getOrElse(i) { "-" }, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodyMedium)
                    Text(deposits.getOrElse(i) { "-" }, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}
package cc.darak.aptanywhere.ui.components.search.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import cc.darak.aptanywhere.R

@Composable
fun ContactRow(label: String, name: String?, phone: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            // Show gray "(정보 없음)" if name is null
            Text(
                text = name ?: stringResource(R.string.label_no_data),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (name == null) Color.Gray else Color.Unspecified
            )
        }
        Text(
            text = phone ?: stringResource(R.string.label_no_data),
            style = MaterialTheme.typography.bodyLarge,
            color = if (phone == null) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
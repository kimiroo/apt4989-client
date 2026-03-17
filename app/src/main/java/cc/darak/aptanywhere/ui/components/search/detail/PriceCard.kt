package cc.darak.aptanywhere.ui.components.search.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.darak.aptanywhere.R

@Composable
fun PriceCard(modifier: Modifier, label: String, state: String?, price: String?) {
    val displayParts = listOfNotNull(state, price).filter { it.isNotBlank() }
    val displayText = displayParts.joinToString(" ").ifBlank { null }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = displayText ?: stringResource(R.string.label_no_data),
                style = MaterialTheme.typography.titleLarge,
                color = if (displayText == null) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = if (displayText == null) 16.sp else 20.sp,
                fontWeight = if (displayText == null) FontWeight.Normal else FontWeight.Bold,
                lineHeight = 24.sp
            )
        }
    }
}
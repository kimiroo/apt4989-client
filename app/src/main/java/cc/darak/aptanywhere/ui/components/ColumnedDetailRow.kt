package cc.darak.aptanywhere.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ColumnedDetailRow(
    label1: String, value1: String,
    label2: String, value2: String,
    label3: String? = null, value3: String? = null,
    label4: String? = null, value4: String? = null
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp) // Gap between two columns
    ) {
        val isQuad = label3 != null && value3 != null && label4 != null && value4 != null

        val weight1 = if (isQuad) 2f else 1f
        val weight2 = if (isQuad) 3f else 1f

        DetailItem(label = label1, value = value1, modifier = Modifier.weight(weight1))
        DetailItem(label = label2, value = value2, modifier = Modifier.weight(weight2))

        if (isQuad) {
            DetailItem(label = label3, value = value3, modifier = Modifier.weight(5f))
            DetailItem(label = label4, value = value4, modifier = Modifier.weight(5f))
        }
    }
}

/**
 * Sub-component to reduce code duplication
 */
@Composable
private fun DetailItem(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            //maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
package cc.darak.aptanywhere.ui.components.search.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.darak.aptanywhere.R

@Composable
fun InfoBlock(label: String, content: String?, isLog: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            color = if (isLog) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color.Transparent,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = content ?: stringResource(R.string.label_no_data),
                modifier = Modifier.padding(if (isLog) 12.dp else 0.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (content == null) Color.Gray else Color.Unspecified,
                lineHeight = 20.sp
            )
        }
    }
}
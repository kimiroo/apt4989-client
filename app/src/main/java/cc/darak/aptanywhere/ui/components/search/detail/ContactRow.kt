package cc.darak.aptanywhere.ui.components.search.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.util.toPhoneAnnotatedString

@Composable
fun ContactRow(label: String, name: String?, phone: String?) {

    // Dialog states
    var showDialog by remember { mutableStateOf(false) }
    var selectedPhone by remember { mutableStateOf("") }

    val noDataLabel = stringResource(R.string.label_no_data)
    val annotatedPhone = remember(phone, noDataLabel) {
        phone?.toPhoneAnnotatedString { number ->
            selectedPhone = number
            showDialog = true
        } ?: AnnotatedString(noDataLabel)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            // Show gray "(정보 없음)" if name is null
            Text(
                text = name ?: stringResource(R.string.label_no_data),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (name == null) Color.Gray else Color.Unspecified
            )
        }
        Text(
            text = annotatedPhone,
            style = MaterialTheme.typography.bodyLarge,
            color = if (phone == null) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showDialog) {
        ContactActionDialog(
            phoneNumber = selectedPhone,
            onDismiss = { showDialog = false }
        )
    }
}

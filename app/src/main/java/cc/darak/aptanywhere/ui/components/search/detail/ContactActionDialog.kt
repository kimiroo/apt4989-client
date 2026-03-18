package cc.darak.aptanywhere.ui.components.search.detail

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.util.formatPhoneNumber

@Composable
fun ContactActionDialog(
    phoneNumber: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = formatPhoneNumber(phoneNumber),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = { Text(text = stringResource(R.string.dialog_contact_number)) },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDismiss) {
                    Text(stringResource(R.string.btn_close))
                }

                Button(onClick = {
                    val intent = Intent(
                        Intent.ACTION_SENDTO,
                        "smsto:$phoneNumber".toUri()
                    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                    context.startActivity(intent)
                    onDismiss()
                }) {
                    Text(stringResource(R.string.btn_text_message))
                }

                Button(onClick = {
                    val intent = Intent(
                        Intent.ACTION_DIAL,
                        "tel:$phoneNumber".toUri()
                    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                    context.startActivity(intent)
                    onDismiss()
                }) {
                    Text(stringResource(R.string.btn_call))
                }
            }
        }
    )
}
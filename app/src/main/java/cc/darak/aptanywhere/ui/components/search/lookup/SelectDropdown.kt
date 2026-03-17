package cc.darak.aptanywhere.ui.components.search.lookup

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cc.darak.aptanywhere.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDropdown(
    optionList: List<String>,
    selectedOption: String?, // String? to handle null (all/none)
    label: String,
    isRequired: Boolean,
    onOptionSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            // Display logic: null -> placeholder/empty string
            value = selectedOption ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(if (isRequired) {
                "$label (${stringResource(R.string.label_required)})"
            } else {
                "$label (${stringResource(R.string.label_optional)})"
            }) },
            placeholder = { Text(stringResource(R.string.label_not_selected)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // "All" option only if not required
            if (!isRequired) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.label_not_selected)) },
                    onClick = {
                        onOptionSelected(null) // Pass null explicitly
                        expanded = false
                    }
                )
            }

            optionList.forEach { complexName ->
                DropdownMenuItem(
                    text = { Text(complexName.ifEmpty { stringResource(R.string.label_blank) }) },
                    onClick = {
                        onOptionSelected(complexName) // Pass the actual string (even if "")
                        expanded = false
                    }
                )
            }
        }
    }
}
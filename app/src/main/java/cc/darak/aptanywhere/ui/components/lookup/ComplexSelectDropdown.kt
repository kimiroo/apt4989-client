package cc.darak.aptanywhere.ui.components.lookup

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
fun ComplexSelectDropdown(
    complexList: List<String>,
    selectedComplex: String?, // String? to handle null (all/none)
    isRequired: Boolean,
    onComplexSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            // Display logic: null -> placeholder/empty string
            value = selectedComplex ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(if (isRequired) {
                stringResource(R.string.label_complex_required)
            } else {
                stringResource(R.string.label_complex_optional)
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
                        onComplexSelected(null) // Pass null explicitly
                        expanded = false
                    }
                )
            }

            complexList.forEach { complexName ->
                DropdownMenuItem(
                    text = { Text(complexName.ifEmpty { stringResource(R.string.label_blank) }) },
                    onClick = {
                        onComplexSelected(complexName) // Pass the actual string (even if "")
                        expanded = false
                    }
                )
            }
        }
    }
}
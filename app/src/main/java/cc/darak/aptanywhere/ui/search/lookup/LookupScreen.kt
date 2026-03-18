package cc.darak.aptanywhere.ui.search.lookup

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.SearchType
import cc.darak.aptanywhere.ui.components.CommonLayout
import cc.darak.aptanywhere.ui.components.search.lookup.SelectDropdown
import cc.darak.aptanywhere.ui.components.search.lookup.StatusOverlay
import cc.darak.aptanywhere.viewmodel.LookupViewModel

@Composable
fun LookupScreen(
    viewModel: LookupViewModel = viewModel(),
    searchType: SearchType,
    onBack: () -> Unit
) {
    // Fetched complex & building list
    val complexes = viewModel.complexList
    val buildings = viewModel.buildingList

    // State for common fields
    var selectedComplex: String? by remember { mutableStateOf(null) }
    var selectedBld: String? by remember { mutableStateOf(null) }
    var unit by remember { mutableStateOf("") }

    // State for specific fields
    var phoneNumber by remember { mutableStateOf("") }
    var keyword by remember { mutableStateOf("") }

    CommonLayout(
        title = when (searchType) {
            SearchType.PHONE -> stringResource(R.string.title_lookup_phone)
            SearchType.KEYWORD -> stringResource(R.string.title_lookup_keyword)
            SearchType.UNIT -> stringResource(R.string.title_lookup_unit)
        },
        showBack = true,
        onBackClick = onBack,
        isScrollable = false,
        applySidePadding = true
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background) // Use colorScheme instead of colors
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
                    .graphicsLayer {
                        if (viewModel.isLoading || viewModel.errorMessage?.isNotBlank() == true) {
                            renderEffect = android.graphics.RenderEffect.createBlurEffect(
                                20f, 20f, android.graphics.Shader.TileMode.DECAL
                            ).asComposeRenderEffect()
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                when (searchType) {
                    SearchType.PHONE -> {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text(stringResource(R.string.label_phone_required)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                        SelectDropdown(
                            optionList = complexes,
                            selectedOption = selectedComplex,
                            label = stringResource(R.string.label_complex),
                            isRequired = false,
                            onOptionSelected = { selectedComplex = it }
                        )
                    }
                    SearchType.KEYWORD -> {
                        OutlinedTextField(
                            value = keyword,
                            onValueChange = { keyword = it },
                            label = { Text(stringResource(R.string.label_keyword_required)) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.label_keyword_placeholder)) }
                        )
                        SelectDropdown(
                            optionList = complexes,
                            selectedOption = selectedComplex,
                            label = stringResource(R.string.label_complex),
                            isRequired = false,
                            onOptionSelected = {
                                selectedComplex = it
                                selectedBld = null
                                viewModel.onComplexChanged(it)
                            }
                        )
                        SelectDropdown(
                            optionList = buildings,
                            selectedOption = selectedBld,
                            label = stringResource(R.string.label_bld),
                            isRequired = false,
                            onOptionSelected = { selectedBld = it }
                        )
                    }
                    SearchType.UNIT -> {
                        SelectDropdown(
                            optionList = complexes,
                            selectedOption = selectedComplex,
                            label = stringResource(R.string.label_complex),
                            isRequired = true,
                            onOptionSelected = {
                                selectedComplex = it
                                selectedBld = null
                                viewModel.onComplexChanged(it)
                            }
                        )
                        SelectDropdown(
                            optionList = buildings,
                            selectedOption = selectedBld,
                            label = stringResource(R.string.label_bld),
                            isRequired = true,
                            onOptionSelected = { selectedBld = it }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = unit,
                                onValueChange = { unit = it },
                                label = { Text(stringResource(R.string.label_unit_optional)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 3. Search Button
                Button(
                    onClick = {
                        // Pass all current UI states to ViewModel
                        viewModel.performSearch(
                            type = searchType,
                            phone = phoneNumber,
                            keyword = keyword,
                            complex = selectedComplex,
                            bld = selectedBld,
                            unit = unit
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = viewModel.isInputValid(
                        type = searchType,
                        phone = phoneNumber,
                        keyword = keyword,
                        complex = selectedComplex,
                        bld = selectedBld ?: ""
                    )
                ) {
                    Text(stringResource(R.string.btn_lookup), style = MaterialTheme.typography.titleMedium)
                }
            }

            // Loading overlay
            StatusOverlay(
                isLoading = viewModel.isLoading,
                errorMessage = viewModel.errorMessage,
                onDismiss = onBack // Finish activity when clicked
            )
        }
    }
}
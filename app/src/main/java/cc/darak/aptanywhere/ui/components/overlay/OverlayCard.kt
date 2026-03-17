package cc.darak.aptanywhere.ui.components.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.UiState
import cc.darak.aptanywhere.ui.components.ColumnedDetailRow
import cc.darak.aptanywhere.ui.components.DetailRow
import cc.darak.aptanywhere.util.AssetUtils.isOwner
import cc.darak.aptanywhere.util.formatPhoneNumber


@Composable
fun OverlayCard(
    maxHeightDp: Int,
    state: UiState,
    onDismiss: () -> Unit
) {
    OverlayCardTemplate(
        maxHeightDp = maxHeightDp,
        onDismiss = onDismiss
    ) {
        when (state) {
            is UiState.Loading -> {
                Text(
                    stringResource(id = R.string.overlay_loading_data),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            is UiState.NotFound -> {
                Text(
                    stringResource(id = R.string.overlay_no_result),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            is UiState.Error -> {
                Text(
                    stringResource(id = R.string.error_occurred, state.message),
                    color = Color.Red
                )
            }
            is UiState.Success -> {
                val formattedNumber = formatPhoneNumber(state.number)

                Column {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            // 1. Top-only rounded corners for the header background
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            // Horizontal padding is kept minimal to maximize space
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        // 2. Align items to the start (Left) instead of SpaceBetween
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = formattedNumber,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Minimal spacing between number and count
                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.overlay_search_result, state.infoList.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                        )
                    }

                    state.infoList.forEachIndexed { index, info ->
                        ExpandableRowTemplate(
                            title = stringResource(
                                R.string.overlay_search_result_row_title,
                                info.complex, info.bld, info.unit
                            ),
                            label = if (isOwner(info, state.number)) {
                                stringResource(R.string.label_owner)
                            } else {
                                stringResource(R.string.label_tenant)
                            },
                            isExpanded = (index == 0 && state.infoList.size == 1), // 첫 번째 항목만 자동 펼침
                            content = {
                                ColumnedDetailRow(
                                    stringResource(R.string.label_area), info.area,
                                    stringResource(R.string.label_type), info.type,
                                    stringResource(R.string.label_owner_name), info.ownerName,
                                    stringResource(R.string.label_tenant_name), info.tenantName
                                )
                                DetailRow(stringResource(R.string.label_remarks), info.remarks)
                                DetailRow(stringResource(R.string.label_consult_log), info.consultLog)
                            }
                        )

                        if (index < state.infoList.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = Color.LightGray.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}
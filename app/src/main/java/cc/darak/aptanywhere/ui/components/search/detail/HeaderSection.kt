package cc.darak.aptanywhere.ui.components.search.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.AssetInfo

@Composable
fun HeaderSection(info: AssetInfo) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = info.complex,
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = stringResource(
                R.string.asset_detail_bld_unit,
                info.bld, info.unit
            ),
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(8.dp)
        ) {
            val areaText = info.area ?: stringResource(R.string.label_no_data)
            val typeText = info.type ?: stringResource(R.string.label_no_data)
            Text(
                text = "$areaText / $typeText",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
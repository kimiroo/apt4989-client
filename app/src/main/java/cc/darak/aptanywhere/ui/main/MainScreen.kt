package cc.darak.aptanywhere.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cc.darak.aptanywhere.data.model.SearchType
import cc.darak.aptanywhere.ui.components.CommonLayout
import cc.darak.aptanywhere.ui.components.main.MenuCard

@Composable
fun MainScreen(
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    CommonLayout(
        title = "AssetLink",
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // Use colorScheme instead of colors
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "APT4989",
                style = MaterialTheme.typography.headlineMedium, // Changed from h4
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    MenuCard(
                        title = "전화번호 검색",
                        icon = Icons.Default.Phone,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSearch(SearchType.PHONE) }
                    )
                    MenuCard(
                        title = "키워드 검색",
                        icon = Icons.Default.Search,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSearch(SearchType.KEYWORD) }
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    MenuCard(
                        title = "동/호수 검색",
                        icon = Icons.Default.Home,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSearch(SearchType.UNIT) }
                    )
                    MenuCard(
                        title = "설정",
                        icon = Icons.Default.Settings,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSettings
                    )
                }
            }
        }
    }
}
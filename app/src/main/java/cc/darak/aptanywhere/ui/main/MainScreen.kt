package cc.darak.aptanywhere.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.SearchType
import cc.darak.aptanywhere.ui.components.CommonLayout
import cc.darak.aptanywhere.ui.components.main.MenuCard

@Composable
fun MainScreen(
    onNavigateToSearch: (SearchType) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Use colorScheme instead of colors
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column {
            Image(
                painter = painterResource(id = R.drawable.cat), // 자를 이미지 리소스
                contentDescription = "동그란 이미지",
                // 주석: 핵심 변경 사항 - 이미지를 동그랗게 자릅니다.
                modifier = Modifier
                    .size(150.dp) // 이미지 크기 설정
                    .clip(CircleShape), // 원형으로 자르기
                contentScale = ContentScale.Crop // 이미지가 비율에 맞게 꽉 차도록 설정 (필수!)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                MenuCard(
                    title = stringResource(R.string.title_lookup_phone),
                    icon = Icons.Default.Phone,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSearch(SearchType.PHONE) }
                )
                MenuCard(
                    title = stringResource(R.string.title_lookup_keyword),
                    icon = Icons.Default.Search,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSearch(SearchType.KEYWORD) }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                MenuCard(
                    title = stringResource(R.string.title_lookup_unit),
                    icon = Icons.Default.Home,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateToSearch(SearchType.UNIT) }
                )
                MenuCard(
                    title = stringResource(R.string.title_settings),
                    icon = Icons.Default.Settings,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToSettings
                )
            }
        }
    }
}
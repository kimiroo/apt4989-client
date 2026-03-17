package cc.darak.aptanywhere.ui.init

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.darak.aptanywhere.ui.components.CommonLayout
import cc.darak.aptanywhere.ui.components.PermissionCard
import cc.darak.aptanywhere.viewmodel.PermissionViewModel

@Composable
fun SetupScreen(
    viewModel: PermissionViewModel = viewModel(),
    onComplete: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var apiUrl by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }

    val context = LocalContext.current
    val generalGranted by viewModel.generalGranted.collectAsState()
    val notificationAccessGranted by viewModel.notificationAccessGranted.collectAsState()
    val overlayGranted by viewModel.overlayGranted.collectAsState()
    val batteryGranted by viewModel.batteryGranted.collectAsState()

    val scrollState = rememberScrollState()

    // 1. Validation Logic (Regex)
    val isUrlValid = remember(apiUrl) {
        // Simple URL regex: Starts with http/https
        val urlPattern = "^(https?://)[\\w.-]+(?:\\.[\\w.-]+)+(?::\\d+)?$".toRegex()
        apiUrl.isNotBlank() && urlPattern.matches(apiUrl)
    }

    val isApiKeyValid = remember(apiKey) {
        // Example: API Key must be alphanumeric and 16+ characters
        // Adjust the regex pattern to match your actual requirements
        val keyPattern = "^[a-zA-Z0-9]{16,}$".toRegex()
        apiKey.isNotBlank() && keyPattern.matches(apiKey)
    }

    // 2. Check if all conditions are met
    val allPermissionsGranted = generalGranted && overlayGranted && batteryGranted
    val isSetupComplete = isUrlValid && isApiKeyValid && allPermissionsGranted

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        viewModel.checkGeneralPermissions(context)
    }

    CommonLayout(
        title = "초기 설정",
        onBackClick = onBack
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
                    .padding(bottom = 80.dp), // Space for the fixed button
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Info Section ---
                Text(
                    text = "앱 사용을 위해 API 서버 정보와 필수 권한 허용이 필요합니다.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))


                // --- API Settings Section ---
                Text(
                    text = "API 설정",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = apiUrl,
                    onValueChange = { apiUrl = it },
                    label = { Text("API 서버 주소") },
                    placeholder = { Text("https://example.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = apiUrl.isNotEmpty() && !isUrlValid, // Visual feedback for invalid URL
                    supportingText = {
                        if (apiUrl.isNotEmpty() && !isUrlValid) {
                            Text("유효한 HTTP/HTTPS 주소를 입력하세요.", color = MaterialTheme.colorScheme.error)
                        } else {
                            Text("호스트명까지만 입력하세요.", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API 접근 키") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = apiKey.isNotEmpty() && !isApiKeyValid,
                    supportingText = {
                        if (apiKey.isNotEmpty() && !isApiKeyValid) {
                            Text("API 키는 16자 이상의 영문/숫자여야 합니다.", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))


                // --- Permission Section ---
                Text(
                    text = "필수 권한",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // 1. General Permissions (READ_PHONE_STATE, etc.)
                PermissionCard(
                    title = "일반 권한",
                    description = "알림을 띄우거나 수신된 전화번호를 가져오는데 필요합니다.",
                    isGranted = generalGranted,
                    onClick = { launcher.launch(viewModel.permissionsList) }
                )

                // 2. ChannelID Access Permission
                PermissionCard(
                    title = "알림 접근 권한",
                    description = "문자 등 알림을 감지하여 정보를 조회하기 위해 필요합니다.",
                    isGranted = notificationAccessGranted,
                    onClick = { viewModel.requestNotificationAccessPermission(context) }
                )

                // 3. Overlay Permission
                PermissionCard(
                    title = "오버레이 권한",
                    description = "조회 결과를 오버레이로 띄우는데 필요합니다.",
                    isGranted = overlayGranted,
                    onClick = { viewModel.requestOverlayPermission(context) }
                )

                // 4. Battery Optimization
                PermissionCard(
                    title = "배터리 권한",
                    description = "백그라운드에서 모니터링이 끊기지 않게 합니다.",
                    isGranted = batteryGranted,
                    onClick = { viewModel.requestIgnoreBatteryOptimizations(context) }
                )
            }

            // --- Action Button (Fixed at the bottom) ---
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                // Match the background color of your screen
                color = MaterialTheme.colorScheme.surface,
                // Add a subtle shadow to separate from the scrollable content
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        if (isSetupComplete) {
                            onComplete(apiUrl, apiKey)
                        } else {
                            val message = when {
                                !isUrlValid -> "API 주소를 올바르게 입력해주세요."
                                !isApiKeyValid -> "API 접근 키 형식이 올바르지 않습니다."
                                !allPermissionsGranted -> "모든 필수 권한을 허용해주세요."
                                else -> "설정을 다시 확인해주세요."
                            }
                            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp) // Proper spacing around the button
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "시작하기",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isSetupComplete) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
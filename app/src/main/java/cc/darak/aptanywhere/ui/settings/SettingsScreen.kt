package cc.darak.aptanywhere.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.darak.aptanywhere.App
import cc.darak.aptanywhere.ui.components.CommonLayout
import cc.darak.aptanywhere.ui.components.PermissionCard
import cc.darak.aptanywhere.ui.components.settings.SettingsSection
import cc.darak.aptanywhere.util.PreferencesHelper
import cc.darak.aptanywhere.viewmodel.PermissionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PermissionViewModel = viewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // API Settings
    var apiUrl by remember { mutableStateOf(PreferencesHelper.getApiUrl(App.instance)) }
    var apiKey by remember { mutableStateOf("") }
    val isKeySaved = remember { PreferencesHelper.getApiKey(App.instance).isNotBlank() }

    // Overlay Settings (Initial values from Preferences)
    var showOverlay by remember { mutableStateOf(PreferencesHelper.getShowOverlay(App.instance)) }
    var overlayYOffset by remember { mutableFloatStateOf(PreferencesHelper.getOverlayYOffset(App.instance).toFloat()) }
    var maxOverlayHeight by remember { mutableFloatStateOf(PreferencesHelper.getMaxOverlayMaxHeight(App.instance).toFloat()) }

    // Collect permission states from ViewModel
    val generalGranted by viewModel.generalGranted.collectAsState()
    val notificationAccessGranted by viewModel.notificationAccessGranted.collectAsState()
    val overlayGranted by viewModel.overlayGranted.collectAsState()
    val batteryGranted by viewModel.batteryGranted.collectAsState()

    // Settings
    var showConfirmDialog by remember { mutableStateOf(false) }
    val saveSettings = {
        PreferencesHelper.saveApiSettings(App.instance, apiUrl.trim(), apiKey.trim())
        PreferencesHelper.saveOverlaySettings(App.instance, showOverlay, overlayYOffset.toInt(), maxOverlayHeight.toInt())
        Toast.makeText(context, "설정이 저장되었습니다.", Toast.LENGTH_SHORT).show()
        onBack()
    }

    // Launcher for general permissions (READ_PHONE_STATE, etc.)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        viewModel.checkAllPermissions(context) // Re-check after request
    }

    // Validation Logic (Regex)
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

    // Optional: Refresh permissions when user returns to this screen
    DisposableEffect(Unit) {
        viewModel.checkAllPermissions(context)
        onDispose { }
    }

    CommonLayout(
        title = "설정",
        showBack = true,
        onBackClick = onBack
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // --- Section 1: Server Connection ---
                SettingsSection(title = "API 서버 설정") {
                    OutlinedTextField(
                        value = apiUrl,
                        onValueChange = { apiUrl = it },
                        label = { Text("API 서버 주소") },
                        placeholder = { Text("https://example.com") },
                        supportingText = {
                            if (apiUrl.isNotEmpty() && !isUrlValid) {
                                Text("유효한 HTTP/HTTPS 주소를 입력하세요.", color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("호스트명까지만 입력하세요.", color = MaterialTheme.colorScheme.primary)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("API 접근 키") },
                        placeholder = {
                            Text(if (isKeySaved) "verySECRETapiKEY" else "새로운 API 키 입력")
                        },
                        supportingText = {
                            if (apiKey.isNotEmpty() && !isApiKeyValid) {
                                Text("API 키는 16자 이상의 영문/숫자여야 합니다.", color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("기존 키가 덮어씌워집니다. 변경시에만 입력하세요.", color = MaterialTheme.colorScheme.primary)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null) }
                    )
                }

                // --- Section 2: Overlay Customization ---
                SettingsSection(title = "오버레이 표시 설정") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("전화 및 문자 수신시 오버레이 표시")
                        Switch(
                            checked = showOverlay, // Your boolean state
                            onCheckedChange = {
                                // Update your boolean state or preference
                                showOverlay = it
                            }
                        )
                    }

                    // 1. Display as Int using toInt()
                    Text("시작 위치 (Y축 Offset): ${overlayYOffset.toInt()}dp")
                    Slider(
                        value = overlayYOffset,
                        onValueChange = {
                            // 2. Continuous float value for smooth sliding
                            overlayYOffset = it
                        },
                        valueRange = 0f..1000f
                    )

                    Text("최대 높이: ${maxOverlayHeight.toInt()}dp")
                    Slider(
                        value = maxOverlayHeight,
                        onValueChange = {
                            maxOverlayHeight = it
                        },
                        valueRange = 100f..800f
                    )
                }

                // --- Section 3: Permissions ---
                SettingsSection(title = "권한 관리") {
                    // 1. General Permissions
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
            }

            // --- Fixed Bottom Save Button ---
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        // 2. Check if apiKey is not empty to show dialog
                        if (apiKey.trim().isNotEmpty()) {
                            showConfirmDialog = true
                        } else {
                            saveSettings()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(20.dp).height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("설정 저장", style = MaterialTheme.typography.titleMedium)
                }
            }

            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = { Text("API 키 변경 확인") },
                    text = { Text("API 키가 변경됩니다. 계속하시겠습니까?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showConfirmDialog = false
                                saveSettings() // Execute the save logic
                            }
                        ) {
                            Text("계속")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text("취소")
                        }
                    }
                )
            }
        }
    }
}
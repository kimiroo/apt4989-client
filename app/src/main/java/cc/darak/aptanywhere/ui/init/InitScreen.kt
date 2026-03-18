package cc.darak.aptanywhere.ui.init

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.ui.components.CommonLayout
import cc.darak.aptanywhere.ui.components.PermissionCard
import cc.darak.aptanywhere.ui.components.settings.SettingsSection
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
        title = stringResource(R.string.title_init),
        showBack = false,
        onBackClick = onBack,
        isScrollable = false,
        applySidePadding = true
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // --- Info Section ---
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.init_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 20.sp
                        )
                    }
                }

                // --- API Settings Section ---
                SettingsSection(title = stringResource(R.string.title_api_settings)) {
                    OutlinedTextField(
                        value = apiUrl,
                        onValueChange = { apiUrl = it },
                        label = { Text(stringResource(R.string.label_api_server)) },
                        placeholder = { Text("https://example.com") },
                        supportingText = {
                            if (apiUrl.isNotEmpty() && !isUrlValid) {
                                Text(
                                    stringResource(R.string.help_api_server_enter_valid),
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text(
                                    stringResource(R.string.help_api_server_enter_hostname),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) }
                    )
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text(stringResource(R.string.label_api_token)) },
                        placeholder = {
                            Text("verySECUREapiKEY1234")
                        },
                        supportingText = {
                            if (apiKey.isNotEmpty() && !isApiKeyValid) {
                                Text(
                                    stringResource(R.string.help_api_token_length),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null) }
                    )
                }


                // --- Permission Section ---
                SettingsSection(title = stringResource(R.string.title_permissions), showDivider = false) {
                    // 1. General Permissions
                    PermissionCard(
                        title = stringResource(R.string.label_general_permissions),
                        description = stringResource(R.string.description_general_permissions),
                        isGranted = generalGranted,
                        onClick = { launcher.launch(viewModel.permissionsList) }
                    )

                    // 2. ChannelID Access Permission
                    PermissionCard(
                        title = stringResource(R.string.label_notification_access),
                        description = stringResource(R.string.description_notification_access),
                        isGranted = notificationAccessGranted,
                        onClick = { viewModel.requestNotificationAccessPermission(context) }
                    )

                    // 3. Overlay Permission
                    PermissionCard(
                        title = stringResource(R.string.label_overlay_permission),
                        description = stringResource(R.string.description_overlay_permission),
                        isGranted = overlayGranted,
                        onClick = { viewModel.requestOverlayPermission(context) }
                    )

                    // 4. Battery Optimization
                    PermissionCard(
                        title = stringResource(R.string.label_battery_granted),
                        description = stringResource(R.string.description_battery_granted),
                        isGranted = batteryGranted,
                        onClick = { viewModel.requestIgnoreBatteryOptimizations(context) }
                    )
                }
            }

            // --- Action Button (Fixed at the bottom) ---
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                val buttonColor = if (isSetupComplete) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                Button(
                    onClick = {
                        val trimmedKey = apiKey.trim()

                        if (isSetupComplete) {
                            onComplete(apiUrl, trimmedKey)
                        } else {
                            val message = when {
                                !isUrlValid -> getString(context, R.string.not_valid_url)
                                !isApiKeyValid -> getString(context, R.string.not_valid_key)
                                !allPermissionsGranted -> getString(context, R.string.permission_not_granted)
                                else -> getString(context, R.string.init_general_error)
                            }
                            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(20.dp).height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = buttonColor
                ) {
                    Text(
                        text = stringResource(R.string.btn_start),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}
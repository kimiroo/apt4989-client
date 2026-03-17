package cc.darak.aptanywhere.ui.settings

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewmodel.compose.viewModel
import cc.darak.aptanywhere.App
import cc.darak.aptanywhere.R
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

    // Default overlay settings values
    val defaultShowOverlay = true
    val defaultOverlayY = 500f
    val defaultMaxHeight = 400f

    // Settings
    var showConfirmDialog by remember { mutableStateOf(false) }
    val saveSettings = {
        PreferencesHelper.saveApiSettings(
            App.instance,
            apiUrl.trim(),
            apiKey.trim()
        )
        PreferencesHelper.saveOverlaySettings(
            App.instance,
            showOverlay,
            overlayYOffset.toInt(),
            maxOverlayHeight.toInt()
        )
        Toast.makeText(
            context,
            getString(context, R.string.toast_settings_saved),
            Toast.LENGTH_SHORT
        ).show()

        onBack()
    }

    // Launcher for general permissions (READ_PHONE_STATE, etc.)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
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
        title = stringResource(R.string.title_settings),
        showBack = true,
        onBackClick = onBack,
        applySidePadding = true
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
                            Text(if (isKeySaved) "****************" else "verySECUREapiKEY1234")
                        },
                        supportingText = {
                            if (apiKey.isNotEmpty() && !isApiKeyValid) {
                                Text(stringResource(R.string.help_api_token_length), color = MaterialTheme.colorScheme.error)
                            } else {
                                Text(stringResource(R.string.help_api_token_overwrite), color = MaterialTheme.colorScheme.primary)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null) }
                    )
                }

                // --- Section 2: Overlay Customization ---
                SettingsSection(title = stringResource(R.string.title_overlay_settings)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.label_show_overlay))
                        Switch(
                            checked = showOverlay, // Your boolean state
                            onCheckedChange = {
                                // Update your boolean state or preference
                                showOverlay = it
                            }
                        )
                    }

                    // 1. Display as Int using toInt()
                    Text(stringResource(R.string.label_y_offset, overlayYOffset.toInt()))
                    Slider(
                        value = overlayYOffset,
                        onValueChange = {
                            // 2. Continuous float value for smooth sliding
                            overlayYOffset = it
                        },
                        valueRange = 0f..1000f
                    )

                    Text(stringResource(R.string.label_max_height, maxOverlayHeight.toInt()))
                    Slider(
                        value = maxOverlayHeight,
                        onValueChange = {
                            maxOverlayHeight = it
                        },
                        valueRange = 100f..800f
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Reset Button
                    Button(
                        onClick = {
                            // Reset all states to default values
                            showOverlay = defaultShowOverlay
                            overlayYOffset = defaultOverlayY
                            maxOverlayHeight = defaultMaxHeight
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.btn_reset_overlay_settings))
                    }
                }

                // --- Section 3: Permissions ---
                SettingsSection(title = stringResource(R.string.title_permissions)) {
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

            // --- Fixed Bottom Save Button ---
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        val trimmedKey = apiKey.trim()

                        // 1. Validate URL first
                        if (!isUrlValid) {
                            Toast.makeText(context, R.string.not_valid_url, Toast.LENGTH_SHORT).show()
                        }
                        // 2. If key is provided, validate it and show confirmation
                        else if (trimmedKey.isNotEmpty()) {
                            if (!isApiKeyValid) {
                                // Key is provided but not valid
                                Toast.makeText(context, R.string.not_valid_key, Toast.LENGTH_SHORT).show()
                            } else {
                                // Key is provided and valid
                                showConfirmDialog = true
                            }
                        }
                        // 3. If key is empty (no update), save settings directly
                        else {
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
                    title = { Text(stringResource(R.string.title_api_token_overwrite)) },
                    text = { Text(stringResource(R.string.message_api_token_overwrite)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showConfirmDialog = false
                                saveSettings() // Execute the save logic
                            }
                        ) {
                            Text(stringResource(R.string.btn_continue))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text(stringResource(R.string.btn_cancel))
                        }
                    }
                )
            }
        }
    }
}
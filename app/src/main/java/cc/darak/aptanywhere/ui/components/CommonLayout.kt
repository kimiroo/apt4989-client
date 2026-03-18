package cc.darak.aptanywhere.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonLayout(
    title: String,
    modifier: Modifier = Modifier, // 1. Added Modifier
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    isScrollable: Boolean = true,
    scrollState: ScrollState = rememberScrollState(), // 2. Hoisted ScrollState
    applySidePadding: Boolean = false,
    // 3. Optional: Add a slot for an action button
    content: @Composable () -> Unit
) {
    val outerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val innerColor = MaterialTheme.colorScheme.background

    // Note: Edge-to-edge is best handled in the Activity via enableEdgeToEdge(),
    // but we keep the logic here if you need it scoped to this layout.
    UpdateSystemBars()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = outerColor,
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back" // Ideally use stringResource(R.string.back)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        // Scaffold handles the status bar insets for the topBar automatically
        //contentWindowInsets = WindowInsets.systemBars
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = if (applySidePadding) 8.dp else 0.dp)
                .imePadding()
                .navigationBarsPadding()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(28.dp),
                color = innerColor
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .let { mod ->
                            if (isScrollable) mod.verticalScroll(scrollState)
                            else mod
                        }
                ) {
                    content()
                }
            }
        }
    }
}
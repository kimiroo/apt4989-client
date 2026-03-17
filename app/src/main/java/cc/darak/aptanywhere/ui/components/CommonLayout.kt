package cc.darak.aptanywhere.ui.components

import android.R.attr.bottom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    applySidePadding: Boolean = false,
    content: @Composable () -> Unit // Simplified content lambda
) {

    val outerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val innerColor = MaterialTheme.colorScheme.background

    Scaffold(
        containerColor = outerColor,
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // Let the scaffold color show through
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { innerPadding ->
        // The White Arched Container
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()) // Only apply top padding to position below AppBar
                .padding(horizontal = if (applySidePadding) 8.dp else 0.dp),
            shape = RoundedCornerShape( // Distinctive arch shape
                topStart = 28.dp,
                topEnd = 28.dp,
            ),
            color = innerColor
        ) {
            // Inside the arch, we don't need innerPadding anymore
            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}
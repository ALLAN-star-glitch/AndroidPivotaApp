package com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.PostJobViewModel

/**
 * Orchestrator for the Post Job experience.
 * Switches between a Master-Detail split for Tablets/Web
 * and a single-column flow for Mobile.
 */
@Composable
fun AdaptiveJobPostLayout(
    onBack: () -> Unit,
    viewModel: PostJobViewModel = hiltViewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    // Pivota Professional Surface Color - Using theme background
    val surfaceColor = colorScheme.background

    Scaffold(
        topBar = {
            PostJobTopBar(onBack = onBack)
        },
        containerColor = surfaceColor
    ) { paddingValues ->
        if (isWide) {
            /* ───────── TABLET/DESKTOP: MASTER-DETAIL SPLIT ───────── */
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // LEFT: The Form Editor (White "Paper" Surface)
                Surface(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    color = colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    JobPostFormContent(viewModel = viewModel)
                }

                // RIGHT: Sticky Live Preview (Visual Confirmation Canvas)
                Box(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight()
                        .background(colorScheme.background)
                        .padding(32.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    LivePreviewSurface(viewModel = viewModel)
                }
            }
        } else {
            /* ───────── MOBILE: UNIFIED SCROLL ───────── */
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding() // Keyboard safety
            ) {
                JobPostFormContent(viewModel = viewModel)
            }
        }
    }
}

// ─────────────── DESIGN PREVIEWS ───────────────

@Preview(name = "Post Job - Tablet Split", device = Devices.PIXEL_TABLET, showSystemUi = true)
@Composable
fun PreviewJobPostTablet() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Text(
                "Tablet Preview: Split View Active",
                Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview(name = "Post Job - Mobile", device = Devices.PIXEL_7, showSystemUi = true)
@Composable
fun PreviewJobPostMobile() {
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            Text(
                "Mobile Preview: Single Column Active",
                Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
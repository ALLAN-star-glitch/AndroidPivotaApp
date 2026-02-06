package com.example.pivota.listings.presentation.composables.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.pivota.listings.presentation.viewmodel.PostJobViewModel

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
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWide = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    // Pivota Professional Surface Color (Soft Mint/Grey)
    val surfaceColor = Color(0xFFF6FAF9)

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
                    color = Color.White,
                    tonalElevation = 2.dp
                ) {
                    JobPostFormContent(viewModel = viewModel)
                }

                // RIGHT: Sticky Live Preview (Visual Confirmation Canvas)
                Box(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight()
                        .background(surfaceColor)
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
    // Note: In a real environment, you'd use a MockViewModel or Stateless version
    // to see the actual UI here if Hilt isn't initialized.
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF6FAF9))) {
        Text("Tablet Preview: Split View Active", Modifier.align(Alignment.Center))
    }
}

@Preview(name = "Post Job - Mobile", device = Devices.PIXEL_7, showSystemUi = true)
@Composable
fun PreviewJobPostMobile() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Text("Mobile Preview: Single Column Active", Modifier.align(Alignment.Center))
    }
}
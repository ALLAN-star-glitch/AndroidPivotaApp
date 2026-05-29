package com.example.pivota.dashboard.presentation.screens.client_admin_screens.professional

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.professionals.AdaptiveServicePostLayout
import com.example.pivota.ui.theme.PivotaConnectTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostServiceScreen(
    onBack: () -> Unit
) {
    AdaptiveServicePostLayout(onBack = onBack)
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    name = "Post Service - Mobile",
    showBackground = true,
    backgroundColor = 0xFFF7F9FE,
    heightDp = 800,
    widthDp = 400
)
@Composable
private fun PreviewPostServiceScreenMobile() {
    PivotaConnectTheme(darkTheme = false) {
        PostServiceScreen(onBack = {})
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    name = "Post Service - Tablet",
    device = Devices.TABLET,
    showBackground = true,
    backgroundColor = 0xFFF7F9FE,
    heightDp = 900,
    widthDp = 800
)
@Composable
private fun PreviewPostServiceScreenTablet() {
    PivotaConnectTheme(darkTheme = false) {
        PostServiceScreen(onBack = {})
    }
}
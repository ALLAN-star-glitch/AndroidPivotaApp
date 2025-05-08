import PreferenceContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.window.core.layout.WindowSizeClass
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.background_image_and_overlay.BackgroundImageAndOverlay

@Composable
fun PreferenceAdaptiveLayout(
    header: String="",
    welcomeText: String,
    desc1: String="",
    desc2: String="",
    onNavigateToDashboard: ()->Unit
) {
    Box {
        val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val isMediumScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
        val isExpandedScreen = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

        when {

            //TWO PANE LAYOUT
            isMediumScreen || isExpandedScreen -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Pane: Image
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        BackgroundImageAndOverlay(
                            header = header,
                            isWideScreen = true,
                            offset = 200.dp,
                            showUpgradeButton = true,
                            imageHeight = 300.dp,
                            enableCarousel = true,
                            image = R.drawable.happy_client


                        )
                    }

                    // Right Pane: Form
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {

                        PreferenceContent(
                            topPadding = 24.dp,
                            onNavigateToDashboardScreen = onNavigateToDashboard
                        )

                    }
                }
            }

            else -> {

                //SINGLE PANE
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {

                    BackgroundImageAndOverlay(
                        header = header,
                        isWideScreen = false,
                        offset = 350.dp,
                        showUpgradeButton = false,
                        imageHeight = 600.dp,
                        enableCarousel = true,
                        image = R.drawable.happy_client

                    )


                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(2f)
                    ) {

                        PreferenceContent(
                            topPadding = 450.dp,
                            onNavigateToDashboardScreen = onNavigateToDashboard
                        )


                    }

                }
            }
        }
    }
}

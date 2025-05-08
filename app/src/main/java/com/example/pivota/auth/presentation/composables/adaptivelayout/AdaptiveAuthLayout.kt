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
fun AdaptiveAuthLayout(
    header: String,
    welcomeText: String,
    desc1: String,
    desc2: String,
    isLoginScreen: Boolean,
    onNavigateToRegisterScreen: () -> Unit={},
    onNavigateToLogin: ()-> Unit={},
    onNavigateToDashboard: ()-> Unit={},
    onRegisterSuccess: ()->Unit={}
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
                            isWideScreen = true,
                            welcomeText = welcomeText,
                            desc1 = desc1,
                            desc2 = desc2,
                            offset = 200.dp,
                            showUpgradeButton = true,
                            imageHeight = 300.dp,
                            image = R.drawable.happyman
                        )
                    }

                    // Right Pane: Form
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.White)
                    ) {
                        if (isLoginScreen){

                            LoginFormContent(
                                topPadding = 64.dp,
                                showHeader = true,
                                isWideScreen = true,
                                onNavigateToRegisterScreen = onNavigateToRegisterScreen,
                                onNavigateToDashboardScreen = onNavigateToDashboard
                            )
                            }
                        else {
                            RegistrationFormContent(
                                topPadding = 64.dp,
                                showHeader = true,
                                isWideScreen = true,
                                onRegisterSuccess = onRegisterSuccess,
                                onNavigateToLoginScreen = onNavigateToLogin
                            )
                        }

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
                            isWideScreen = false,
                            header = header,
                            offset = if (isLoginScreen) 350.dp else 140.dp,
                            showUpgradeButton = false,
                            imageHeight = if (isLoginScreen) 600.dp else 300.dp,
                            image = R.drawable.happyman
                        )


                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .zIndex(2f)
                    ) {

                        if (isLoginScreen)

                            LoginFormContent(
                                topPadding = 450.dp,
                                showHeader = true,
                                isWideScreen = false,
                                onNavigateToRegisterScreen = onNavigateToRegisterScreen ,
                                onNavigateToDashboardScreen = onNavigateToDashboard
                            )

                        else
                            RegistrationFormContent(
                                topPadding = 220.dp,
                                showHeader = true,
                                isWideScreen = false,
                                onRegisterSuccess = onRegisterSuccess,
                                onNavigateToLoginScreen = onNavigateToLogin
                            )
                    }

                }
            }
        }
    }
}

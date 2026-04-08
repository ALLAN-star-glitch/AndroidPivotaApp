package com.example.pivota.welcome.presentation.composables.welcome_content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.buttons.PivotaPrimaryButton
import com.example.pivota.ui.theme.InfoBlue


@Composable
fun WelcomeContent(
    topPadding: Dp,
    header: String = "Your Gateway to Life Opportunities",
    welcomeText: String = "Find Jobs, Housing, Services & Support Across Africa",
    onNavigateToContinueSetup: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = topPadding)
            .clip(RoundedCornerShape(topEnd = 80.dp))
            .background(MaterialTheme.colorScheme.background) // Using theme background
            .zIndex(2f),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 32.dp)
        ) {
            /* ───── LOGO ───── */
            AsyncImage(
                model = R.drawable.transparentpivlogo,
                contentDescription = "PivotaConnect Logo",
                modifier = Modifier.size(120.dp)
            )

            /* ───── HEADLINE ───── */
            Text(
                text = header,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 36.sp
                ),
                textAlign = TextAlign.Center
            )

            /* ───── BODY TEXT ───── */
            Text(
                text = welcomeText,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            /* ───── Get Started Button ───── */
            PivotaPrimaryButton(
                text = "Get Started",
                onClick = onNavigateToContinueSetup,
                modifier = Modifier.fillMaxWidth(),
                icon = ImageVector.vectorResource(R.drawable.ic_person)
            )


            /* ───── LOGIN TEXT ───── */
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = "Log in",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = InfoBlue  // Using brand Info Blue
                    ),
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            /* ───── FOOTER LINKS ───── */
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Terms of Service",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.clickable { /* Navigate to Terms */ }
                )
                Text(
                    text = " • ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )
                )
                Text(
                    text = "Privacy Policy",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.clickable { /* Navigate to Privacy */ }
                )
            }
        }
    }
}
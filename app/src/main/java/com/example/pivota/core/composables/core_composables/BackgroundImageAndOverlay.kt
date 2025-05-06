package com.example.pivota.core.composables.core_composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.pivota.R

@Composable
fun BackgroundImageAndOverlay(
    isWideScreen: Boolean,
    welcomeText: String = "",
    desc1: String = "",
    desc2: String = "",
    header: String = "",
    offset: Dp,
    showUpgradeButton: Boolean

    ) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.happy_clients),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = if (isWideScreen) Modifier.fillMaxSize() else Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Box(
            modifier = Modifier
                .offset(y = offset)
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(Color(0xAA008080))
                .zIndex(1f)
        ) {
            if (isWideScreen) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = welcomeText,
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = desc1,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = desc2,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFFFC107)),
                        textAlign = TextAlign.Center
                    )
                    if(showUpgradeButton){
                        PivotaUpgradeButton(modifier = Modifier.padding(top = 15.dp))

                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .width(100.dp)
                            .padding(bottom = 8.dp),
                        thickness = 2.dp,
                        color = Color(0xFFE9C16C)
                    )

                    Text(
                        text = header,
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        }
    }
}

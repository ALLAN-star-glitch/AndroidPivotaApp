package com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.housing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pivota.R


@Composable
fun SimpleHousingImageGallery(
    images: List<Any>,
    modifier: Modifier = Modifier,
    isForSale: Boolean = false
) {
    var selectedImageIndex by remember { mutableIntStateOf(0) }

    if (images.isEmpty()) {
        Image(
            painter = painterResource(R.drawable.property_placeholder4),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        )
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main Image with Overlay
        Box {
            AsyncImage(
                model = images[selectedImageIndex],
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            // Badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (isForSale)
                    Color(0xFF2E7D32)
                else
                    Color(0xFF1565C0)
            ) {
                Text(
                    text = if (isForSale) "FOR SALE" else "FOR RENT",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Counter
            Text(
                text = "${selectedImageIndex + 1}/${images.size}",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White
            )
        }

        // Thumbnails
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(images.size) { index ->
                AsyncImage(
                    model = images[index],
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .scale(if (selectedImageIndex == index) 1.05f else 1f)
                        .border(
                            width = if (selectedImageIndex == index) 3.dp else 1.dp,
                            color = if (selectedImageIndex == index)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.Gray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedImageIndex = index },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
package com.example.pivota.listings.presentation.composables.housing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HousingPreviewCard(
    price: String,
    type: String,
    loc: String,
    imageRes: Int = com.example.pivota.R.drawable.nairobi_city,
    status: String = "For Rent",
    rating: Float = 4.5f,
    amenities: List<String> = listOf("Water", "Security")
) {
    val gold = Color(0xFFE9C16C) // Your brand gold

    Card(
        modifier = Modifier
            .width(280.dp)
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 🏷️ Status Badge with Golden/Teal Touch
                Surface(
                    color = if (status.contains("Sale")) gold else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 12.dp, topEnd = 0.dp, bottomStart = 0.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = status.uppercase(),
                        color = if (status.contains("Sale")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                // 🤖 SmartMatch Verified Icon (Gold Border)
                Surface(
                    color = Color.White,
                    shape = CircleShape,
                    border = BorderStroke(2.dp, gold),
                    modifier = Modifier.padding(12.dp).size(34.dp).align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = gold,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = price,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.Star, null, tint = gold, modifier = Modifier.size(16.dp))
                    Text(text = rating.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Text(text = type, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1C1E))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Text(text = loc, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(start = 2.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    amenities.forEach { amenity ->
                        Surface(
                            color = gold.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.5.dp, gold.copy(0.3f))
                        ) {
                            Text(
                                text = amenity,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
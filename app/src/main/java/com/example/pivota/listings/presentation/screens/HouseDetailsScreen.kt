package com.example.pivota.listings.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.Bathtub
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.Chip
import com.example.pivota.listings.domain.models.HousingPost
import com.example.pivota.core.presentations.composables.TopBar
import java.text.NumberFormat
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun HouseDetailsScreen() {
    val housingPost = HousingPost( // sample data
        images = listOf("test", "test"),
        price = 45000,
        currency = "KES",
        priceRate = "monthly",
        title = "Modern 2-Bedroom Apartment in Westlands",
        description = "Spacious and well-lit apartment located in the heart of westlands. Features a large living area, modern kitchen with fitted " +
                "cabinets, and a master ensuite. The building offers 24/7 security and high-speed lifts. Close to Sarit Center.",
        country = "Kenya",
        city = "Nairobi",
        neighbourhood = "Westlands",
        address = "Pepani Road",
        bedrooms = 2,
        bathrooms = 2,
        furnished = true,
        type = "Apt",
        amenities =  listOf("Water Backup", "Electricity", "Parking", "Fiber Ready", "24/7 Security"),
    )
    Scaffold(
        topBar = {
            TopBar(
                title = "House details",
                onBack = {
                    // TODO navigate back
                },
                icon = Icons.AutoMirrored.Outlined.Help
            )
        },

        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // TODO handle contact button click
                        }
                    ) {
                        Text("Contact")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // TODO handle Book viewing open
                        }
                    ) {
                        Text("Book Viewing", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            AsyncImage(
                model = null,
                contentDescription = null,
                modifier = Modifier
                    .height(120.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = housingPost.currency,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = NumberFormat.getInstance(Locale.US).format(housingPost.price),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "/${housingPost.priceRate}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Text(
                text = housingPost.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${housingPost.neighbourhood} ${housingPost.city}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RoomInfo(
                    icon = Icons.Outlined.Bed,
                    data = housingPost.bedrooms.toString(),
                    category = "Bedrooms",
                    modifier = Modifier.weight(1f)
                )
                RoomInfo(
                    icon = Icons.Outlined.Bathtub,
                    data = housingPost.bathrooms.toString(),
                    category = "Bathrooms",
                    modifier = Modifier.weight(1f)
                )
                RoomInfo(
                    icon = Icons.Outlined.Chair,
                    data = if (housingPost.furnished) "Yes" else "No",
                    category = "Furnished",
                    modifier = Modifier.weight(1f)
                )
                RoomInfo(
                    icon = Icons.Outlined.Home,
                    data = housingPost.type,
                    category = "Type",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "Description",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(housingPost.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Amenities
            Text(
                text = "Amenities",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                housingPost.amenities.forEach { item ->
                    Chip(item)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Location
            Text(
                text = "Location",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${housingPost.address}, ${housingPost.neighbourhood}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "${housingPost.city}, ${housingPost.country}",
                style = MaterialTheme.typography.bodyMedium
            )


            // Owner
            Spacer(modifier = Modifier.height(32.dp))
            Owner(
                name = "Sarah Mwangi",
                imageLink = "imagelink"
            )
        }
    }
}

@Composable
fun Owner(name: String, imageLink: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.happyman),
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Verified,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                        contentDescription = null
                    )
                    Text("Verified Owner", color = MaterialTheme.colorScheme.primary)
                }
            }

            IconButton(
                onClick = {
                    // TODO handle messaging
                },
            ) {
                Icon(
                    Icons.Outlined.ChatBubble,
                    contentDescription = "Message"
                )
            }
        }
    }
}

@Composable
fun RoomInfo(
    icon: ImageVector,
    data: String,
    category: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
        )

        Text(data)

        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
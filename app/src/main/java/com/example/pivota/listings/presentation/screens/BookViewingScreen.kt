package com.example.pivota.listings.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bathtub
import androidx.compose.material.icons.outlined.Bed
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.R
import com.example.pivota.listings.domain.models.HousingPost
import com.example.pivota.core.presentations.composables.TopBar
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun BookViewingScreen() {
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

    var date by remember { mutableStateOf(Date().toString()) }
    var preferredTime by remember { mutableStateOf("Afternoon") }
    var noteToOwner: String? by remember { mutableStateOf(null) }

    // Sample user details
    val name = "Allan Njoroge"
    val phone = "+254712345678"

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    Scaffold(
        topBar = {
            TopBar(
                title = "House details",
                onBack = {
                    // TODO navigate back
                },
                icon = Icons.Outlined.Share
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PROPERTY",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Housing(housingPost)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "WHEN WOULD YOU LIKE TO VISIT",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Preferred Date")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField( // TODO handle get and display date
                        value = date,
                        onValueChange = { date = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null) },
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Preferred Time")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val modifier = Modifier.weight(1f)
                        PreferredTime(modifier, "Morning", "8-11 AM", preferredTime, onSelect = { preferredTime = it })
                        PreferredTime(modifier, "Afternoon", "12-4 PM", preferredTime, onSelect = { preferredTime = it })
                        PreferredTime(modifier, "Evening", "5-7 PM", preferredTime, onSelect = { preferredTime = it })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("The owner will confirm specific availability", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "NOTE TO OWNER (OPTIONAL)",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = if (noteToOwner == null) "" else noteToOwner!!,
                        onValueChange = { noteToOwner = it.ifBlank { null } },
                        label = { Text("Any questions or specific time preference?") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = false,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "YOUR DETAILS",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Name")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = {  },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Outlined.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Phone")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {  },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Shared only after booking is confirmed", style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Action buttons
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    onClick = {
                        // TODO handle Request Viewing
                    }
                ) {
                    Text(
                        text ="Request Viewing",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    onClick = {
                        // TODO handle cancel button click
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PreferredTime(modifier: Modifier, period: String, time: String, preferred: String, onSelect: (String) -> Unit) {
    val selected = preferred == period
    Card(
        modifier = modifier.clickable { onSelect(period) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column (
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = period,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = time,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun Housing(housingPost: HousingPost) {
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
            Image(
                painter = painterResource(id = R.drawable.happyman),
                contentDescription = null,
                modifier = Modifier
                    .height(70.dp)
                    .width(70.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )

            Column {
                Text(
                    text = "${housingPost.currency} ${NumberFormat.getInstance(Locale.US).format(housingPost.price)}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = housingPost.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = housingPost.neighbourhood,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Outlined.Bed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = housingPost.bedrooms.toString(),
                        color = MaterialTheme.colorScheme.outline
                    )

                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Outlined.Bathtub,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = housingPost.bathrooms.toString(),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
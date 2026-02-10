package com.example.pivota.listings.presentation.composables.housing

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pivota.auth.presentation.screens.InterestChip
import com.example.pivota.core.presentations.composables.KmpSpinner
import com.example.pivota.core.presentations.composables.UniversalSegmentedToggle

@Preview(showBackground = true)
@Composable
fun PostHousing() {
    var listingType by remember { mutableStateOf("For Rent") }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("KES") }
    var negotiable by remember {mutableStateOf(false)}
    var furnished by remember {mutableStateOf(false)}
    var bedrooms by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var amenities by remember { mutableStateOf<List<String>>(emptyList())}
    var city by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var address: String? by remember { mutableStateOf(null) }
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var status by remember { mutableStateOf("For Rent") }
    var additionalNotes: String? by remember { mutableStateOf(null) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Core details
        Card (
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ){
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text("Core Details")

                Spacer(Modifier.height(16.dp))

                Text("Listings")
                UniversalSegmentedToggle(
                    options = listOf("For Rent", "For Sale"),
                    selected = listingType,
                    onSelect = { listingType = it },
                )

                Spacer(Modifier.height(16.dp))

                Text("Listing Title")
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("e.g. 3-Bedroom Apartment in Westlands") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Category")
                KmpSpinner(
                    label = "Select category",
                    options = listOf("", ""), // TODO include categories
                    selectedOption = category,
                    onOptionSelected = { category = it }
                )

                Spacer(Modifier.height(16.dp))

                Text("Description")
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Include size, style, unique features, nearby landmarks") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = false,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Price and currency
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column (
                modifier = Modifier.padding(all = 8.dp)
            ){
                Text("Price & Currency")
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Price")
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            label = { Text("KES 0.00") },
                            colors = textFieldColors,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Currency")
                        KmpSpinner(
                            label = "",
                            options = listOf("KES"),
                            selectedOption = currency,
                            onOptionSelected = { currency = it }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Negotiable Price")
                        Text("Can buyers discuss the price?", style = MaterialTheme.typography.bodySmall)
                    }

                    Switch(
                        checked = negotiable,
                        onCheckedChange = { negotiable = it },
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Features and amenities
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text("Features & Amenities")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bedrooms")
                        OutlinedTextField(
                            value = bedrooms,
                            onValueChange = { bedrooms = it },
                            label = { Text("0") },
                            colors = textFieldColors,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bathrooms")
                        OutlinedTextField(
                            value = bathrooms,
                            onValueChange = { bathrooms = it },
                            label = { Text("0") },
                            colors = textFieldColors,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fully furnished")
                        Text("Does it come with furniture?", style = MaterialTheme.typography.bodySmall)
                    }

                    Switch(
                        checked = furnished,
                        onCheckedChange = { furnished = it },
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text("Amenities")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Water", "Electricity", "Parking", "Wi-Fi", "Security", "Balcony", "Swimming Pool", "Gym").forEach { item ->
                        InterestChip(
                            label = item,
                            isSelected = amenities.contains(item),
                            onToggle = {
                                amenities = if (amenities.contains(item))
                                    amenities - item else amenities + item
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Location
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text("Location")
                Spacer(Modifier.height(16.dp))

                Text("City/Town")
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("e.g. Nairobi") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text("Neighborhood/Estate")
                OutlinedTextField(
                    value = neighborhood,
                    onValueChange = { neighborhood = it },
                    label = { Text("e.g. Kilimani") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text("Address (Optional")
                OutlinedTextField(
                    value = address ?: "",
                    onValueChange = { address = it.ifBlank { null } },
                    label = { Text("e.g. Argwings Kodhek Road") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Property images
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text("Property Images")
                Text("Upload up to 10 images. First image is cover.", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(8.dp))

                ImagePicker { uris ->
                    images = uris
                }
                if (images.isNotEmpty()) {
                    SelectedImageGrid(images)
                } else {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No images selected", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text("Status")
                Spacer(modifier = Modifier.height(16.dp))
                KmpSpinner(
                    label = "",
                    options = listOf("For Rent", "For Sale"),
                    selectedOption = status,
                    onOptionSelected = { status = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Additional notes
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text("Additional Noted (Optional)")
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = if (additionalNotes == null) "" else additionalNotes!!,
                    onValueChange = { additionalNotes = it.ifBlank { null } },
                    label = { Text("House rules, special instructions, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = false,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}
package com.example.pivota.listings.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.core.presentations.composables.TopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostServiceScreen(
    onBack: () -> Unit = {},
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopBar(
                icon = Icons.Default.Info,
                title = "Post a Service",
                onBack = onBack
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Section 1: Service Details ---
            SectionCard(title = "Service Details") {
                Label("Service Title")
                CustomTextField(placeholder = "e.g. Plumbing – Pipe Repair & Installation")

                Label("Category")
                CustomDropdown(placeholder = "Select Category")

                Label("Description")
                CustomTextField(
                    placeholder = "Include your skills, specialties, and tools provided",
                    singleLine = false,
                    minLines = 4
                )

                Label("Service Type", optional = true)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CustomCheckbox(checked = true, label = "Jobs (Standard)")
                    Spacer(modifier = Modifier.width(16.dp))
                    CustomCheckbox(checked = false, label = "Social Support")
                }
            }

            // --- Section 2: Pricing ---
            SectionCard(title = "Pricing") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1.5f)) {
                        Label("Base Price")
                        CustomTextField(placeholder = "0.00", prefix = "KES")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Label("Currency")
                        CustomDropdown(placeholder = "KES")
                    }
                }
                Label("Price Unit")
                CustomDropdown(placeholder = "Fixed Price")
            }

            // --- Section 3: Experience & Availability ---
            SectionCard(title = "Experience & Availability") {
                Label("Years of Experience", optional = true)
                CustomTextField(placeholder = "e.g. 5")

                Label("Availability")
                CustomTextField(placeholder = "e.g. Mon-Fri, 8am–5pm")
            }

            // --- Section 4: Location ---
            SectionCard(title = "Location") {
                Label("City / Town")
                CustomTextField(placeholder = "e.g. Nairobi", leadingIcon = Icons.Outlined.Place)

                Label("Neighborhood", optional = true)
                CustomTextField(placeholder = "e.g. Westlands")
            }

            // --- Section 5: Portfolio Images ---
            SectionCard(title = "Portfolio Images") {
                Text(
                    "Show off your work. Max 5 images.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Placeholder for "Main" image
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        // Pattern background simulation
                        Box(modifier = Modifier.fillMaxSize().background(Color.White))

                        // "Main" badge
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                "Main",
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Add Button
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)), // Dashed border simulated
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // --- Section 6: Listing Status ---
            SectionCard(title = "Listing Status") {
                CustomDropdown(placeholder = "Active (Visible)")
            }

            // --- Section 7: Additional Notes ---
            SectionCard(title = "Additional Notes", optional = true) {
                CustomTextField(
                    placeholder = "Tools provided, service duration, or special instructions",
                    singleLine = false,
                    minLines = 3
                )
            }

            // --- Section 8: Live Service Preview ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer), // Light teal bg
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Live Service Preview",
                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // The inner preview card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            // Image placeholder
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                            )

                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Plumbing Services",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        "KES 1,500",
                                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Outlined.Place,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Nairobi", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)))
                                    }
                                    Text("/ visit", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFFEF3C7), // Light yellow
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "5 Yrs Exp",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFD97706)),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- Footer Button ---
            Button(
                onClick = { /* Submit */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Post Service", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                "Tip: Complete required fields to post your service.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- Helper Components ---

@Composable
fun SectionCard(
    title: String,
    optional: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                    )
                )
                if (optional) {
                    Text(
                        " (Optional)",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun Label(text: String, optional: Boolean = false) {
    Row(modifier = Modifier.padding(bottom = 6.dp, top = 4.dp)) {
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
            )
        )
        if (optional) {
            Text(
                " (Optional)",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
fun CustomTextField(
    placeholder: String,
    prefix: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    leadingIcon: ImageVector? = null
) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
        prefix = if (prefix != null) { { Text(prefix, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) } } else null,
        leadingIcon = if (leadingIcon != null) { { Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) } } else null,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = singleLine,
        minLines = minLines
    )
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun CustomDropdown(placeholder: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.outline, // Dropdowns usually don't highlight as text fields
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            trailingIcon = {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            }
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun CustomCheckbox(checked: Boolean, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(if (checked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(4.dp))
                .border(1.dp, if (checked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceContainer, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostServiceScreen() {
    MaterialTheme {
        PostServiceScreen()
    }
}
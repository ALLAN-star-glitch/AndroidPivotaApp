package com.example.pivota.auth.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.auth.presentation.composables.CustomSegmentedToggle

@Preview(showBackground = true)
@Composable
fun InterestsScreen() {
    var selectedInterests by remember { mutableStateOf(setOf("Housing")) }
    var userType by remember { mutableStateOf("Looking for") }
    var selectedFocus by remember { mutableStateOf("Near me") }
    var specificInput by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp) // Consistent section spacing
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your interests", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Skip for now",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { /* Skip logic */ }
                    )
                }
                Text(
                    text = "Tell us what you're interested in. This helps personalize your experience later.",
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = "You can change this anytime.",
                    color = Color.Gray,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            // Interests Section using official FlowRow
            item {
                SectionWrapper("What are you interested in?") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Housing", "Jobs", "Services", "Support", "Service Providers").forEach { item ->
                            InterestChip(
                                label = item,
                                isSelected = selectedInterests.contains(item),
                                onToggle = {
                                    selectedInterests = if (selectedInterests.contains(item))
                                        selectedInterests - item else selectedInterests + item
                                }
                            )
                        }
                    }
                }
            }

            // Segmented Toggle Section
            item {
                SectionWrapper("What best describes you?") {
                    CustomSegmentedToggle(
                        options = listOf("Looking for", "Offering"),
                        selected = userType,
                        onSelect = { userType = it }
                    )
                }
            }

            // Focus Section using official FlowRow
            item {
                SectionWrapper("Where should we focus?") {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("Near me", "My city", "My country", "Remote / Online").forEach { locale ->
                            InterestChip(
                                label = locale,
                                isSelected = selectedFocus == locale,
                                onToggle = { selectedFocus = locale }
                            )
                        }
                    }
                }
            }

            // Input Section
            item {
                SectionWrapper("Anything specific? (Optional)") {
                    OutlinedTextField(
                        value = specificInput,
                        onValueChange = { specificInput = it },
                        placeholder = { Text("e.g. Remote tech jobs", color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    Text(
                        "Use a few words to describe what you have in mind.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Footer Buttons
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { /* Save action */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                    ) {
                        Text("Save preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Text(
                        "Preferences are optional and only used to tailor your experience later.",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomSegmentedToggle(options: List<String>, selected: String, onSelect: () -> Unit) {
    TODO("Not yet implemented")
}

@Composable
fun SectionWrapper(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        content()
    }
}

@Composable
fun InterestChip(label: String, isSelected: Boolean, onToggle: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onToggle() },
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}
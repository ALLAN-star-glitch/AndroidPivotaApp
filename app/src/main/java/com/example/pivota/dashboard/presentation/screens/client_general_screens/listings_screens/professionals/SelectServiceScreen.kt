package com.example.pivota.dashboard.presentation.screens.client_general_screens.listings_screens.professionals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.core.presentations.composables.TopBar
import com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.professionals.BookingStepper

data class ServiceOption(
    val id: Int,
    val title: String,
    val description: String,
    val price: String,
    val unit: String,
    val isMostBooked: Boolean = false
)

@Composable
fun SelectServiceScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val services = listOf(
        ServiceOption(1, "Standard Leak Repair", "Diagnosis and repair of visible pipe leaks. Includes sealant and labor.", "KES 1,500", "visit", true),
        ServiceOption(2, "Full Inspection & Maintenance", "Complete check of water systems, tanks, and pressure. Recommended annually.", "KES 2,500", "session"),
        ServiceOption(3, "Tank Installation Consultation", "Assessment for new tank installation. Fee deducted if job is booked.", "KES 800", "consultation")
    )

    var selectedServiceId by remember { mutableStateOf(1) }

    Scaffold(
        topBar = {
            TopBar(
                title = "Book Service",
                icon = Icons.Default.Info,
                onBack = onBack
            )
        },
        bottomBar = {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Continue", modifier = Modifier.padding(8.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Provider Info Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Joseph Kamau", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(14.dp))
                        Text(" 4.9 ", fontSize = 12.sp, color = Color.Gray)
                        Text(" • Westlands, Nairobi", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.5f))

            BookingStepper(currentStep = 1)

            Text(
                "Select a Service",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(services) { service ->
                    ServiceItem(
                        service = service,
                        isSelected = selectedServiceId == service.id,
                        onClick = { selectedServiceId = service.id }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "You can choose a date and time in the next step.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceItem(
    service: ServiceOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.White)
            .border(
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    service.title,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(service.description, fontSize = 13.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(service.price, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(" / ${service.unit}", fontSize = 12.sp, color = Color.Gray)
            }
        }

        if (service.isMostBooked) {
            Surface(
                modifier = Modifier.align(Alignment.TopEnd),
                color = Color(0xFFEBC170),
                shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 8.dp)
            ) {
                Text(
                    "MOST BOOKED",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

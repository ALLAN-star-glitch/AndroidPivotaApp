package com.example.pivota.dashboard.presentation.bookservice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.core.presentations.composables.TopBar

@Composable
fun ReviewAndPayScreen(
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    var selectedPaymentMethod by remember { mutableStateOf("M-Pesa") }

    Scaffold(
        topBar = {
            TopBar(
                title = "Review & Pay",
                icon = Icons.Default.Info,
                onBack = onBack
            )
        },
        bottomBar = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm & Pay KES 1,800")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            BookingStepper(currentStep = 4)

            SectionTitle("Booking Summary")
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.align(Alignment.Center))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("David K.", fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB400), modifier = Modifier.size(14.dp))
                                Text(" 4.8 ", fontSize = 12.sp, color = Color.Gray)
                                Text("(124 reviews)", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SummaryItem(Icons.Default.Build, "Service", "Plumbing Leak Repair")
                    SummaryItem(Icons.Default.CalendarToday, "Date & Time", "Oct 12, 2023 at 11:00 AM")
                    SummaryItem(Icons.Default.LocationOn, "Location", "Westlands, Nairobi")
                }
            }

            SectionTitle("Pricing Breakdown")
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PriceRow("Service Price", "KES 1,500")
                    PriceRow("Platform Fee", "KES 150")
                    PriceRow("Taxes (10%)", "KES 150")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("KES 1,800", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            SectionTitle("Payment Method")
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMethodItem(
                    title = "M-Pesa",
                    subtitle = "Pay via mobile money",
                    icon = Icons.Default.Smartphone,
                    isSelected = selectedPaymentMethod == "M-Pesa",
                    onClick = { selectedPaymentMethod = "M-Pesa" }
                )
                PaymentMethodItem(
                    title = "Airtel Money",
                    subtitle = "Pay via mobile money",
                    icon = Icons.Default.Smartphone,
                    isSelected = selectedPaymentMethod == "Airtel Money",
                    onClick = { selectedPaymentMethod = "Airtel Money" }
                )
                PaymentMethodItem(
                    title = "Card",
                    subtitle = "Visa or Mastercard",
                    icon = Icons.Default.CreditCard,
                    isSelected = selectedPaymentMethod == "Card",
                    isEnabled = false,
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Your payment is protected", fontSize = 12.sp, color = Color.Gray)
                }
                TextButton(onClick = {}) {
                    Text("Review Cancellation Policy", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SummaryItem(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PriceRow(label: String, price: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(price, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun PaymentMethodItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.White,
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F7FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = if (isEnabled) Color.Gray else Color.LightGray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        title,
                        fontWeight = FontWeight.Bold,
                        color = if (isEnabled) Color.Unspecified else Color.Gray
                    )
                    if (!isEnabled) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Unavailable", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontSize = 8.sp)
                        }
                    }
                }
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            RadioButton(
                selected = isSelected,
                onClick = null,
                enabled = isEnabled
            )
        }
    }
}

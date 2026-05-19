package com.example.pivota.dashboard.presentation.screens.client_general_screens.listings_screens.professionals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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

@Composable
fun ScheduleServiceScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var selectedTime by remember { mutableStateOf("11:00 AM") }

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
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Continue")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BookingStepper(currentStep = 2)

            // Provider/Service Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.align(Alignment.Center))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Joseph Kamau", fontWeight = FontWeight.Bold)
                        Text("Leak Detection & Repair", fontSize = 12.sp, color = Color.Gray)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("KES 1,500", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("/fixed", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }

            Text(
                "Select Date",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Simple Calendar View (Placeholder)
            CalendarPlaceholder()

            Text(
                "Available Times",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                "Thu, Oct 12",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 12.sp,
                color = Color.Gray
            )

            val times = listOf("09:00 AM", "11:00 AM", "01:00 PM", "03:00 PM", "05:00 PM", "07:00 PM")

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(times) { time ->
                    TimeSlotItem(
                        time = time,
                        isSelected = selectedTime == time,
                        onClick = { selectedTime = time }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarPlaceholder() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) { Icon(Icons.Default.ChevronLeft, contentDescription = null) }
                Text("October 2023", fontWeight = FontWeight.Bold)
                IconButton(onClick = {}) { Icon(Icons.Default.ChevronRight, contentDescription = null) }
            }
            
            // Simplified grid for calendar
            Text("Su  Mo  Tu  We  Th  Fr  Sa", modifier = Modifier.fillMaxWidth(), color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            // This is just a visual placeholder matching the design
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color(0xFFFAFAFA))) {
                // Drawing a couple of circles to match the screenshot
                Box(modifier = Modifier.offset(x = 135.dp, y = 40.dp).size(30.dp).clip(CircleShape).border(1.dp, Color(0xFFEBC170), CircleShape), contentAlignment = Alignment.Center) {
                    Text("4", fontSize = 12.sp)
                }
                Box(modifier = Modifier.offset(x = 175.dp, y = 70.dp).size(30.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                    Text("12", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun TimeSlotItem(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
            .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

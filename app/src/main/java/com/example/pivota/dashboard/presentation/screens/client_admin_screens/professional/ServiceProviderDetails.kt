package com.example.pivota.dashboard.presentation.screens.client_admin_screens.professional

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pivota.core.presentations.composables.TopBar

//@Preview(showBackground = true)
@Composable
fun ServiceProviderDetails(
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Provider Details",
                onBack = onBack,
                icon = Icons.Outlined.Info
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                        border = BorderStroke(1.dp, Color(0xFFFFEBEE))
                    ) {
                        Text("Suspend", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006064))
                    ) {
                        Icon(Icons.Outlined.Email, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Message", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(64.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.align(Alignment.Center).size(40.dp),
                                        tint = Color.White
                                    )
                                }
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF00796B),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.BottomEnd)
                                        .background(Color.White, CircleShape)
                                        .padding(2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Joseph Kamau", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("QuickFix Plumbing Solutions", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        "UID: PRV-849201",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        InfoRow(icon = Icons.Outlined.StarOutline, text = "4.8 Rating (124 Reviews)", iconTint = Color(0xFFFBC02D))
                        InfoRow(icon = Icons.Outlined.LocationOn, text = "Westlands, Nairobi")
                        InfoRow(icon = Icons.Outlined.Phone, text = "+254 712 345 678")
                        InfoRow(icon = Icons.Outlined.Email, text = "joseph@quickfix.co.ke")

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Text("Edit Profile", color = Color.DarkGray)
                            }
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Text("Reset Password", color = Color.DarkGray)
                            }
                        }
                    }
                }
            }

            // 2. Services Offered
            item {
                SectionHeader(title = "Services Offered", actionText = "Add")
            }

            item {
                ServiceItem(
                    title = "Leak Repair & Pipe Fixing",
                    description = "Complete diagnosis and fixing of water leaks, burst pipes, and blockages.",
                    price = "KES 1,500 / Fixed",
                    status = "Active",
                    statusColor = Color(0xFFE0F2F1),
                    statusTextColor = Color(0xFF00796B),
                    actions = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", color = Color.DarkGray)
                            }
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFFFEBEE))
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", color = Color(0xFFD32F2F))
                            }
                        }
                    }
                )
            }

            item {
                ServiceItem(
                    title = "Water Heater Installation",
                    description = "Professional installation and maintenance of hot water systems.",
                    price = "KES 3,000 / Session",
                    status = "Active",
                    statusColor = Color(0xFFE0F2F1),
                    statusTextColor = Color(0xFF00796B),
                    actions = {
                         Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", color = Color.DarkGray)
                            }
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFFFEBEE))
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", color = Color(0xFFD32F2F))
                            }
                        }
                    }
                )
            }

            item {
                ServiceItem(
                    title = "Bathroom Remodeling Consult",
                    description = "Assessment and consultation for bathroom upgrades.",
                    price = "KES 1,000 / Hour",
                    status = "Pending",
                    statusColor = Color(0xFFFFF3E0),
                    statusTextColor = Color(0xFFF57C00),
                    actions = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFE0F2F1))
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF00796B))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Approve", color = Color(0xFF00796B))
                            }
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, Color(0xFFFFEBEE))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFD32F2F))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reject", color = Color(0xFFD32F2F))
                            }
                        }
                    }
                )
            }

            // 3. Documents & Certifications
            item {
                Text("Documents & Certifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DocumentItem(
                            title = "National ID",
                            date = "Uploaded 12 Oct 2023",
                            status = "Verified",
                            statusColor = Color(0xFFE0F2F1),
                            statusTextColor = Color(0xFF00796B),
                            icon = Icons.Outlined.AssignmentInd,
                            actionIcon = Icons.Outlined.FileDownload
                        )
                        DocumentItem(
                            title = "Plumbing License",
                            date = "Uploaded 15 Oct 2023",
                            status = "Pending",
                            statusColor = Color(0xFFFFF3E0),
                            statusTextColor = Color(0xFFF57C00),
                            icon = Icons.Outlined.Badge,
                            isApprovalPending = true
                        )
                        DocumentItem(
                            title = "Background Check",
                            date = "Uploaded 20 Oct 2023",
                            status = "Rejected",
                            statusColor = Color(0xFFFFEBEE),
                            statusTextColor = Color(0xFFD32F2F),
                            icon = Icons.Outlined.FactCheck,
                            actionIcon = Icons.Outlined.Refresh
                        )
                    }
                }
            }

            // 4. Reviews & Ratings
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Reviews & Ratings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Surface(
                                color = Color(0xFFF5F5F5),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFBC02D))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("4.8 Avg", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        ReviewItem(name = "Grace M.", time = "2 days ago", comment = "Joseph arrived on time and fixed the leak in under an hour. Very professional and clean work!")
                        Spacer(modifier = Modifier.height(16.dp))
                        ReviewItem(name = "David K.", time = "1 week ago", comment = "Good communication and reasonable pricing. Solved a persistent issue.")
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Text("View All 124 Reviews", color = Color.DarkGray)
                        }
                    }
                }
            }

            // 5. Activity Logs
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Activity Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { }) {
                                Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        LogItem(icon = Icons.AutoMirrored.Outlined.Login, iconBg = Color(0xFFE1F5FE), iconTint = Color(0xFF039BE5), title = "Logged in from Nairobi, KE", time = "Today, 10:45 AM")
                        LogItem(icon = Icons.Outlined.AssignmentTurnedIn, iconBg = Color(0xFFE8F5E9), iconTint = Color(0xFF43A047), title = "Accepted booking #B-109", time = "Yesterday, 3:20 PM")
                        LogItem(icon = Icons.Outlined.EditCalendar, iconBg = Color(0xFFFFF3E0), iconTint = Color(0xFFFB8C00), title = "Updated profile description", time = "Oct 20, 2:15 PM")
                        LogItem(icon = Icons.Outlined.CloudUpload, iconBg = Color(0xFFF3E5F5), iconTint = Color(0xFF8E24AA), title = "Uploaded new document (Plumbing License)", time = "Oct 15, 9:00 AM")

                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Text("View Full Logs", color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String, iconTint: Color = Color.Gray) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = iconTint)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
    }
}

@Composable
fun SectionHeader(title: String, actionText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Surface(
            color = Color(0xFFF5F5F5),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(actionText, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ServiceItem(
    title: String,
    description: String,
    price: String,
    status: String,
    statusColor: Color,
    statusTextColor: Color,
    actions: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(price, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            actions()
        }
    }
}

@Composable
fun DocumentItem(
    title: String,
    date: String,
    status: String,
    statusColor: Color,
    statusTextColor: Color,
    icon: ImageVector,
    actionIcon: ImageVector? = null,
    isApprovalPending: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(40.dp),
            color = Color(0xFFF5F5F5),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.padding(10.dp), tint = Color(0xFF006064))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = statusColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = statusTextColor
                )
            }
        }
        
        if (isApprovalPending) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    color = Color(0xFFE0F2F1),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Approve", modifier = Modifier.padding(6.dp), tint = Color(0xFF00796B))
                }
                Surface(
                    modifier = Modifier.size(32.dp),
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Reject", modifier = Modifier.padding(6.dp), tint = Color(0xFFD32F2F))
                }
            }
        } else if (actionIcon != null) {
            IconButton(onClick = { }) {
                Icon(actionIcon, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
}

@Composable
fun ReviewItem(name: String, time: String, comment: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            repeat(5) {
                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFFBC02D))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(comment, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OutlinedButton(
                onClick = { },
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                border = BorderStroke(1.dp, Color(0xFFFFEBEE))
            ) {
                Icon(Icons.Outlined.Flag, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Remove", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LogItem(icon: ImageVector, iconBg: Color, iconTint: Color, title: String, time: String) {
    Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(36.dp),
            color = iconBg,
            shape = CircleShape
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.padding(8.dp), tint = iconTint)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
            Text(time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

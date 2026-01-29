package com.example.pivota.subscribe.presentations.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.subscribe.models.Plan

@Preview(showBackground = true)
@Composable
fun ChoosePlanScreen() {
    var selectedPlan by remember { mutableStateOf("Gold") }
    var selectedCycle by remember { mutableStateOf("1 Year") }

    val plans = listOf(
        Plan("Free Forever", listOf("Browse & follow listings", "Save opportunities")),
        Plan("Bronze", listOf("Limited listings & services", "Entry-level visibility")),
        Plan("Silver", listOf("More listings & services", "Enhanced visibility")),
        Plan("Gold", listOf("Maximum listings", "Priority visibility", "Full platform access"), true)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface, // Light mint background from image
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = { /* Handle Continue */ },
            ) {
                Text("Continue")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Choose Your Plan",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Start free or unlock more features with a paid plan. Upgrade anytime.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                )
            }

            items(plans) { plan ->
                PlanCard(
                    plan = plan,
                    isSelected = selectedPlan == plan.name,
                    onSelect = { selectedPlan = plan.name }
                )
            }

            item {
                BillingCycleSection(
                    selectedCycle = selectedCycle,
                    onCycleSelected = { selectedCycle = it }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PlanCard(plan: Plan, isSelected: Boolean, onSelect: () -> Unit) {
    val borderColor = if (isSelected) Color(0xFFE6B800) else Color.Transparent // Gold border

    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (plan.name == "Free Forever") MaterialTheme.colorScheme.surfaceContainerHigh
            else MaterialTheme.colorScheme.surfaceContainer
        ),
        border = if (isSelected) BorderStroke(2.dp, borderColor) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = plan.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if (plan.isBestValue) {
                        Surface(
                            color = Color(0xFFFFD700).copy(alpha = 0.6f),
                            shape = CircleShape,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "BEST VALUE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                plan.features.forEach { feature ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (plan.name == "Gold") Icons.Outlined.CheckCircle else Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = feature,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Custom Radio Icon
            RadioButton(
                selected = isSelected,
                onClick = {}
            )
        }
    }
}

@Composable
fun BillingCycleSection(selectedCycle: String, onCycleSelected: (String) -> Unit) {
    val cycles = listOf("1 Month", "4 Months", "6 Months", "1 Year")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Select Billing Cycle", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cycles.forEach { cycle ->
                val isSelected = selectedCycle == cycle
                Button(
                    onClick = { onCycleSelected(cycle) },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = if (!isSelected) BorderStroke(1.dp, Color.LightGray) else null
                ) {
                    Text(cycle, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "✨ Longer plans offer better value.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
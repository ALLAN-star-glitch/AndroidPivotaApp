package com.example.pivota.dashboard.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OpportunitiesSection(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Active Opportunities",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            val opportunities = listOf(
                OpportunityItem("Jobs", 24),
                OpportunityItem("Houses", 12),
                OpportunityItem("Services", 18)
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                itemsIndexed(opportunities) { index, item ->
                    OpportunityCard(item = item, index = index)
                }
            }
        }
    }
}

data class OpportunityItem(val label: String, val count: Int)

private val cardGradients = listOf(
    listOf(Color(0xFF008080), Color(0xFF4FD1C5)), // Teal to Light Teal
    listOf(Color(0xFF3F51B5), Color(0xFF5C6BC0)), // Indigo to Light Indigo
    listOf(Color(0xFFFFA000), Color(0xFFFFD54F))  // Amber to Light Amber
)

@Composable
fun OpportunityCard(item: OpportunityItem, index: Int, onClick: () -> Unit = {}) {
    val gradient = cardGradients[index % cardGradients.size]

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp)
            .height(70.dp), // Reduced height
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradient))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
            )
            Text(
                text = "${item.count}+",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
            )
        }
    }
}

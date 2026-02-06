package com.example.pivota.listings.presentation.composables.jobs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.pivota.listings.domain.models.PayRate
import com.example.pivota.listings.presentation.viewmodel.PostJobViewModel

@Composable
fun CompensationSection(viewModel: PostJobViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF006565),
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = Color.Transparent
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Pay & Negotiation",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 💰 Amount Input
        OutlinedTextField(
            value = if (uiState.payAmount == 0.0) "" else uiState.payAmount.toString(),
            onValueChange = { viewModel.updatePay(it) },
            label = { Text("Pay Amount (KES)") },
            placeholder = { Text("0.00") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            prefix = { Text("KES ", fontWeight = FontWeight.Bold) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📅 Pay Rate Selector (Pills)
        Text(
            text = "Pay Frequency",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PayRate.entries.forEach { rate ->
                val isSelected = uiState.payRate == rate
                PayRatePill(
                    label = rate.name.lowercase().replaceFirstChar { it.uppercase() }.replace("_", " "),
                    isSelected = isSelected,
                    onClick = { /* viewModel.updatePayRate(rate) */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🤝 Negotiable Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF6FAF9))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Negotiable",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Allow workers to propose a different rate",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Switch(
                checked = uiState.isNegotiable,
                onCheckedChange = { viewModel.toggleNegotiable(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF006565))
            )
        }
    }
}

@Composable
private fun PayRatePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFF006565) else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0xFF006565) else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color.Gray
        )
    }
}
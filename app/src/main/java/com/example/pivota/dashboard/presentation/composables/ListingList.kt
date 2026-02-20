package com.example.pivota.dashboard.presentation.composables


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.pivota.dashboard.presentation.model.ListingUiModel

@Composable
fun ListingsList(
    listings: List<ListingUiModel>,
    onListingClick: (ListingUiModel) -> Unit,
    onLongPress: (ListingUiModel) -> Unit
) {
    LazyColumn {
        items(listings, key = { it.id }) { listing ->
            ListingCard(
                listing = listing,
                onClick = { onListingClick(listing) },
                onLongPress = { onLongPress(listing) }
            )
        }
    }
}

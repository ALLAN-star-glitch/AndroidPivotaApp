package com.example.pivota.dashboard.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.pivota.dashboard.presentation.composables.ExploreAppBar
import com.example.pivota.dashboard.presentation.composables.OpportunitiesSection

@Composable
fun Explore() {
      val scrollState = rememberScrollState()

      Box {
            Column(
                  modifier = Modifier
                        .verticalScroll(scrollState)
            ) {
                  ExploreAppBar()

                  // Add spacing to make room for the overlapping section
                  Spacer(modifier = Modifier.height(32.dp))

                  // Opportunities Section overlaps up
                  OpportunitiesSection(
                        modifier = Modifier
                              .padding(horizontal = 16.dp)
                              .offset(y = (-60).dp)
                              .zIndex(1f)
                  )

                  // More sections can follow here...
                  Spacer(modifier = Modifier.height(16.dp)) // just for visual breathing room
            }
      }
}

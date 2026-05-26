package com.example.pivota.dashboard.presentation.screens.client_general_screens.listings_screens.professionals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.pivota.dashboard.domain.model.listings_models.general.Category
import com.example.pivota.dashboard.presentation.composables.client_general_composables.listings_composables.categories.getIconForService
import com.example.pivota.dashboard.presentation.state.SubcategoriesUiState
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.SubcategoriesViewModel

/**
 * Screen that displays subcategories for a selected main category
 * Uses circular icons matching the All Services screen design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubcategoriesScreen(
    parentCategoryId: String,
    parentCategoryName: String,
    vertical: String,
    onSubcategoryClick: (String, String, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val viewModel: SubcategoriesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Load subcategories when screen opens
    LaunchedEffect(parentCategoryId) {
        viewModel.loadSubcategories(parentCategoryId)
    }

    // Get window size for responsive layout
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val windowWidthClass = windowSizeClass.windowWidthSizeClass
    val isExpanded = windowWidthClass == WindowWidthSizeClass.EXPANDED
    val isMedium = windowWidthClass == WindowWidthSizeClass.MEDIUM
    val isTablet = isExpanded || isMedium

    // Grid columns matching All Services screen
    val gridColumns = when {
        isExpanded -> 6
        isMedium -> 4
        else -> 3
    }

    val horizontalPadding = when {
        isExpanded -> 32.dp
        isMedium -> 24.dp
        else -> 16.dp
    }

    val gridSpacing = when {
        isExpanded -> 20.dp
        isMedium -> 16.dp
        else -> 12.dp
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = parentCategoryName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Subcategories",
                            fontSize = 12.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is SubcategoriesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading subcategories...",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            is SubcategoriesUiState.Success -> {
                val subcategories = state.subcategories

                if (subcategories.isEmpty()) {
                    LaunchedEffect(Unit) {
                        onSubcategoryClick(parentCategoryId, parentCategoryName, vertical)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(gridColumns),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(
                            start = horizontalPadding,
                            end = horizontalPadding,
                            top = 16.dp,
                            bottom = 100.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
                        verticalArrangement = Arrangement.spacedBy(gridSpacing)
                    ) {
                        items(subcategories, key = { it.id }) { subcategory ->
                            SubcategoryCircleItem(
                                subcategory = subcategory,
                                parentCategoryName = parentCategoryName,
                                colorScheme = colorScheme,
                                onClick = {
                                    onSubcategoryClick(
                                        subcategory.id,
                                        subcategory.name,
                                        subcategory.vertical ?: vertical
                                    )
                                }
                            )
                        }
                    }
                }
            }

            is SubcategoriesUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "❌ ${state.message}",
                            color = colorScheme.error,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = { viewModel.loadSubcategories(parentCategoryId) }) {
                            Text("Retry")
                        }
                        TextButton(onClick = {
                            onSubcategoryClick(parentCategoryId, parentCategoryName, vertical)
                        }) {
                            Text("View all $parentCategoryName services instead")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Circle item component for displaying a single subcategory
 * Matches the exact design of the All Services screen grid items
 */
@Composable
fun SubcategoryCircleItem(
    subcategory: Category,
    parentCategoryName: String,
    colorScheme: ColorScheme,
    onClick: () -> Unit
) {
    val pillarColor = when (subcategory.vertical) {
        "HOUSING" -> colorScheme.primary
        "JOBS" -> colorScheme.secondary
        "SOCIAL_SUPPORT" -> colorScheme.tertiary
        else -> colorScheme.primary
    }

    val icon = getIconForService(subcategory.name, subcategory.vertical ?: "")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circular Icon (same as All Services screen)
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(pillarColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = subcategory.name,
                tint = pillarColor,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subcategory Name
        Text(
            text = subcategory.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        // Parent category indicator (light text)
        Text(
            text = parentCategoryName,
            fontSize = 9.sp,
            fontWeight = FontWeight.Normal,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
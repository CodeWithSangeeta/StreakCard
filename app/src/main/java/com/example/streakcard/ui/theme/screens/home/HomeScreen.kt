package com.example.streakcard.ui.theme.screens.home


package com.streakcard.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.streakcard.domain.model.Goal
import com.streakcard.ui.components.GoalCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onAddGoal: () -> Unit,
    onGoalClick: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("StreakCard 🔥", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Keep your goals alive", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                },
                actions = {
                    IconButton(onClick = { /* stats screen */ }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Stats")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddGoal,
                icon = { Icon(Icons.Default.Add, "Add Goal") },
                text = { Text("New Goal") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.goals.isEmpty() -> EmptyState(onAddGoal)
                else -> GoalList(
                    goals = uiState.goals,
                    onCheckIn = viewModel::checkIn,
                    onGoalClick = onGoalClick,
                    onDelete = viewModel::deleteGoal,
                    onTogglePin = viewModel::togglePin,
                    onToggleWidget = viewModel::toggleWidget
                )
            }

            // Error snackbar
            uiState.errorMessage?.let { msg ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = {
                        TextButton(onClick = viewModel::dismissError) { Text("Dismiss") }
                    }
                ) { Text(msg) }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GoalList(
    goals: List<Goal>,
    onCheckIn: (Long) -> Unit,
    onGoalClick: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onTogglePin: (Goal) -> Unit,
    onToggleWidget: (Goal) -> Unit
) {
    val pinned = goals.filter { it.isPinned }
    val others  = goals.filter { !it.isPinned }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(160.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp
    ) {
        if (pinned.isNotEmpty()) {
            item(span = StaggeredGridItemSpan.FullLine) {
                SectionHeader(title = "📌 Pinned", subtitle = "Always visible")
            }
            items(pinned, key = { it.id }) { goal ->
                GoalCardItem(goal, onCheckIn, onGoalClick, onDelete, onTogglePin, onToggleWidget)
            }
            item(span = StaggeredGridItemSpan.FullLine) {
                SectionHeader(title = "All Goals", subtitle = "${others.size} active")
            }
        }
        items(others, key = { it.id }) { goal ->
            GoalCardItem(goal, onCheckIn, onGoalClick, onDelete, onTogglePin, onToggleWidget)
        }
        item(span = StaggeredGridItemSpan.FullLine) { Spacer(Modifier.height(80.dp)) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GoalCardItem(
    goal: Goal,
    onCheckIn: (Long) -> Unit,
    onGoalClick: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onTogglePin: (Goal) -> Unit,
    onToggleWidget: (Goal) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box {
        GoalCard(
            goal = goal,
            onCheckIn = { onCheckIn(goal.id) },
            onClick = { onGoalClick(goal.id) },
            modifier = Modifier
                .animateItemPlacement()
                .combinedClickable(
                    onClick = { onGoalClick(goal.id) },
                    onLongClick = { showMenu = true }
                )
        )
        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
            DropdownMenuItem(
                text = { Text(if (goal.isPinned) "Unpin" else "📌 Pin to top") },
                onClick = { onTogglePin(goal); showMenu = false },
                leadingIcon = { Icon(Icons.Default.PushPin, null) }
            )
            DropdownMenuItem(
                text = { Text(if (goal.isWidgetEnabled) "Remove widget" else "🪄 Add to widget") },
                onClick = { onToggleWidget(goal); showMenu = false },
                leadingIcon = { Icon(Icons.Default.Widgets, null) }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                onClick = { onDelete(goal.id); showMenu = false },
                leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(onAddGoal: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎯", fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        Text("No goals yet", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            "Create your first streak card and start building
                    consistency one day at a time.",
                    fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = onAddGoal) { Text("Create First Goal") }
    }
}

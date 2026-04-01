package com.example.streakcard.uii.screens.addgoal


import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.streakcard.data.model.CardColorScheme
import com.example.streakcard.data.model.CardShape
import com.example.streakcard.data.model.CardSize
import com.example.streakcard.data.model.MissPolicy
import com.example.streakcard.domain.model.Goal
import com.example.streakcard.uii.component.GoalCard
import com.example.streakcard.ui.theme.cardGradients
import java.time.LocalDate
import kotlin.collections.get

// Popular emoji suggestions for goals
private val EMOJI_SUGGESTIONS = listOf(
    "🎯","💪","📚","🏃","🧘","💧","🥗","😴","🧠","✍️",
    "🎸","🎨","💻","🌱","🚴","🤸","📝","🎤","🏊","⚽"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    onBack: () -> Unit,
    viewModel: AddGoalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Goal Card") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = viewModel::saveGoal,
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) CircularProgressIndicator(Modifier.size(20.dp))
                        else Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Live preview
            PreviewCard(uiState)

            // ── Title
            SectionLabel("Goal Title")
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Read 30 minutes daily") },
                leadingIcon = { Text("📝", fontSize = 20.sp, modifier = Modifier.padding(start = 4.dp)) },
                isError = uiState.error != null && uiState.title.isBlank(),
                singleLine = true
            )

            // ── Emoji picker
            SectionLabel("Icon")
            EmojiPicker(
                selected = uiState.emoji,
                onSelect = viewModel::onEmojiChange
            )

            // ── Color picker
            SectionLabel("Color Theme")
            ColorPicker(
                selected = uiState.selectedColor,
                onSelect = viewModel::onColorChange
            )

            // ── Shape picker
            SectionLabel("Card Shape")
            ShapePicker(
                selected = uiState.selectedShape,
                onSelect = viewModel::onShapeChange
            )

            // ── Size picker
            SectionLabel("Card Size")
            SizePicker(
                selected = uiState.selectedSize,
                onSelect = viewModel::onSizeChange
            )

            // ── Miss policy
            SectionLabel("If You Miss a Day…")
            MissPolicyPicker(
                selected = uiState.missPolicy,
                onSelect = viewModel::onMissPolicyChange
            )

            // ── Notes
            SectionLabel("Notes (optional)")
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = viewModel::onNotesChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Why is this goal important?") },
                minLines = 2,
                maxLines = 4
            )

            Spacer(Modifier.height(80.dp))
        }
    }

    uiState.error?.let { msg ->
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text("Oops") },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = viewModel::dismissError) { Text("OK") } }
        )
    }
}

@Composable
private fun PreviewCard(state: AddGoalUiState) {
    val previewGoal = Goal(
        title = state.title.ifBlank { "Your Goal" },
        emoji = state.emoji,
        colorScheme = state.selectedColor,
        cardShape = state.selectedShape,
        cardSize = state.selectedSize,
        missPolicy = state.missPolicy,
        createdAt = LocalDate.now()
    )
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        GoalCard(goal = previewGoal, onCheckIn = {}, onClick = {})
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
}

@Composable
private fun EmojiPicker(selected: String, onSelect: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(EMOJI_SUGGESTIONS) { emoji ->
            val isSelected = emoji == selected
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        if (isSelected) 2.dp else 0.dp,
                        MaterialTheme.colorScheme.primary, CircleShape
                    )
                    .clickable { onSelect(emoji) },
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 22.sp)
            }
        }
    }
}

@Composable
private fun ColorPicker(selected: CardColorScheme, onSelect: (CardColorScheme) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(CardColorScheme.values().toList()) { scheme ->
            val gradient = cardGradients[scheme]!!
            val isSelected = scheme == selected
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(gradient.start, gradient.end))
                    )
                    .border(
                        if (isSelected) 3.dp else 0.dp,
                        Color.White, CircleShape
                    )
                    .clickable { onSelect(scheme) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ShapePicker(selected: CardShape, onSelect: (CardShape) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CardShape.values().forEach { shape ->
            val label = when (shape) {
                CardShape.ROUNDED  -> "⬜ Rounded"
                CardShape.STADIUM  -> "💊 Pill"
                CardShape.SHARP    -> "▪ Sharp"
                CardShape.CIRCLE   -> "⭕ Circle"
                CardShape.DIAMOND  -> "♦ Diamond"
            }
            FilterChip(
                selected = shape == selected,
                onClick = { onSelect(shape) },
                label = { Text(label, fontSize = 12.sp) }
            )
        }
    }
}

@Composable
private fun SizePicker(selected: CardSize, onSelect: (CardSize) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        CardSize.values().forEach { size ->
            FilterChip(
                selected = size == selected,
                onClick = { onSelect(size) },
                label = { Text(size.name.lowercase().replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

@Composable
private fun MissPolicyPicker(selected: MissPolicy, onSelect: (MissPolicy) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PolicyOption(
            title = "🔁 Reset Streak",
            description = "Streak restarts from 1 — builds discipline",
            isSelected = selected == MissPolicy.RESET,
            onClick = { onSelect(MissPolicy.RESET) }
        )
        PolicyOption(
            title = "➡️ Continue Streak",
            description = "Streak keeps going — forgiving mode",
            isSelected = selected == MissPolicy.CONTINUE,
            onClick = { onSelect(MissPolicy.CONTINUE) }
        )
    }
}

@Composable
private fun PolicyOption(
    title: String, description: String,
    isSelected: Boolean, onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(selected = isSelected, onClick = onClick)
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(description, fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

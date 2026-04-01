package com.example.streakcard.ui.theme.screens.addgoal


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streakcard.data.model.CardColorScheme
import com.example.streakcard.data.model.CardShape
import com.example.streakcard.data.model.CardSize
import com.example.streakcard.data.model.MissPolicy
import com.example.streakcard.domain.model.Goal
import com.example.streakcard.domain.usecase.AddGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AddGoalUiState(
    val title: String = "",
    val emoji: String = "🎯",
    val selectedColor: CardColorScheme = CardColorScheme.OCEAN,
    val selectedShape: CardShape = CardShape.ROUNDED,
    val selectedSize: CardSize = CardSize.MEDIUM,
    val missPolicy: MissPolicy = MissPolicy.RESET,
    val reminderTime: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddGoalViewModel @Inject constructor(
    private val addGoalUseCase: AddGoalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddGoalUiState())
    val uiState: StateFlow<AddGoalUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onEmojiChange(value: String) = _uiState.update { it.copy(emoji = value) }
    fun onColorChange(value: CardColorScheme) = _uiState.update { it.copy(selectedColor = value) }
    fun onShapeChange(value: CardShape) = _uiState.update { it.copy(selectedShape = value) }
    fun onSizeChange(value: CardSize) = _uiState.update { it.copy(selectedSize = value) }
    fun onMissPolicyChange(value: MissPolicy) = _uiState.update { it.copy(missPolicy = value) }
    fun onReminderChange(value: String) = _uiState.update { it.copy(reminderTime = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun saveGoal() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Title cannot be empty") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val goal = Goal(
                title = state.title.trim(),
                emoji = state.emoji,
                colorScheme = state.selectedColor,
                cardShape = state.selectedShape,
                cardSize = state.selectedSize,
                missPolicy = state.missPolicy,
                reminderTime = state.reminderTime.ifBlank { null },
                notes = state.notes,
                createdAt = LocalDate.now()
            )
            runCatching { addGoalUseCase(goal) }
                .onSuccess { _uiState.update { it.copy(isSaving = false, isSaved = true) } }
                .onFailure { e -> _uiState.update { it.copy(isSaving = false, error = e.message) } }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}

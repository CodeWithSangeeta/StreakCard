package com.example.streakcard.uii.screens.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streakcard.domain.model.Goal
import com.example.streakcard.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGoalsUseCase: GetGoalsUseCase,
    private val checkInGoalUseCase: CheckInGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase,
    private val updateGoalUseCase: UpdateGoalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getGoalsUseCase()
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { goals ->
                    _uiState.update { it.copy(goals = goals, isLoading = false) }
                }
        }
    }

    fun checkIn(goalId: Long) {
        viewModelScope.launch {
            runCatching { checkInGoalUseCase(goalId) }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            runCatching { deleteGoalUseCase(goalId) }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun togglePin(goal: Goal) {
        viewModelScope.launch {
            runCatching { updateGoalUseCase(goal.copy(isPinned = !goal.isPinned)) }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun toggleWidget(goal: Goal) {
        viewModelScope.launch {
            runCatching { updateGoalUseCase(goal.copy(isWidgetEnabled = !goal.isWidgetEnabled)) }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun dismissError() { _uiState.update { it.copy(errorMessage = null) } }
}

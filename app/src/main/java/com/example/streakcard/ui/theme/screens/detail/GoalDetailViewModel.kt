package com.example.streakcard.ui.theme.screens.detail


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.streakcard.domain.model.Goal
import com.streakcard.domain.usecase.CheckInGoalUseCase
import com.streakcard.domain.usecase.DeleteGoalUseCase
import com.streakcard.domain.usecase.GetGoalByIdUseCase
import com.streakcard.domain.usecase.UpdateGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val goal: Goal? = null,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GoalDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getGoalByIdUseCase: GetGoalByIdUseCase,
    private val checkInGoalUseCase: CheckInGoalUseCase,
    private val deleteGoalUseCase: DeleteGoalUseCase,
    private val updateGoalUseCase: UpdateGoalUseCase
) : ViewModel() {

    private val goalId: Long = checkNotNull(savedStateHandle["goalId"])

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getGoalByIdUseCase(goalId)
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { goal ->
                    _uiState.update { it.copy(goal = goal, isLoading = false) }
                }
        }
    }

    fun checkIn() {
        viewModelScope.launch {
            runCatching { checkInGoalUseCase(goalId) }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun delete() {
        viewModelScope.launch {
            runCatching { deleteGoalUseCase(goalId) }
                .onSuccess { _uiState.update { it.copy(isDeleted = true) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }
}

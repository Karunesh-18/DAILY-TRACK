package com.dailytrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailytrack.data.database.entities.StudentAttendanceStats
import com.dailytrack.data.repository.AttendanceRepository
import com.dailytrack.utils.AttendanceCalculator
import com.dailytrack.utils.AttendanceGrade
import com.dailytrack.utils.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val attendanceCalculator: AttendanceCalculator,
    private val dateUtils: DateUtils
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()
    
    val studentStats: StateFlow<List<StudentAttendanceStats>> = 
        attendanceRepository.getAllStudentsAttendanceStats()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    
    private val _selectedTimeRange = MutableStateFlow(TimeRange.ALL_TIME)
    val selectedTimeRange: StateFlow<TimeRange> = _selectedTimeRange.asStateFlow()
    
    init {
        loadAnalyticsData()
        loadTodaysSummary()
    }
    
    fun setTimeRange(timeRange: TimeRange) {
        _selectedTimeRange.value = timeRange
        loadAnalyticsData()
    }
    
    fun refreshData() {
        loadAnalyticsData()
        loadTodaysSummary()
    }
    
    private fun loadAnalyticsData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Calculate overall statistics
                val stats = studentStats.value
                val classAverage = if (stats.isNotEmpty()) {
                    stats.map { it.attendancePercentage }.average()
                } else {
                    0.0
                }
                
                val studentsAbove75 = stats.count { it.attendancePercentage >= 75.0 }
                val studentsBelow75 = stats.count { it.attendancePercentage < 75.0 }
                val studentsAtRisk = stats.count { it.attendancePercentage < 80.0 && it.attendancePercentage >= 75.0 }
                
                val gradeDistribution = stats.groupBy { 
                    attendanceCalculator.getAttendanceGrade(it.attendancePercentage) 
                }.mapValues { it.value.size }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    classAverage = classAverage,
                    totalStudents = stats.size,
                    studentsAbove75Percent = studentsAbove75,
                    studentsBelow75Percent = studentsBelow75,
                    studentsAtRisk = studentsAtRisk,
                    gradeDistribution = gradeDistribution
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load analytics: ${e.message}"
                )
            }
        }
    }
    
    private fun loadTodaysSummary() {
        viewModelScope.launch {
            try {
                val today = dateUtils.getCurrentDateString()
                val todaysSummary = attendanceRepository.getAttendanceSummaryForDate(today)
                
                _uiState.value = _uiState.value.copy(
                    todaysSummary = todaysSummary
                )
            } catch (e: Exception) {
                // Silently handle error for today's summary as it's optional
            }
        }
    }
    
    fun getStudentsByGrade(grade: AttendanceGrade): List<StudentAttendanceStats> {
        return studentStats.value.filter { 
            attendanceCalculator.getAttendanceGrade(it.attendancePercentage) == grade 
        }
    }
    
    fun getStudentsNeedingAttention(): List<StudentAttendanceStats> {
        return studentStats.value.filter { it.attendancePercentage < 75.0 }
            .sortedBy { it.attendancePercentage }
    }
    
    fun getTopPerformers(): List<StudentAttendanceStats> {
        return studentStats.value.filter { it.attendancePercentage >= 90.0 }
            .sortedByDescending { it.attendancePercentage }
            .take(10)
    }
    
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class AnalyticsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val classAverage: Double = 0.0,
    val totalStudents: Int = 0,
    val studentsAbove75Percent: Int = 0,
    val studentsBelow75Percent: Int = 0,
    val studentsAtRisk: Int = 0,
    val gradeDistribution: Map<AttendanceGrade, Int> = emptyMap(),
    val todaysSummary: com.dailytrack.data.database.entities.AttendanceSummary? = null
)

enum class TimeRange(val displayName: String) {
    ALL_TIME("All Time"),
    CURRENT_MONTH("This Month"),
    LAST_30_DAYS("Last 30 Days"),
    CURRENT_SEMESTER("This Semester")
}
package com.dailytrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailytrack.data.database.entities.AttendanceStatus
import com.dailytrack.data.database.entities.StudentAttendanceForDate
import com.dailytrack.data.repository.AttendanceRepository
import com.dailytrack.data.repository.StudentRepository
import com.dailytrack.utils.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val studentRepository: StudentRepository,
    private val dateUtils: DateUtils
) : ViewModel() {
    
    private val _selectedDate = MutableStateFlow(dateUtils.getCurrentDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()
    
    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()
    
    val studentsWithAttendance: StateFlow<List<StudentAttendanceForDate>> = 
        selectedDate.flatMapLatest { date ->
            attendanceRepository.getStudentsWithAttendanceForDate(date)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    init {
        loadAttendanceData()
    }
    
    fun selectDate(date: String) {
        _selectedDate.value = date
        loadAttendanceData()
    }
    
    fun markAttendance(studentId: String, status: AttendanceStatus, leaveFormSubmitted: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = attendanceRepository.markAttendance(
                studentId = studentId,
                date = _selectedDate.value,
                status = status,
                leaveFormSubmitted = leaveFormSubmitted
            )
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Attendance marked successfully"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to mark attendance"
                    )
                }
            )
        }
    }
    
    fun markAllPresent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val students = studentsWithAttendance.value
            val attendanceData = students.map { studentData ->
                com.dailytrack.data.repository.AttendanceData(
                    studentId = studentData.student.id,
                    date = _selectedDate.value,
                    status = AttendanceStatus.PRESENT,
                    leaveFormSubmitted = false
                )
            }
            
            val result = attendanceRepository.markBulkAttendance(attendanceData)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "All students marked present"
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to mark all present"
                    )
                }
            )
        }
    }
    
    fun shareAttendanceReport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val report = attendanceRepository.getAttendanceReportForDate(_selectedDate.value)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    attendanceReport = report
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to generate report: ${e.message}"
                )
            }
        }
    }
    
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
    
    fun clearReport() {
        _uiState.value = _uiState.value.copy(attendanceReport = null)
    }
    
    private fun loadAttendanceData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val summary = attendanceRepository.getAttendanceSummaryForDate(_selectedDate.value)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    attendanceSummary = summary
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load attendance data: ${e.message}"
                )
            }
        }
    }
    
    fun isToday(): Boolean {
        return dateUtils.isToday(_selectedDate.value)
    }
    
    fun getFormattedDate(): String {
        return dateUtils.formatDateForDisplay(_selectedDate.value)
    }
    
    fun getRelativeDateString(): String {
        return dateUtils.getRelativeDateString(_selectedDate.value)
    }
}

data class AttendanceUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val attendanceSummary: com.dailytrack.data.database.entities.AttendanceSummary? = null,
    val attendanceReport: com.dailytrack.data.repository.AttendanceReport? = null
)
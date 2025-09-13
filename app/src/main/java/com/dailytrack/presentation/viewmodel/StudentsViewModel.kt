    // Admin action: replace all students with bulk list
    fun replaceAllWithBulkList(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            studentRepository.replaceAllStudents(studentRepository.bulkStudentList)
            onComplete?.invoke()
        }
    }
package com.dailytrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailytrack.data.database.entities.Student
import com.dailytrack.data.repository.StudentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StudentsViewModel @Inject constructor(
    private val studentRepository: StudentRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _uiState = MutableStateFlow(StudentsUiState())
    val uiState: StateFlow<StudentsUiState> = _uiState.asStateFlow()
    
    val students: StateFlow<List<Student>> = searchQuery
        .debounce(300) // Debounce search to avoid excessive queries
        .flatMapLatest { query ->
            if (query.isBlank()) {
                studentRepository.getAllActiveStudents()
            } else {
                studentRepository.searchStudents(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun addStudent(name: String, rollNo: String) {
        viewModelScope.launch {
            if (name.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Student name cannot be empty"
                )
                return@launch
            }
            
            if (rollNo.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Roll number cannot be empty"
                )
                return@launch
            }
            
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = studentRepository.addStudent(name, rollNo)
            
            result.fold(
                onSuccess = { student ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Student added successfully",
                        showAddDialog = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to add student"
                    )
                }
            )
        }
    }
    
    fun updateStudent(student: Student, newName: String, newRollNo: String) {
        viewModelScope.launch {
            if (newName.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Student name cannot be empty"
                )
                return@launch
            }
            
            if (newRollNo.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Roll number cannot be empty"
                )
                return@launch
            }
            
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val updatedStudent = student.copy(
                name = newName,
                rollNo = newRollNo
            )
            
            val result = studentRepository.updateStudent(updatedStudent)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Student updated successfully",
                        showEditDialog = false,
                        selectedStudent = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to update student"
                    )
                }
            )
        }
    }
    
    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = studentRepository.deleteStudent(studentId)
            
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Student deleted successfully",
                        showDeleteDialog = false,
                        selectedStudent = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to delete student"
                    )
                }
            )
        }
    }
    
    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = true)
    }
    
    fun hideAddDialog() {
        _uiState.value = _uiState.value.copy(showAddDialog = false)
    }
    
    fun showEditDialog(student: Student) {
        _uiState.value = _uiState.value.copy(
            showEditDialog = true,
            selectedStudent = student
        )
    }
    
    fun hideEditDialog() {
        _uiState.value = _uiState.value.copy(
            showEditDialog = false,
            selectedStudent = null
        )
    }
    
    fun showDeleteDialog(student: Student) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            selectedStudent = student
        )
    }
    
    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            selectedStudent = null
        )
    }
    
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
    
    suspend fun isRollNoAvailable(rollNo: String, excludeStudentId: String? = null): Boolean {
        return studentRepository.isRollNoAvailable(rollNo, excludeStudentId)
    }
}

data class StudentsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val selectedStudent: Student? = null
)
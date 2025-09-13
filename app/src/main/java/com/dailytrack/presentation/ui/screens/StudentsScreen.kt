package com.dailytrack.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailytrack.R
import com.dailytrack.presentation.ui.components.LoadingIndicator
import com.dailytrack.presentation.ui.components.StudentCard
import com.dailytrack.presentation.ui.components.StudentDialog
import com.dailytrack.presentation.viewmodel.StudentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(
    viewModel: StudentsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val students by viewModel.students.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text(stringResource(R.string.students_search)) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Add Student Button
        Button(
            onClick = { viewModel.showAddDialog() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.students_add))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Students Count
        Text(
            text = "Total Students: ${students.size}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Loading State
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }
        
        // Students List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(students) { student ->
                StudentCard(
                    student = student,
                    onEditClick = { viewModel.showEditDialog(student) },
                    onDeleteClick = { viewModel.showDeleteDialog(student) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    
    // Add Student Dialog
    if (uiState.showAddDialog) {
        StudentDialog(
            title = stringResource(R.string.students_add),
            studentName = "",
            studentRollNo = "",
            onDismiss = { viewModel.hideAddDialog() },
            onConfirm = { name, rollNo ->
                viewModel.addStudent(name, rollNo)
            },
            isLoading = uiState.isLoading
        )
    }
    
    // Edit Student Dialog
    if (uiState.showEditDialog && uiState.selectedStudent != null) {
        StudentDialog(
            title = stringResource(R.string.student_edit),
            studentName = uiState.selectedStudent!!.name,
            studentRollNo = uiState.selectedStudent!!.rollNo,
            onDismiss = { viewModel.hideEditDialog() },
            onConfirm = { name, rollNo ->
                viewModel.updateStudent(uiState.selectedStudent!!, name, rollNo)
            },
            isLoading = uiState.isLoading
        )
    }
    
    // Delete Confirmation Dialog
    if (uiState.showDeleteDialog && uiState.selectedStudent != null) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text(stringResource(R.string.dialog_delete_student_title)) },
            text = { Text(stringResource(R.string.dialog_delete_student_message)) },
            confirmButton = {
                TextButton(
                    onClick = { 
                        viewModel.deleteStudent(uiState.selectedStudent!!.id)
                    },
                    enabled = !uiState.isLoading
                ) {
                    Text(stringResource(R.string.dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideDeleteDialog() }
                ) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        )
    }
    
    // Show snackbar for error/success messages
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Handle error message with Snackbar
            viewModel.clearMessages()
        }
    }
    
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // Handle success message with Snackbar
            viewModel.clearMessages()
        }
    }
}
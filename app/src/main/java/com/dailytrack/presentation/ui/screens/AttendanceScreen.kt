package com.dailytrack.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailytrack.R
import com.dailytrack.presentation.ui.components.AttendanceCard
import com.dailytrack.presentation.ui.components.DateSelector
import com.dailytrack.presentation.ui.components.LoadingIndicator
import com.dailytrack.presentation.viewmodel.AttendanceViewModel
import com.dailytrack.utils.WhatsAppIntegration
import com.dailytrack.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    whatsAppIntegration: WhatsAppIntegration = WhatsAppIntegration(),
    dateUtils: DateUtils = DateUtils()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val studentsWithAttendance by viewModel.studentsWithAttendance.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    
    // Handle sharing
    LaunchedEffect(uiState.attendanceReport) {
        uiState.attendanceReport?.let { report ->
            whatsAppIntegration.shareAttendanceReport(context, report, dateUtils)
            viewModel.clearReport()
        }
    }
    
    // Show snackbar for messages
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Handle error message
            viewModel.clearMessages()
        }
    }
    
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // Handle success message
            viewModel.clearMessages()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Date Selector
        DateSelector(
            selectedDate = selectedDate,
            onDateSelected = { date -> viewModel.selectDate(date) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.markAllPresent() },
                modifier = Modifier.weight(1f),
                enabled = !uiState.isLoading && viewModel.isToday()
            ) {
                Text(stringResource(R.string.attendance_mark_all_present))
            }
            
            OutlinedButton(
                onClick = { viewModel.shareAttendanceReport() },
                modifier = Modifier.weight(1f),
                enabled = !uiState.isLoading
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.attendance_share_report))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Summary Card
        uiState.attendanceSummary?.let { summary ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Today's Summary",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Present: ${summary.presentCount}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Absent: ${summary.absentCount}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Column {
                            Text(
                                text = "OD: ${summary.odCount}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Average: ${String.format("%.1f", summary.classAverage)}%",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
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
            items(studentsWithAttendance) { studentData ->
                AttendanceCard(
                    student = studentData.student,
                    attendanceRecord = studentData.attendanceRecord,
                    onStatusChange = { status, leaveFormSubmitted ->
                        viewModel.markAttendance(
                            studentData.student.id,
                            status,
                            leaveFormSubmitted
                        )
                    },
                    isEditable = viewModel.isToday(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
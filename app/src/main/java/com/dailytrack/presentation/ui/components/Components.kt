package com.dailytrack.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailytrack.data.database.entities.AttendanceRecord
import com.dailytrack.data.database.entities.AttendanceStatus
import com.dailytrack.data.database.entities.Student
import com.dailytrack.data.database.entities.StudentAttendanceStats
import com.dailytrack.presentation.ui.theme.*

@Composable
fun LoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.size(48.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun DateSelector(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Simplified date selector - in real implementation would use DatePicker
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Selected Date: $selectedDate",
                style = MaterialTheme.typography.titleMedium
            )
            // Add DatePicker implementation here
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceCard(
    student: Student,
    attendanceRecord: AttendanceRecord?,
    onStatusChange: (AttendanceStatus, Boolean) -> Unit,
    isEditable: Boolean,
    modifier: Modifier = Modifier
) {
    var leaveFormSubmitted by remember { 
        mutableStateOf(attendanceRecord?.leaveFormSubmitted ?: false) 
    }
    
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${student.rollNo} - ${student.name}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AttendanceStatus.values().forEach { status ->
                    FilterChip(
                        onClick = { 
                            if (isEditable) {
                                onStatusChange(status, leaveFormSubmitted)
                            }
                        },
                        label = { Text(status.name) },
                        selected = attendanceRecord?.status == status,
                        enabled = isEditable,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (status) {
                                AttendanceStatus.PRESENT -> PresentColor
                                AttendanceStatus.ABSENT -> AbsentColor
                                AttendanceStatus.OD -> OnDutyColor
                            }
                        )
                    )
                }
            }
            
            if (attendanceRecord?.status == AttendanceStatus.ABSENT || 
                (isEditable && attendanceRecord?.status == null)) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = leaveFormSubmitted,
                        onCheckedChange = { 
                            if (isEditable) {
                                leaveFormSubmitted = it
                                onStatusChange(
                                    attendanceRecord?.status ?: AttendanceStatus.ABSENT, 
                                    it
                                )
                            }
                        },
                        enabled = isEditable
                    )
                    Text(
                        text = "Leave Form Submitted",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun StudentCard(
    student: Student,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Roll No: ${student.rollNo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row {
                TextButton(onClick = onEditClick) {
                    Text("Edit")
                }
                TextButton(onClick = onDeleteClick) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun StudentDialog(
    title: String,
    studentName: String,
    studentRollNo: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    isLoading: Boolean
) {
    var name by remember { mutableStateOf(studentName) }
    var rollNo by remember { mutableStateOf(studentRollNo) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Name") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = rollNo,
                    onValueChange = { rollNo = it },
                    label = { Text("Roll Number") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, rollNo) },
                enabled = !isLoading && name.isNotBlank() && rollNo.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AnalyticsCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = valueColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StudentStatsCard(
    studentStats: StudentAttendanceStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${studentStats.rollNo} - ${studentStats.studentName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${String.format("%.1f", studentStats.attendancePercentage)}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = when {
                        studentStats.attendancePercentage >= 90 -> ExcellentColor
                        studentStats.attendancePercentage >= 80 -> GoodColor
                        studentStats.attendancePercentage >= 75 -> SatisfactoryColor
                        studentStats.attendancePercentage >= 60 -> NeedsImprovementColor
                        else -> PoorColor
                    },
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Attended: ${studentStats.attendedClasses}/${studentStats.totalClasses}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (studentStats.classesNeededFor75Percent > 0) {
                    Text(
                        text = "Need: ${studentStats.classesNeededFor75Percent} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
package com.dailytrack.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailytrack.R
import com.dailytrack.presentation.ui.components.AnalyticsCard
import com.dailytrack.presentation.ui.components.LoadingIndicator
import com.dailytrack.presentation.ui.components.StudentStatsCard
import com.dailytrack.presentation.viewmodel.AnalyticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val studentStats by viewModel.studentStats.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with Refresh Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.analytics_title),
                style = MaterialTheme.typography.headlineSmall
            )
            
            IconButton(
                onClick = { viewModel.refreshData() }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Loading State
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Today's Summary Card
            item {
                uiState.todaysSummary?.let { summary ->
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
                                text = "Today's Attendance",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${summary.presentCount}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Present",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${summary.absentCount}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Absent",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${summary.odCount}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "OD",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Overall Statistics Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnalyticsCard(
                        title = "Class Average",
                        value = "${String.format("%.1f", uiState.classAverage)}%",
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsCard(
                        title = "Total Students",
                        value = "${uiState.totalStudents}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnalyticsCard(
                        title = "Above 75%",
                        value = "${uiState.studentsAbove75Percent}",
                        valueColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsCard(
                        title = "Below 75%",
                        value = "${uiState.studentsBelow75Percent}",
                        valueColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            
            // Students Needing Attention
            item {
                val studentsNeedingAttention = viewModel.getStudentsNeedingAttention()
                if (studentsNeedingAttention.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Students Needing Attention (Below 75%)",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            studentsNeedingAttention.take(5).forEach { student ->
                                Text(
                                    text = "${student.rollNo} - ${student.studentName}: ${String.format("%.1f", student.attendancePercentage)}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            
                            if (studentsNeedingAttention.size > 5) {
                                Text(
                                    text = "And ${studentsNeedingAttention.size - 5} more...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
            
            // Individual Student Stats
            item {
                Text(
                    text = "Individual Student Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(studentStats) { student ->
                StudentStatsCard(
                    studentStats = student,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
    
    // Error handling
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Handle error with Snackbar
            viewModel.clearErrorMessage()
        }
    }
}
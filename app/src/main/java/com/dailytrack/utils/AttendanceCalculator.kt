package com.dailytrack.utils

import kotlin.math.ceil
import kotlin.math.max
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceCalculator @Inject constructor() {
    
    /**
     * Calculate attendance percentage
     * @param totalClasses Total number of classes conducted
     * @param attendedClasses Number of classes attended (Present status)
     * @return Attendance percentage as Double
     */
    fun calculateAttendancePercentage(totalClasses: Int, attendedClasses: Int): Double {
        if (totalClasses == 0) return 0.0
        return (attendedClasses.toDouble() / totalClasses.toDouble()) * 100.0
    }
    
    /**
     * Calculate class average attendance percentage
     * @param studentAttendances List of student attendance data
     * @return Class average percentage as Double
     */
    fun calculateClassAverage(studentAttendances: List<StudentAttendanceData>): Double {
        if (studentAttendances.isEmpty()) return 0.0
        
        val totalPercentage = studentAttendances.sumOf { attendance ->
            calculateAttendancePercentage(attendance.totalClasses, attendance.attendedClasses)
        }
        
        return totalPercentage / studentAttendances.size
    }
    
    /**
     * Calculate how many classes a student needs to attend to reach 75% attendance
     * Uses the formula: (currentAttended + x) / (totalClasses + x) >= 0.75
     * Solving for x: x >= (0.75 * totalClasses - currentAttended) / 0.25
     * 
     * @param currentAttended Number of classes currently attended
     * @param totalClasses Total number of classes conducted so far
     * @return Number of consecutive classes needed to attend (0 if already above 75%)
     */
    fun getClassesToAttendFor75Percent(currentAttended: Int, totalClasses: Int): Int {
        if (totalClasses == 0) return 0
        
        val currentPercentage = calculateAttendancePercentage(totalClasses, currentAttended)
        if (currentPercentage >= 75.0) return 0
        
        // Formula: x >= (0.75 * totalClasses - currentAttended) / 0.25
        val requiredClasses = (0.75 * totalClasses - currentAttended) / 0.25
        return max(0, ceil(requiredClasses).toInt())
    }
    
    /**
     * Calculate how many classes a student can miss while maintaining 75% attendance
     * Uses the formula: (currentAttended) / (totalClasses + x) >= 0.75
     * Solving for x: x <= (currentAttended / 0.75) - totalClasses
     * 
     * @param currentAttended Number of classes currently attended
     * @param totalClasses Total number of classes conducted so far
     * @return Number of classes that can be missed (0 if cannot miss any)
     */
    fun getClassesCanMissFor75Percent(currentAttended: Int, totalClasses: Int): Int {
        if (totalClasses == 0 || currentAttended == 0) return 0
        
        // Formula: x <= (currentAttended / 0.75) - totalClasses
        val maxTotalClasses = currentAttended / 0.75
        val canMiss = maxTotalClasses - totalClasses
        return max(0, canMiss.toInt())
    }
    
    /**
     * Get attendance status based on percentage
     * @param percentage Attendance percentage
     * @return AttendanceGrade enum
     */
    fun getAttendanceGrade(percentage: Double): AttendanceGrade {
        return when {
            percentage >= 90.0 -> AttendanceGrade.EXCELLENT
            percentage >= 80.0 -> AttendanceGrade.GOOD
            percentage >= 75.0 -> AttendanceGrade.SATISFACTORY
            percentage >= 60.0 -> AttendanceGrade.NEEDS_IMPROVEMENT
            else -> AttendanceGrade.POOR
        }
    }
    
    /**
     * Check if student is at risk of falling below 75% attendance
     * @param currentAttended Number of classes currently attended
     * @param totalClasses Total number of classes conducted so far
     * @param upcomingClasses Number of upcoming classes to consider
     * @return true if at risk, false otherwise
     */
    fun isAtRiskOfFalling75Percent(
        currentAttended: Int, 
        totalClasses: Int, 
        upcomingClasses: Int
    ): Boolean {
        if (totalClasses == 0) return false
        
        val futurePercentage = calculateAttendancePercentage(
            totalClasses + upcomingClasses,
            currentAttended
        )
        
        return futurePercentage < 75.0
    }
}

data class StudentAttendanceData(
    val studentId: String,
    val totalClasses: Int,
    val attendedClasses: Int
)

enum class AttendanceGrade(val displayName: String, val colorHex: String) {
    EXCELLENT("Excellent", "#4CAF50"),
    GOOD("Good", "#8BC34A"),
    SATISFACTORY("Satisfactory", "#FFC107"),
    NEEDS_IMPROVEMENT("Needs Improvement", "#FF9800"),
    POOR("Poor", "#F44336")
}
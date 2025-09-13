package com.dailytrack.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class StudentWithAttendance(
    @Embedded val student: Student,
    @Relation(
        parentColumn = "id",
        entityColumn = "studentId"
    )
    val attendanceRecords: List<AttendanceRecord>
)

data class StudentAttendanceForDate(
    @Embedded val student: Student,
    @Embedded(prefix = "attendanceRecord_") val attendanceRecord: AttendanceRecord?
)

data class AttendanceSummary(
    val date: String,
    val totalStudents: Int,
    val presentCount: Int,
    val absentCount: Int,
    val odCount: Int,
    val classAverage: Double
)

data class StudentAttendanceStats(
    val studentId: String,
    val studentName: String,
    val rollNo: String,
    val totalClasses: Int,
    val attendedClasses: Int,
    val attendancePercentage: Double,
    val classesNeededFor75Percent: Int
)
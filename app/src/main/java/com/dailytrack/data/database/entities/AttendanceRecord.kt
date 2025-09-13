package com.dailytrack.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "attendance_records",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["date"]),
        Index(value = ["studentId", "date"], unique = true)
    ]
)
data class AttendanceRecord(
    @PrimaryKey val id: String,
    val studentId: String,
    val date: String, // Format: yyyy-MM-dd
    val status: AttendanceStatus,
    val leaveFormSubmitted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    OD // On Duty
}
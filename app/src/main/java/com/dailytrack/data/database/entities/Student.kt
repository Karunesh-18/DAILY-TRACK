package com.dailytrack.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val id: String,
    val rollNo: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
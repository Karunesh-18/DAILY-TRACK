package com.dailytrack.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import android.content.Context
import com.dailytrack.data.database.dao.AttendanceDao
import com.dailytrack.data.database.dao.StudentDao
import com.dailytrack.data.database.entities.AttendanceRecord
import com.dailytrack.data.database.entities.AttendanceStatus
import com.dailytrack.data.database.entities.Student

@Database(
    entities = [Student::class, AttendanceRecord::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "attendance_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromAttendanceStatus(status: AttendanceStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toAttendanceStatus(status: String): AttendanceStatus {
        return AttendanceStatus.valueOf(status)
    }
}
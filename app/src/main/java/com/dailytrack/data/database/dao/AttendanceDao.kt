package com.dailytrack.data.database.dao

import androidx.room.*
import com.dailytrack.data.database.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    
    @Query("""
        SELECT s.*, ar.id as attendanceRecord_id, ar.studentId as attendanceRecord_studentId, 
               ar.date as attendanceRecord_date, ar.status as attendanceRecord_status, 
               ar.leaveFormSubmitted as attendanceRecord_leaveFormSubmitted, 
               ar.createdAt as attendanceRecord_createdAt, ar.updatedAt as attendanceRecord_updatedAt
        FROM students s 
        LEFT JOIN attendance_records ar ON s.id = ar.studentId AND ar.date = :date
        WHERE s.isActive = 1
        ORDER BY s.rollNo ASC
    """)
    fun getStudentsWithAttendanceForDate(date: String): Flow<List<StudentAttendanceForDate>>
    
    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId AND date = :date")
    suspend fun getAttendanceForStudentAndDate(studentId: String, date: String): AttendanceRecord?
    
    @Query("SELECT * FROM attendance_records WHERE date = :date")
    suspend fun getAttendanceForDate(date: String): List<AttendanceRecord>
    
    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceRecord>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendanceRecord: AttendanceRecord)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(attendanceRecords: List<AttendanceRecord>)
    
    @Update
    suspend fun updateAttendance(attendanceRecord: AttendanceRecord)
    
    @Delete
    suspend fun deleteAttendance(attendanceRecord: AttendanceRecord)
    
    @Query("DELETE FROM attendance_records WHERE date = :date")
    suspend fun deleteAttendanceForDate(date: String)
    
    @Query("""
        SELECT 
            COUNT(*) as totalClasses,
            SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) as attendedClasses
        FROM attendance_records 
        WHERE studentId = :studentId
    """)
    suspend fun getAttendanceStatsForStudent(studentId: String): AttendanceStats?
    
    @Query("""
        SELECT 
            ar.studentId,
            s.name as studentName,
            s.rollNo,
            COUNT(*) as totalClasses,
            SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) as attendedClasses
        FROM attendance_records ar
        JOIN students s ON ar.studentId = s.id
        WHERE s.isActive = 1
        GROUP BY ar.studentId, s.name, s.rollNo
        ORDER BY s.rollNo ASC
    """)
    fun getAllStudentsAttendanceStats(): Flow<List<StudentAttendanceStatsRaw>>
    
    @Query("""
        SELECT 
            :date as date,
            COUNT(DISTINCT s.id) as totalStudents,
            SUM(CASE WHEN ar.status = 'PRESENT' THEN 1 ELSE 0 END) as presentCount,
            SUM(CASE WHEN ar.status = 'ABSENT' THEN 1 ELSE 0 END) as absentCount,
            SUM(CASE WHEN ar.status = 'OD' THEN 1 ELSE 0 END) as odCount
        FROM students s
        LEFT JOIN attendance_records ar ON s.id = ar.studentId AND ar.date = :date
        WHERE s.isActive = 1
    """)
    suspend fun getAttendanceSummaryForDate(date: String): AttendanceSummaryRaw?
    
    @Query("""
        SELECT DISTINCT date 
        FROM attendance_records 
        ORDER BY date DESC 
        LIMIT 30
    """)
    suspend fun getRecentAttendanceDates(): List<String>
}

data class AttendanceStats(
    val totalClasses: Int,
    val attendedClasses: Int
)

data class StudentAttendanceStatsRaw(
    val studentId: String,
    val studentName: String,
    val rollNo: String,
    val totalClasses: Int,
    val attendedClasses: Int
)

data class AttendanceSummaryRaw(
    val date: String,
    val totalStudents: Int,
    val presentCount: Int,
    val absentCount: Int,
    val odCount: Int
)
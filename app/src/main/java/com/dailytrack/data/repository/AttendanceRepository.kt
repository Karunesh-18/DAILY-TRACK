package com.dailytrack.data.repository

import com.dailytrack.data.database.dao.AttendanceDao
import com.dailytrack.data.database.dao.StudentDao
import com.dailytrack.data.database.entities.*
import com.dailytrack.utils.AttendanceCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val studentDao: StudentDao,
    private val attendanceCalculator: AttendanceCalculator
) {
    
    fun getStudentsWithAttendanceForDate(date: String): Flow<List<StudentAttendanceForDate>> {
        return attendanceDao.getStudentsWithAttendanceForDate(date)
    }
    
    suspend fun markAttendance(
        studentId: String, 
        date: String, 
        status: AttendanceStatus
    ): Result<Unit> {
        return try {
            val existingRecord = attendanceDao.getAttendanceForStudentAndDate(studentId, date)
            if (existingRecord != null) {
                // Update existing record
                val updatedRecord = existingRecord.copy(
                    status = status,
                    updatedAt = System.currentTimeMillis()
                )
                attendanceDao.updateAttendance(updatedRecord)
            } else {
                // Create new record
                val newRecord = AttendanceRecord(
                    id = UUID.randomUUID().toString(),
                    studentId = studentId,
                    date = date,
                    status = status,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                attendanceDao.insertAttendance(newRecord)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markBulkAttendance(attendanceData: List<AttendanceData>): Result<Unit> {
        return try {
            val records = attendanceData.map { data ->
                AttendanceRecord(
                    id = UUID.randomUUID().toString(),
                    studentId = data.studentId,
                    date = data.date,
                    status = data.status,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            }
            
            attendanceDao.insertAttendanceRecords(records)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceRecord>> {
        return attendanceDao.getAttendanceForStudent(studentId)
    }
    
    suspend fun getAttendanceSummaryForDate(date: String): AttendanceSummary? {
        val summaryRaw = attendanceDao.getAttendanceSummaryForDate(date) ?: return null
        
        val classAverage = if (summaryRaw.totalStudents > 0) {
            (summaryRaw.presentCount.toDouble() / summaryRaw.totalStudents.toDouble()) * 100.0
        } else {
            0.0
        }
        
        return AttendanceSummary(
            date = summaryRaw.date,
            totalStudents = summaryRaw.totalStudents,
            presentCount = summaryRaw.presentCount,
            absentCount = summaryRaw.absentCount,
            odCount = summaryRaw.odCount,
            classAverage = classAverage
        )
    }
    
    fun getAllStudentsAttendanceStats(): Flow<List<StudentAttendanceStats>> {
        return attendanceDao.getAllStudentsAttendanceStats().map { statsRawList ->
            statsRawList.map { statsRaw ->
                val attendancePercentage = attendanceCalculator.calculateAttendancePercentage(
                    statsRaw.totalClasses,
                    statsRaw.attendedClasses
                )
                
                val classesNeededFor75Percent = attendanceCalculator.getClassesToAttendFor75Percent(
                    statsRaw.attendedClasses,
                    statsRaw.totalClasses
                )
                
                StudentAttendanceStats(
                    studentId = statsRaw.studentId,
                    studentName = statsRaw.studentName,
                    rollNo = statsRaw.rollNo,
                    totalClasses = statsRaw.totalClasses,
                    attendedClasses = statsRaw.attendedClasses,
                    attendancePercentage = attendancePercentage,
                    classesNeededFor75Percent = classesNeededFor75Percent
                )
            }
        }
    }
    
    suspend fun getStudentAttendanceStats(studentId: String): StudentAttendanceStats? {
        val student = studentDao.getStudentById(studentId) ?: return null
        val stats = attendanceDao.getAttendanceStatsForStudent(studentId) ?: return null
        
        val attendancePercentage = attendanceCalculator.calculateAttendancePercentage(
            stats.totalClasses,
            stats.attendedClasses
        )
        
        val classesNeededFor75Percent = attendanceCalculator.getClassesToAttendFor75Percent(
            stats.attendedClasses,
            stats.totalClasses
        )
        
        return StudentAttendanceStats(
            studentId = student.id,
            studentName = student.name,
            rollNo = student.rollNo,
            totalClasses = stats.totalClasses,
            attendedClasses = stats.attendedClasses,
            attendancePercentage = attendancePercentage,
            classesNeededFor75Percent = classesNeededFor75Percent
        )
    }
    
    suspend fun getRecentAttendanceDates(): List<String> {
        return attendanceDao.getRecentAttendanceDates()
    }
    
    suspend fun deleteAttendanceForDate(date: String): Result<Unit> {
        return try {
            attendanceDao.deleteAttendanceForDate(date)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getAttendanceReportForDate(date: String): AttendanceReport {
        val summary = getAttendanceSummaryForDate(date)
        val attendanceRecords = attendanceDao.getAttendanceForDate(date)
        
        val absentees = mutableListOf<StudentStatus>()
        val odStudents = mutableListOf<StudentStatus>()
        
        attendanceRecords.forEach { record ->
            val student = studentDao.getStudentById(record.studentId)
            if (student != null) {
                val studentStatus = StudentStatus(
                    studentId = student.id,
                    rollNo = student.rollNo,
                    name = student.name,
                    status = record.status
                )
                when (record.status) {
                    AttendanceStatus.ABSENT -> absentees.add(studentStatus)
                    AttendanceStatus.OD -> odStudents.add(studentStatus)
                    else -> {} // Do nothing for PRESENT
                }
            }
        }
        
        return AttendanceReport(
            date = date,
            summary = summary ?: AttendanceSummary(date, 0, 0, 0, 0, 0.0),
            absentees = absentees.sortedBy { it.rollNo },
            odStudents = odStudents.sortedBy { it.rollNo }
        )
    }
}

data class AttendanceData(
    val studentId: String,
    val date: String,
    val status: AttendanceStatus
)

data class StudentStatus(
    val studentId: String,
    val rollNo: String,
    val name: String,
    val status: AttendanceStatus
)

data class AttendanceReport(
    val date: String,
    val summary: AttendanceSummary,
    val absentees: List<StudentStatus>,
    val odStudents: List<StudentStatus>
)
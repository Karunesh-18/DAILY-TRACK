package com.dailytrack.data.repository

import com.dailytrack.data.database.dao.StudentDao
import com.dailytrack.data.database.entities.Student
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val studentDao: StudentDao
) {
    
    fun getAllActiveStudents(): Flow<List<Student>> {
        return studentDao.getAllActiveStudents()
    }
    
    suspend fun getStudentById(studentId: String): Student? {
        return studentDao.getStudentById(studentId)
    }
    
    suspend fun getStudentByRollNo(rollNo: String): Student? {
        return studentDao.getStudentByRollNo(rollNo)
    }
    
    fun searchStudents(query: String): Flow<List<Student>> {
        return studentDao.searchStudents(query)
    }
    
    suspend fun addStudent(name: String, rollNo: String): Result<Student> {
        return try {
            // Check if roll number already exists
            val existingStudent = studentDao.getStudentByRollNo(rollNo)
            if (existingStudent != null) {
                return Result.failure(Exception("Roll number already exists"))
            }
            
            val student = Student(
                id = UUID.randomUUID().toString(),
                rollNo = rollNo.trim(),
                name = name.trim(),
                createdAt = System.currentTimeMillis()
            )
            
            studentDao.insertStudent(student)
            Result.success(student)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateStudent(student: Student): Result<Student> {
        return try {
            // Check if roll number is being changed and if it conflicts
            val existingStudent = studentDao.getStudentByRollNo(student.rollNo)
            if (existingStudent != null && existingStudent.id != student.id) {
                return Result.failure(Exception("Roll number already exists"))
            }
            
            studentDao.updateStudent(student.copy(
                name = student.name.trim(),
                rollNo = student.rollNo.trim()
            ))
            Result.success(student)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteStudent(studentId: String): Result<Unit> {
        return try {
            studentDao.deactivateStudent(studentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getActiveStudentCount(): Int {
        return studentDao.getActiveStudentCount()
    }
    
    suspend fun isRollNoAvailable(rollNo: String, excludeStudentId: String? = null): Boolean {
        val existingStudent = studentDao.getStudentByRollNo(rollNo)
        return existingStudent == null || existingStudent.id == excludeStudentId
    }
}
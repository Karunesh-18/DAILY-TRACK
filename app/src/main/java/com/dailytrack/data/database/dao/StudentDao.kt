package com.dailytrack.data.database.dao

import androidx.room.*
import com.dailytrack.data.database.entities.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    
    @Query("SELECT * FROM students WHERE active = 1 ORDER BY rollNo ASC")
    fun getAllActiveStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentById(studentId: String): Student?

    @Query("SELECT * FROM students WHERE rollNo = :rollNo AND active = 1")
    suspend fun getStudentByRollNo(rollNo: String): Student?

    @Query("SELECT * FROM students WHERE name LIKE '%' || :searchQuery || '%' AND active = 1 ORDER BY rollNo ASC")
    fun searchStudents(searchQuery: String): Flow<List<Student>>
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertStudent(student: Student)
    
    @Update
    suspend fun updateStudent(student: Student)
    
    @Query("UPDATE students SET active = 0 WHERE id = :studentId")
    suspend fun deactivateStudent(studentId: String)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT COUNT(*) FROM students WHERE active = 1")
    suspend fun getActiveStudentCount(): Int

    @Query("SELECT COUNT(*) FROM students WHERE rollNo = :rollNo AND active = 1")
    suspend fun checkRollNoExists(rollNo: String): Int
}
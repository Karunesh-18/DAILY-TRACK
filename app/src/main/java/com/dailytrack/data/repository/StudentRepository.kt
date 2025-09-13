package com.dailytrack.data.repository

// ...existing code...
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
    companion object {
        val bulkStudentList = listOf(
            "CSE071" to "HARI VIGNESH S",
            "CSE072" to "HARINATH S",
            "CSE073" to "HARINI.C",
            "CSE074" to "HARINI C H",
            "CSE075" to "HARINI K",
            "CSE076" to "HARIPRASATH M",
            "CSE077" to "HARIPRIYAN. A",
            "CSE078" to "HARIS BALAJEE",
            "CSE079" to "HARISH G",
            "CSE080" to "HARISH KUMAR.V",
            "CSE081" to "HARISH.S",
            "CSE082" to "HARITHA E",
            "CSE083" to "HARSHAD R",
            "CSE084" to "HARSHINI A",
            "CSE085" to "HARSHITHA M P",
            "CSE086" to "HERANYAA .TP",
            "CSE087" to "ILAMSARAVANBALAJI PA",
            "CSE088" to "JAGATHRATCHAGAN M",
            "CSE089" to "JAIANISH.J",
            "CSE090" to "JAISURYA S",
            "CSE091" to "JASHWANTH J",
            "CSE092" to "JAY PRAKASH SAH",
            "CSE093" to "JAYASURIYA S",
            "CSE094" to "JAYATHEERTHAN P",
            "CSE095" to "JEFF JEROME JABEZ",
            "CSE096" to "JENITHA M",
            "CSE097" to "JOSHUA RUBERT R",
            "CSE098" to "JUMAANAH BASHEETH",
            "CSE101" to "KANHAIYA PATEL",
            "CSE102" to "KANISH KRISHNA J P",
            "CSE103" to "KANISH M R",
            "CSE104" to "KANISHKA. S",
            "CSE105" to "KANWAL KISHORE",
            "CSE106" to "KARTHIKA A",
            "CSE107" to "KARUNESH AR",
            "CSE108" to "KATHIRAVAN.S.P",
            "CSE109" to "KATHIRVEL S",
            "CSE110" to "KAVINKUMAR C",
            "CSE111" to "KAVIN PRAKASH T",
            "CSE112" to "KAVIPRIYA P",
            "CSE113" to "KAVYA K",
            "CSE114" to "KAVYASRI D",
            "CSE115" to "KEERTHI AANAND K S",
            "CSE116" to "KHAVIYA SREE M",
            "CSE117" to "KIRITH MALINI D S",
            "CSE118" to "KIRITHIKA.S.K",
            "CSE119" to "KOWSALYA V",
            "CSE120" to "KRISHNAVARUN K",
            "CSE121" to "KRISHNAN A",
            "CSE122" to "LAVANYA R",
            "CSE123" to "LOGAPRABHU S",
            "CSE124" to "LOGAVARSHHNI.S",
            "CSE125" to "MADHANIKA.M",
            "CSE126" to "MADHUMITHA Y",
            "CSE127" to "MADHUSREE M",
            "CSE128" to "MANASA DEVI CHAPAGAIN",
            "CSE129" to "MANISH BASNET",
            "CSE130" to "MANISH PRAKKASH M S",
            "CSE131" to "MANOJ V",
            "CSE132" to "MANOJKUMAR S",
            "CSE133" to "MANSUR ANSARI",
            "CSE134" to "MATHIYAZHINI S",
            "CSE135" to "MATHUMITHA S",
            "CSE136" to "MEKALA.S",
            "CSE137" to "MITHRHA.Y",
            "CSE138" to "MOHAMED ASIF. S",
            "CSE139" to "MOHAMMED SUHAIL.M",
            "CSE140" to "MOHAN KAARTHICK C"
        )
    }
    // Bulk replace all students
    suspend fun replaceAllStudents(newStudents: List<Pair<String, String>>) {
        // Deactivate all current students
        val activeStudents = getAllActiveStudents().first()
        for (student in activeStudents) {
            deleteStudent(student.id)
        }
        // Add new students
        for ((rollNo, name) in newStudents) {
            addStudent(name, rollNo)
        }
    }
    
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
package com.dailytrack.util

import android.content.Context
import com.dailytrack.data.database.dao.StudentDao
import com.dailytrack.data.database.entities.Student
import kotlinx.coroutines.runBlocking
import java.util.UUID

object BulkStudentInsert {
    fun insertAll(context: Context, studentDao: StudentDao) {
        val students = listOf(
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
        runBlocking {
            students.forEach { (rollNo, name) ->
                val student = Student(
                    id = UUID.randomUUID().toString(),
                    rollNo = rollNo,
                    name = name,
                    createdAt = System.currentTimeMillis()
                )
                studentDao.insertStudent(student)
            }
        }
    }
}

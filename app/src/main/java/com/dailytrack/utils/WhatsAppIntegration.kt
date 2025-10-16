package com.dailytrack.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.dailytrack.data.repository.AttendanceReport
import com.dailytrack.data.repository.StudentStatus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WhatsAppIntegration @Inject constructor() {
    
    companion object {
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
        private const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"
    }
    
    /**
     * Share attendance report via WhatsApp
     */
    fun shareAttendanceReport(
        context: Context,
        report: AttendanceReport,
        dateUtils: DateUtils
    ): ShareResult {
        val message = formatAttendanceMessage(report, dateUtils)
        return shareMessage(context, message)
    }
    
    /**
     * Share custom message via WhatsApp
     */
    fun shareMessage(context: Context, message: String): ShareResult {
        return try {
            // First try regular WhatsApp
            if (isWhatsAppInstalled(context, WHATSAPP_PACKAGE)) {
                val intent = createWhatsAppIntent(message, WHATSAPP_PACKAGE)
                context.startActivity(intent)
                ShareResult.Success
            } 
            // Then try WhatsApp Business
            else if (isWhatsAppInstalled(context, WHATSAPP_BUSINESS_PACKAGE)) {
                val intent = createWhatsAppIntent(message, WHATSAPP_BUSINESS_PACKAGE)
                context.startActivity(intent)
                ShareResult.Success
            }
            // Fall back to system sharing
            else {
                shareViaSystemIntent(context, message)
                ShareResult.FallbackUsed
            }
        } catch (e: Exception) {
            ShareResult.Failed(e.message ?: "Unknown error occurred")
        }
    }
    
    /**
     * Check if WhatsApp is available
     */
    fun isWhatsAppAvailable(context: Context): WhatsAppAvailability {
        val hasWhatsApp = isWhatsAppInstalled(context, WHATSAPP_PACKAGE)
        val hasWhatsAppBusiness = isWhatsAppInstalled(context, WHATSAPP_BUSINESS_PACKAGE)
        
        return when {
            hasWhatsApp && hasWhatsAppBusiness -> WhatsAppAvailability.Both
            hasWhatsApp -> WhatsAppAvailability.WhatsApp
            hasWhatsAppBusiness -> WhatsAppAvailability.WhatsAppBusiness
            else -> WhatsAppAvailability.None
        }
    }
    
    private fun isWhatsAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    private fun createWhatsAppIntent(message: String, packageName: String): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            `package` = packageName
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    
    private fun shareViaSystemIntent(context: Context, message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_SUBJECT, "Attendance Report")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        val chooser = Intent.createChooser(intent, "Share Attendance Report")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
    
    private fun formatAttendanceMessage(report: AttendanceReport, dateUtils: DateUtils): String {
        return buildString {
            appendLine("📋 Daily Attendance Report")
            appendLine("📅 Date: ${dateUtils.formatDateForDisplay(report.date)}")
            appendLine("📅 Day: ${dateUtils.getDayOfWeek(report.date)}")
            appendLine()
            
            // Absentees section
            if (report.absentees.isNotEmpty()) {
                appendLine("❌ Absentees (${report.absentees.size}):")
                // report.absentees.forEach { student ->
                //     appendLine("• ${student.rollNo} - ${student.name}")
                // }
                // Add roll number summary line
                val rollNumbers = report.absentees.joinToString(", ") { it.rollNo }
                appendLine()
                appendLine("Roll numbers: $rollNumbers")
                appendLine()
            }
            
            // On Duty section
            if (report.odStudents.isNotEmpty()) {
                appendLine("📝 On Duty - OD (${report.odStudents.size}):")
                report.odStudents.forEach { student ->
                    appendLine("• ${student.rollNo} - ${student.name}")
                }
                appendLine()
            }
            
            // Summary section
            appendLine("📊 Summary:")
            appendLine("👥 Total Students: ${report.summary.totalStudents}")
            appendLine("✅ Present: ${report.summary.presentCount}")
            appendLine("❌ Absent: ${report.summary.absentCount}")
            appendLine("📝 On Duty: ${report.summary.odCount}")
            appendLine("📈 Class Average: ${String.format("%.2f", report.summary.classAverage)}%")
            
            // Additional insights
            if (report.summary.totalStudents > 0) {
                val presentPercentage = (report.summary.presentCount.toDouble() / report.summary.totalStudents) * 100
                appendLine("📊 Present Today: ${String.format("%.1f", presentPercentage)}%")
                
                when {
                    presentPercentage >= 90 -> appendLine("🎉 Excellent attendance today!")
                    presentPercentage >= 80 -> appendLine("👍 Good attendance today!")
                    presentPercentage >= 70 -> appendLine("⚠️ Average attendance today")
                    else -> appendLine("🚨 Low attendance today - needs attention")
                }
            }
            
            appendLine()
            appendLine("Generated by Daily Track App")
        }
    }
    
    /**
     * Format a simple attendance summary message
     */
    fun formatQuickSummary(
        date: String,
        presentCount: Int,
        totalStudents: Int,
        dateUtils: DateUtils
    ): String {
        val percentage = if (totalStudents > 0) {
            (presentCount.toDouble() / totalStudents) * 100
        } else {
            0.0
        }
        
        return buildString {
            appendLine("📊 Quick Attendance Summary")
            appendLine("📅 ${dateUtils.formatDateForDisplay(date)}")
            appendLine("✅ Present: $presentCount/$totalStudents")
            appendLine("📈 Percentage: ${String.format("%.1f", percentage)}%")
        }
    }
    
    /**
     * Format absentee-only message
     */
    fun formatAbsenteeMessage(
        date: String,
        absentees: List<StudentStatus>,
        dateUtils: DateUtils
    ): String {
        return buildString {
            appendLine("❌ Absentees Report")
            appendLine("📅 Date: ${dateUtils.formatDateForDisplay(date)}")
            appendLine()
            
            if (absentees.isEmpty()) {
                appendLine("🎉 No absentees today! Perfect attendance!")
            } else {
                appendLine("Total Absentees: ${absentees.size}")
                appendLine()
                absentees.forEach { student ->
                    appendLine("• ${student.rollNo} - ${student.name}")
                }
            }
        }
    }
}

sealed class ShareResult {
    object Success : ShareResult()
    object FallbackUsed : ShareResult()
    data class Failed(val error: String) : ShareResult()
}

enum class WhatsAppAvailability {
    WhatsApp,
    WhatsAppBusiness,
    Both,
    None
}
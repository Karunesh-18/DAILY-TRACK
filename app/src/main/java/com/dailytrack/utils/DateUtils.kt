package com.dailytrack.utils

import kotlinx.datetime.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUtils @Inject constructor() {
    
    companion object {
        const val DATE_FORMAT_STORAGE = "yyyy-MM-dd"
        const val DATE_FORMAT_DISPLAY = "MMM dd, yyyy"
        const val DATE_FORMAT_DISPLAY_SHORT = "dd/MM/yyyy"
        const val DATE_FORMAT_DAY_MONTH = "dd MMM"
        const val DATE_FORMAT_MONTH_YEAR = "MMM yyyy"
    }
    
    private val storageFormatter = SimpleDateFormat(DATE_FORMAT_STORAGE, Locale.getDefault())
    private val displayFormatter = SimpleDateFormat(DATE_FORMAT_DISPLAY, Locale.getDefault())
    private val shortDisplayFormatter = SimpleDateFormat(DATE_FORMAT_DISPLAY_SHORT, Locale.getDefault())
    private val dayMonthFormatter = SimpleDateFormat(DATE_FORMAT_DAY_MONTH, Locale.getDefault())
    private val monthYearFormatter = SimpleDateFormat(DATE_FORMAT_MONTH_YEAR, Locale.getDefault())
    
    /**
     * Get current date in storage format (yyyy-MM-dd)
     */
    fun getCurrentDateString(): String {
        return Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date.toString()
    }
    
    /**
     * Get current date as LocalDate
     */
    fun getCurrentDate(): LocalDate {
        return Clock.System.now().toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
    }
    
    /**
     * Convert date string from storage format to display format
     */
    fun formatDateForDisplay(dateString: String): String {
        return try {
            val date = storageFormatter.parse(dateString)
            date?.let { displayFormatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    /**
     * Convert date string from storage format to short display format
     */
    fun formatDateForShortDisplay(dateString: String): String {
        return try {
            val date = storageFormatter.parse(dateString)
            date?.let { shortDisplayFormatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    /**
     * Convert date string to day and month format (dd MMM)
     */
    fun formatDateForDayMonth(dateString: String): String {
        return try {
            val date = storageFormatter.parse(dateString)
            date?.let { dayMonthFormatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    /**
     * Convert LocalDate to storage format string
     */
    fun formatDateForStorage(date: LocalDate): String {
        return date.toString()
    }
    
    /**
     * Convert Date to storage format string
     */
    fun formatDateForStorage(date: Date): String {
        return storageFormatter.format(date)
    }
    
    /**
     * Parse date string from storage format to Date object
     */
    fun parseStorageDate(dateString: String): Date? {
        return try {
            storageFormatter.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get date string for a specific number of days ago
     */
    fun getDateDaysAgo(days: Int): String {
        val date = getCurrentDate().minus(DatePeriod(days = days))
        return formatDateForStorage(date)
    }
    
    /**
     * Get date string for a specific number of days from now
     */
    fun getDateDaysFromNow(days: Int): String {
        val date = getCurrentDate().plus(DatePeriod(days = days))
        return formatDateForStorage(date)
    }
    
    /**
     * Check if a date string represents today
     */
    fun isToday(dateString: String): Boolean {
        return dateString == getCurrentDateString()
    }
    
    /**
     * Check if a date string represents yesterday
     */
    fun isYesterday(dateString: String): Boolean {
        return dateString == getDateDaysAgo(1)
    }
    
    /**
     * Get day of week for a date string
     */
    fun getDayOfWeek(dateString: String): String {
        return try {
            val date = parseStorageDate(dateString)
            date?.let { 
                SimpleDateFormat("EEEE", Locale.getDefault()).format(it)
            } ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Get relative date string (Today, Yesterday, or formatted date)
     */
    fun getRelativeDateString(dateString: String): String {
        return when {
            isToday(dateString) -> "Today"
            isYesterday(dateString) -> "Yesterday"
            else -> formatDateForDisplay(dateString)
        }
    }
    
    /**
     * Get month and year string for a date
     */
    fun getMonthYear(dateString: String): String {
        return try {
            val date = storageFormatter.parse(dateString)
            date?.let { monthYearFormatter.format(it) } ?: ""
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Get list of dates in a month
     */
    fun getDatesInMonth(year: Int, month: Int): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1) // month is 0-based in Calendar
        
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        
        for (day in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
            dates.add(storageFormatter.format(calendar.time))
        }
        
        return dates
    }
    
    /**
     * Get list of dates in current month
     */
    fun getDatesInCurrentMonth(): List<String> {
        val currentDate = getCurrentDate()
        return getDatesInMonth(currentDate.year, currentDate.monthNumber)
    }
    
    /**
     * Calculate difference in days between two dates
     */
    fun getDaysDifference(startDate: String, endDate: String): Int {
        return try {
            val start = parseStorageDate(startDate)
            val end = parseStorageDate(endDate)
            
            if (start != null && end != null) {
                val diffInMillis = end.time - start.time
                (diffInMillis / (24 * 60 * 60 * 1000)).toInt()
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Check if date is weekend
     */
    fun isWeekend(dateString: String): Boolean {
        return try {
            val date = parseStorageDate(dateString)
            date?.let {
                val calendar = Calendar.getInstance()
                calendar.time = it
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
}
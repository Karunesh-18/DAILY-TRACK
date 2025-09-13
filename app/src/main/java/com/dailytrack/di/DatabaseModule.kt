package com.dailytrack.di

import android.content.Context
import androidx.room.Room
import com.dailytrack.data.database.AppDatabase
import com.dailytrack.data.database.dao.AttendanceDao
import com.dailytrack.data.database.dao.StudentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "daily_track_database"
        ).build()
    }

    @Provides
    fun provideStudentDao(database: AppDatabase): StudentDao = database.studentDao()

    @Provides
    fun provideAttendanceDao(database: AppDatabase): AttendanceDao = database.attendanceDao()
}
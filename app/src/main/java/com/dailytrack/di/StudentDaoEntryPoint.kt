package com.dailytrack.di

import com.dailytrack.data.database.dao.StudentDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface StudentDaoEntryPoint {
    fun studentDao(): StudentDao
}
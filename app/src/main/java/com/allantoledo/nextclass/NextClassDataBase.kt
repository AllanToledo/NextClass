package com.allantoledo.nextclass

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CollegeClass::class],
    version = 2,
)
abstract class NextClassDataBase : RoomDatabase() {
    abstract fun collegeClassDao(): CollegeClassDao
}

fun getNextClassDataBase(context: Context): NextClassDataBase {
    return Room.databaseBuilder(context, NextClassDataBase::class.java, "nextclass")
        .allowMainThreadQueries().fallbackToDestructiveMigration().build()
}
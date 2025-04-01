package com.example.rksishedule.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rksishedule.data.DATABASE_VERSON

@Database(
    version = DATABASE_VERSON,
    exportSchema = true,
    entities = [
        SheduleDB::class,
        GroupsDB::class,
        TeachersDB::class,
        LessonsDB::class,
        HistoryDB::class,
        NotificationDB::class
    ],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sheduleDao(): SheduleDao

}
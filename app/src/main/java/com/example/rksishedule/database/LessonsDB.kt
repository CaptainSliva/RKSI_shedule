package com.example.rksishedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    indices = [androidx.room.Index("id")]
)

data class LessonsDB(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "entity") val entity_name: String,
    @ColumnInfo(name = "lesson") val lesson_name: String
)

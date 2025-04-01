package com.example.rksishedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "teachers",
    indices = [androidx.room.Index("id")]
)
data class TeachersDB(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "teacher_name") val teacher_name: String,
    @ColumnInfo(name = "teacher_value") val teacher_value: String
)

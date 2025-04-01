package com.example.rksishedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shedule",
    indices = [Index("id")],
)
data class SheduleDB(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo (name = "edit_lesson") val edit: Boolean,
    @ColumnInfo (name = "date") val date: String?,
    @ColumnInfo (name = "lesson_number") val lessonNumber: String?,
    @ColumnInfo (name = "lesson") val lesson: String?,
    @ColumnInfo (name = "entity") val entity: String?,
    @ColumnInfo(name = "aud") val aud: String?,
    @ColumnInfo (name = "edit_entity") val editEntity: String?,
)

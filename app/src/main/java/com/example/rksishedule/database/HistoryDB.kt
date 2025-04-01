package com.example.rksishedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history",
    indices = [Index("id")]
)
data class HistoryDB(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo (name = "edit_lesson") val edit: Boolean,
    @ColumnInfo (name = "date") val date: String?,
    @ColumnInfo (name = "lesson_number") val lessonNumber: String?,
    @ColumnInfo (name = "lesson") val lesson: String?,
    @ColumnInfo (name = "entity") val entity: String?,
    @ColumnInfo(name = "aud") val aud: String?,
    @ColumnInfo(name = "time_lesson") val timeLesson: String?,
    @ColumnInfo (name = "name_entity_for_history") val entityForHistory: String?,
    @ColumnInfo(name = "delete_lesson_name") val deleteLessonName: String?,
)

package com.example.rksishedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "notifications",
    indices = [androidx.room.Index("id")]
)

data class NotificationDB(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "day") val day: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "state") val state: Boolean
)

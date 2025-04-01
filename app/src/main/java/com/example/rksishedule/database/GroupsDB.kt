package com.example.rksishedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "groups",
    indices = [Index("id")]
)
data class GroupsDB(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "group_name") val group_name: String,
    @ColumnInfo(name = "group_value") val group_value: String
)

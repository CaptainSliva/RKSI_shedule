package com.example.rksishedule.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.rksishedule.database.AppDatabase
import com.example.rksishedule.database.SheduleDao
import com.example.rksishedule.preferences.USER_PREFERENCES_NAME
import com.example.rksishedule.utils.FunctionsApp

class GlobalVariables {
}

const val DATABASE_VERSON = 10
fun DBconnect(context: Context): SheduleDao {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java, "sheduleDB"
    ).fallbackToDestructiveMigration().build().sheduleDao()

}
val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

val functionsApp = FunctionsApp()
var weekDataRV = mutableListOf<RecyclerView>()
var historyDataRV = mutableListOf<RecyclerView>()
var sheduleLink = ""
var currentDay = ""

var currentEntity = ""
var isRefreshed = false
var nightMode = false

var mainEntity = ""
var start = true
var entity = ""
var uView = ""
var entityIsNotSelect = false
val inviseLessonName = "null"

var allGroups = mutableListOf<String>()
var allTrashGroups = mutableMapOf<String, String>()
var allTrashTeachers = mutableMapOf<String, String>()
val allTeachers = mutableListOf<String>()
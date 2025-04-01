package com.example.rksishedule.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rksishedule.utils.Lesson

@Dao
interface SheduleDao {
    @Query("SELECT * FROM shedule")
    suspend fun getAllFromShedule(): List<SheduleDB>

    @Query("SELECT * FROM groups")
    suspend fun getAllGroupsFromDB(): List<GroupsDB>

    @Query("SELECT * FROM teachers")
    suspend fun getAllTeachersFromDB(): List<TeachersDB>

    @Query("SELECT * FROM lessons")
    suspend fun getAllLessonsFromDB(): List<LessonsDB>

    @Query("SELECT * FROM history")
    suspend fun getAllHistoryFromDB(): List<HistoryDB>

    @Query("SELECT * FROM notifications")
    suspend fun getAllNotificationsFromDB(): List<NotificationDB> // TODO


    @Query("DELETE FROM teachers")
    suspend fun clearTeachers()

    @Query("DELETE FROM groups")
    suspend fun clearGroups()

    @Query("DELETE FROM shedule")
    suspend fun clearShedule()

    @Query("DELETE FROM lessons")
    suspend fun clearLessons()

    @Query("DELETE FROM history")
    suspend fun clearHistory()

    @Query("DELETE FROM notifications")
    suspend fun clearNotifications() // TODO


    @Query("UPDATE history SET edit_lesson = :editLesson, lesson = :lesson, delete_lesson_name = :deleteLessonName, entity = :entity, aud = :aud, time_lesson = :timeLesson WHERE id = :id")
    suspend fun updateHistoryOnID(id: Int, editLesson: Boolean, lesson: String, deleteLessonName: String, entity: String, aud: String, timeLesson: String)

    @Query("UPDATE notifications SET day = :day, time = :time, message = :message, state = :state WHERE id = :id")
    suspend fun updateNotificationOnID(id: Int, day: String, time: String, message: String, state: Boolean) // TODO

    @Query("SELECT lesson FROM lessons WHERE entity = (:entity)")
    suspend fun findEntityLessons(entity: String): List<String>

    @Query("DELETE FROM history WHERE id = (:id)")
    suspend fun deleteHistory(id: Int)

    @Query("DELETE FROM notifications WHERE id = (:id)")
    suspend fun deleteNotify(id: Int) // TODO


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShedule(shedule: SheduleDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeachersDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupsDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonsDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryDB)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotify(notify: NotificationDB) // TODO


    @Delete
    suspend fun deleteDayFromDB(shedule: SheduleDB)

    @Delete
    suspend fun deleteHistoryLessonFromDB(historyLesson: HistoryDB)





}
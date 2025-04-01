package com.example.rksishedule.utils

import com.example.rksishedule.data.currentEntity
import com.example.rksishedule.data.inviseLessonName
import com.example.rksishedule.database.HistoryDB

open class Shedule(lessons: MutableList<Lesson>) { //HashMap<String, String> {"8:00-9:30" "Web-программирование;;;Каламбет;;;ауд;;;414} - для групп
    private var lessons = lessons

    fun getEditLessons() : MutableList<Boolean> {
        var lsns = mutableListOf<Boolean>()
        this.lessons.forEach { lsn ->
            lsns.add(lsn.getEditLesson())
        }
        return lsns
    }

    fun getDays() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessons.forEach { lsn ->
            lsns.add(lsn.getDay())
        }
        return lsns
    }
    fun getTimeLessons() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessons.forEach { lsn ->
            lsns.add(lsn.getTimeLesson())
        }
        return lsns
    }
    fun getLessons() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessons.forEach { lsn ->
            lsns.add(lsn.getLesson())
        }
        return lsns
    }
    fun getEntityes() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessons.forEach { lsn ->
            lsns.add(lsn.getEntity())
        }
        return lsns
    }
    fun getAudiencies() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessons.forEach { lsn ->
            lsns.add(lsn.getAudience())
        }
        return lsns
    }
    fun getLessonsNumbers() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessons.forEach { lsn ->
            lsns.add(lsn.getLessonNumber())
        }
        return lsns
    }


    fun setLessons(lessons: MutableList<Lesson>) {this.lessons = lessons}

    fun toHistory(id: Int?, i: Int, deleteLessonName: String): HistoryDB {
//        println("$id, ${getEditLessons()[i]}, ${getDays()[i]}, ${getLessonsNumbers()[i]}, ${getEntityes()[i]}, ${getAudiencies()[i]}, ${getTimeLessons()[i]}, $currentEntity, ${getLessons()[i]}, $deleteLessonName")
        val delLes = when{
            getLessons()[i] != inviseLessonName && getAudiencies()[i].contains("ауд.") -> getLessons()[i]
            deleteLessonName != inviseLessonName && getAudiencies()[i].contains("ауд.") -> deleteLessonName
            else -> inviseLessonName
        }
//        println(delLes)
        return HistoryDB(id, getEditLessons()[i], getDays()[i], getLessonsNumbers()[i], getLessons()[i], getEntityes()[i], getAudiencies()[i], getTimeLessons()[i], currentEntity, delLes)
    }
}
package com.example.rksishedule.utils


class SheduleHistory(lessons: MutableList<LessonHistory>) {

    private var lessonsHistory = lessons

    fun getDeleteLessons() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessonsHistory.forEach { lsn ->
            lsns.add(lsn.getDeleteLesson())
        }
        return lsns
    }
    fun getEditLessons() : MutableList<Boolean> {
        var lsns = mutableListOf<Boolean>()
        this.lessonsHistory.forEach { lsn ->
            lsns.add(lsn.getEditLesson())
        }
        return lsns
    }
    fun getDays() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessonsHistory.forEach { lsn ->
            lsns.add(lsn.getDay())
        }
        return lsns
    }
    fun getTimeLessons() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessonsHistory.forEach { lsn ->
            lsns.add(lsn.getTimeLesson())
        }
        return lsns
    }
    fun getLessons() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessonsHistory.forEach { lsn ->
            lsns.add(lsn.getLesson())
        }
        return lsns
    }
    fun getEntityes() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessonsHistory.forEach { lsn ->
            lsns.add(lsn.getEntity())
        }
        return lsns
    }
    fun getAudiencies() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessonsHistory.forEach { lsn ->
            lsns.add(lsn.getAudience())
        }
        return lsns
    }
    fun getLessonsNumbers() : MutableList<String> {
        var lsns = mutableListOf<String>()
        this.lessonsHistory.forEach { lsn ->
            lsns.add(lsn.getLessonNumber())
        }
        return lsns
    }


}
package com.example.rksishedule.utils


import com.example.rksishedule.parser.typeOfDay

public open class Lesson() {
    private var editLesson = false
    private var day = ""                                      //HashMap<String, String> {"8:00-9:30" "Web-программирование;;;ИС-44;;;ауд;;;414} - для преподов
    private var lesson = ""
    private var entity = ""
    private var timeLesson = ""
    private var auditory = ""
    private var lessonNumber = ""

    constructor(
        editLesson: Boolean,
        day: String,
        lessonNumber: String,
        lesson: String,
        entity: String,
        auditory: String,
        timeLesson: String,
    ) : this() {
        this.auditory = auditory
        this.day = day
        this.editLesson = editLesson
        this.entity = entity
        this.lesson = lesson
        this.lessonNumber = lessonNumber
        this.timeLesson = timeLesson
    }



    fun getEditLesson(): Boolean {return this.editLesson}
    fun getDay(): String {return  this.day}
    fun getTimeLesson(): String {return this.timeLesson}
    fun getLesson(): String {return this.lesson}
    fun getEntity(): String {return this.entity}
    fun getAudience(): String {return this.auditory}
    fun getLessonNumber(): String {return this.lessonNumber}

    open fun setEditLesson() {this.editLesson = true}
    open fun setDay(day: String) {this.day = day}
    open fun setTimeLesson(timeLesson: String) {this.timeLesson = timeLesson}
    open fun setLesson(lesson: String) {this.lesson = lesson}
    open fun setEntity(entity: String) {this.entity = entity}
    open fun setAudience(auditory: String) {this.auditory = auditory}
    fun setLessonNumber(typeOfDay: String, lessonTime: String) { //typeOfDay (common/short/monday)/((любой день недели)/сокращёнка/понедельник)
        if (typeOfDay == "понедельник") {
            lessonsInMondayDay(lessonTime)
        }
        else {
            lessonsInCommonDay(lessonTime)
            lessonsInShortDay(lessonTime)
            lessonsInUnCommonDay1(lessonTime)
            lessonsInUnCommonDay2(lessonTime)
        }

    }
    open fun setInvizeLessonNumber(lessonNumber: String) {
        this.lessonNumber = lessonNumber
    }

    fun lessonsInCommonDay(lessonTime: String) {
        when(lessonTime) {
            "08:00 — 09:30" -> this.lessonNumber = "1"
            "09:40 — 11:10" -> this.lessonNumber = "2"
            "11:30 — 13:00" -> this.lessonNumber = "3"
            "13:10 — 14:40" -> this.lessonNumber = "4"
            "15:00 — 16:30" -> this.lessonNumber = "5"
            "16:40 — 18:10" -> this.lessonNumber = "6"
            "18:20 — 19:50" -> this.lessonNumber = "7"
        }
    }

    fun lessonsInUnCommonDay1(lessonTime: String) {
        when(lessonTime) {
            "08:00 — 09:30" -> this.lessonNumber = "1"
            "09:40 — 11:10" -> this.lessonNumber = "2"
            "11:30 — 12:20" -> {
                this.lessonNumber = "3"
                typeOfDay = "uncommon1"
            }
            "12:30 — 13:20" -> {
                this.lessonNumber = "4"
                typeOfDay = "uncommon1"
            }
            "13:30 — 14:20" -> {
                this.lessonNumber = "5"
                typeOfDay = "uncommon1"
            }
            "14:30 — 15:20" -> {
                this.lessonNumber = "6"
                typeOfDay = "uncommon1"
            }
            "15:30 — 16:30" -> {
                this.lessonNumber = "7"
                typeOfDay = "uncommon1"
            }
        }
    }

    fun lessonsInUnCommonDay2(lessonTime: String) {
        when(lessonTime) {
            "08:00 — 09:30" -> this.lessonNumber = "1"
            "09:40 — 11:10" -> this.lessonNumber = "2"
            "11:30 — 13:00" -> this.lessonNumber = "3"
            "13.10 — 14.00" -> {
                this.lessonNumber = "4"
                typeOfDay = "uncommon2"
            }
            "14.10 — 15.00" -> {
                this.lessonNumber = "5"
                typeOfDay = "uncommon2"
            }
            "15.10 — 16.00" -> {
                this.lessonNumber = "6"
                typeOfDay = "uncommon2"
            }
            "16.10 — 17.00" -> {
                this.lessonNumber = "7"
                typeOfDay = "uncommon2"
            }
        }
    }

    fun lessonsInShortDay(lessonTime: String) {
        when(lessonTime) {
            "08:00 — 08:50" -> this.lessonNumber = "1"
            "09:00 — 09:50" -> this.lessonNumber = "2"
            "10:00 — 10:50" -> this.lessonNumber = "3"
            "11:00 — 11:50" -> this.lessonNumber = "4"
            "12:00 — 12:50" -> this.lessonNumber = "5"
            "13:00 — 13:50" -> this.lessonNumber = "6"
            "14:00 — 14:50" -> this.lessonNumber = "7"
        }
    }

    fun lessonsInMondayDay(lessonTime: String) {
        when(lessonTime) {
            "08:00 — 09:30" -> this.lessonNumber = "1"
            "09:40 — 11:10" -> this.lessonNumber = "2"
            "11:30 — 13:00" -> this.lessonNumber = "3"
            "13:05 — 14:05" -> this.lessonNumber = "4"
            "14:10 — 15:40" -> this.lessonNumber = "5"
            "16:00 — 17:30" -> this.lessonNumber = "6"
            "17:40 — 19:10" -> this.lessonNumber = "7"
        }
    }

}
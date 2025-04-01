package com.example.rksishedule.utils


class LessonHistory(): Lesson() {
    private var deleteLesson = ""

    constructor(
        editLesson: Boolean,
        day: String,
        lessonNumber: String,
        lesson: String,
        entity: String,
        auditory: String,
        timeLesson: String,
        deleteLesson: String,
    ) : this() {
        this.setAudience(auditory)
        this.setDay(day)
        if (editLesson) this.setEditLesson()
        this.setEntity(entity)
        this.setLesson(lesson)
        this.setInvizeLessonNumber(lessonNumber)
        this.setTimeLesson(timeLesson)
        this.deleteLesson = deleteLesson
    }

    fun getDeleteLesson(): String { return this.deleteLesson}
}
package com.example.rksishedule.parser

import android.content.Context
import android.widget.ProgressBar
import com.example.rksishedule.activities.println
import com.example.rksishedule.database.LessonsDB
import com.example.rksishedule.database.SheduleDB
import com.example.rksishedule.data.DBconnect
import com.example.rksishedule.data.allTrashGroups
import com.example.rksishedule.data.allTrashTeachers
import com.example.rksishedule.data.currentEntity
import com.example.rksishedule.data.entity
import com.example.rksishedule.data.entityIsNotSelect
import com.example.rksishedule.data.functionsApp
import com.example.rksishedule.data.inviseLessonName
import com.example.rksishedule.data.mainEntity
import com.example.rksishedule.database.HistoryDB
import com.example.rksishedule.utils.Lesson
import com.example.rksishedule.utils.Shedule
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import org.jsoup.select.Elements
import java.time.LocalDate
import kotlin.Exception

var typeOfWeek = ""
var typeOfDay = "common"
val sheduleDelimiter = ";;;"
val trueDays = mutableSetOf<String>()
val currentDaysDB = mutableListOf<SheduleDB>()
val weekDays = listOf("понедельник", "вторник", "среда", "четверг", "пятница", "суббота", "воскресенье")
val maxDays = hashMapOf("января" to 31, "февраля" to 28, "марта" to 31, "апреля" to 30, "мая" to 31, "июня" to 30, "июля" to 31, "августа" to 31, "сентября" to 30, "октября" to 31, "ноября" to 30, "декабря" to 31)
val monthList = listOf("января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря")


class ParserSiteShedule {
}

suspend fun getTypeOfWeek(): String {
    val connectToSite = Jsoup.connect("https://www.rksi.ru/schedule")
    val htmlText = connectToSite.get()
    return Jsoup.clean(htmlText.select("strong")[1].toString(), Whitelist.none())
}

suspend fun getAllGroups(): MutableMap<String, String> {
    val connectToSite = Jsoup.connect("https://www.rksi.ru/schedule")
    val htmlText = connectToSite.get()
    val trashGroup = htmlText.getElementById("group").select("option")
    val groups = mutableMapOf<String, String>()
    trashGroup.forEach { el ->
        groups[el.`val`().toString()] = Jsoup.clean(el.toString(), Whitelist.none())
    }

    return  groups // Чистый список групп
}

fun getAllPreps() : MutableMap<String, String> {
    val connectToSite = Jsoup.connect("https://www.rksi.ru/schedule")
    val htmlText = connectToSite.get()
    val trashPrep = htmlText.getElementById("teacher").select("option")
    val preps = mutableMapOf<String, String>()
    trashPrep.forEach { el ->
        val name = Jsoup.clean(el.toString(), Whitelist.none())
        preps[el.`val`().toString()] = Jsoup.clean(el.toString(), Whitelist.none()).slice(0..name.length-2)
    }

    return  preps // Чистый список преподов
}

fun getSheduleOnThisDay(whatParse: String, name: String) : MutableList<String>{
    val shedule = mutableListOf<String>()
    val connectToSite = Jsoup.connect("https://www.rksi.ru/schedule")
    lateinit var trashResponseText: Elements
    if (whatParse == "group") {
        trashResponseText = connectToSite.data("group", name).data("stt", "Показать!").post().select("p,b")
    }
    if (whatParse == "teacher") {
        trashResponseText = connectToSite.data("teacher", name).data("stp", "Показать!").post().select("p,b")
    }

    val htmlText = trashResponseText.subList(1, trashResponseText.size-2)

    var days = false
    for(i in 1..<htmlText.size) {
        val item = htmlText[i]
        if (Regex("\\d ").find(item.select("b").toString()) != null) {
            if (days) break
            shedule.add(Jsoup.clean(item.select("b").toString(), Whitelist.none()))
            days = true
        }
        if (Jsoup.clean(item.select("p").toString(), Whitelist.none()) != typeOfWeek && Jsoup.clean(item.select("p").toString(), Whitelist.none()) != "") {
            shedule.add(item.select("p").toString())
        }
    }
    return shedule
}

suspend fun getWeekShedule(whatParse: String, name: String, context: Context) : MutableList<Shedule> {

    var weekSiteShedule = mutableListOf<Shedule>()
    val connectToSite = Jsoup.connect("https://www.rksi.ru/schedule")

    if (whatParse == "group") {
        val trashResponseText = connectToSite.data("group", name).data("stt", "Показать!").post().select("p,b")
        weekSiteShedule = parseSiteShedule(trashResponseText, context)
    }

    if (whatParse == "teacher") {
        val trashResponseText = connectToSite.data("teacher", name).data("stp", "Показать!").post().select("p,b")
        weekSiteShedule = parseSiteShedule(trashResponseText, context)

    }

    writeHistory(context, weekSiteShedule) // TODO ПРИ ОБНОВЛЕНИИ У МАМЫ ОТКЛЮЧАТЬ

    return weekSiteShedule
}

suspend fun parseSiteShedule(trashResponseText: Elements, context: Context) : MutableList<Shedule> { // парсит расписание сущности и возвращает словарь вида ЧИСЛО:[пара1, пара2...]
    val weekSiteShedule = mutableListOf<Shedule>()
    val htmlText = trashResponseText.subList(1, trashResponseText.size-2)
    val lessonsOfDay = mutableListOf<Lesson>()
    var n = 0
    var date = ""
    for(i in 1..<htmlText.size) {
        val item = htmlText[i]

        if (Regex("\\d ").find(item.select("b").toString()) != null) {
            date = Jsoup.clean(item.select("b").toString(), Whitelist.none()) // можно брать и пихать (тут даты типааа) 24 сентября, вторник
        }

        if (item.select("p").toString() == "" && htmlText[i+1].select("p").toString() == "" || i+1 == htmlText.size) {
            if (Jsoup.clean(item.select("p").toString(), Whitelist.none()) != "") {
                lessonsOfDay.add(parseLessons(item.select("p").toString(), date))            }
            weekSiteShedule.add(Shedule(normalizeDay(lessonsOfDay, context).toMutableList()))

            lessonsOfDay.clear()
            n++
        }
        else {
            if (Jsoup.clean(item.select("p").toString(), Whitelist.none()) != typeOfWeek && Jsoup.clean(item.select("p").toString(), Whitelist.none()) != "") {
                lessonsOfDay.add(parseLessons(item.select("p").toString(), date))            }
        }
    }
    return normalizeWeek(context, weekSiteShedule.toMutableList())
}

suspend fun parseLessons(les: String, date: String): Lesson { // вернёт {08:00 — 09:30=Web-программирование;;;Задорожный К.А;;;325}
    val lesson = Lesson()
    val day = date.split(", ")[1]
    if (les.isNotEmpty()) {
        val s = Jsoup.clean(les.split("<br><b>")[1].replace("</b><br>", sheduleDelimiter).replace("., ауд. ", sheduleDelimiter).replace("/1", ""), Whitelist.none())
        lesson.setDay(date)
        if (date.contains("понедельник")) {
            when(les.split("<br><b>")[0].replace("<p>", "")) {
                "13:10 — 14:40" -> lesson.setTimeLesson("14:10 — 15:40")
                "15:00 — 16:30" -> lesson.setTimeLesson("16:00 — 17:30")
                "16:40 — 18:10" -> lesson.setTimeLesson("17:40 — 19:10")
                else -> lesson.setTimeLesson(les.split("<br><b>")[0].replace("<p>", ""))
            }

        }
        else {
            lesson.setTimeLesson(les.split("<br><b>")[0].replace("<p>", ""))
        }
        lesson.setLesson(s.split(sheduleDelimiter)[0])
        typeOfDay = "common"
        lesson.setLessonNumber(day, lesson.getTimeLesson())
        if (lesson.getTimeLesson() == lessonsTimeInShortDay(lesson.getLessonNumber())) {
            typeOfDay = "short"
        }
        try {
            lesson.setEntity(s.split(sheduleDelimiter)[1])
            lesson.setAudience("ауд. "+s.split(sheduleDelimiter)[2])
        }catch (e: Exception){
            try {
                lesson.setEntity(s.split(sheduleDelimiter)[1].split(", ауд.")[0])
                lesson.setAudience("ауд. "+s.split(sheduleDelimiter)[1].split(", ауд.")[1])
            }
            catch (e: Exception) {
                lesson.setEntity("")
                lesson.setAudience("ауд. ")
            }
        }

    }

    return lesson
}


suspend fun normalizeDay(dayShedule: MutableList<Lesson>, context: Context) : MutableList<Lesson> {
    val improveDay = dayShedule
    val currentLessons = mutableListOf<Int>()
    val normalDay = mutableListOf<Lesson>()
    var doubleLessonOne = Lesson()
    var j = 0
    var n = 1

    dayShedule.forEach {
        if (it.getLessonNumber().toInt() !in currentLessons) {
            currentLessons.add(it.getLessonNumber().toInt())
        }
        else {
            doubleLessonOne = it
        }
    }

    while (currentLessons.size < 7) {
        if (n !in currentLessons) {
            val invizeLesson = Lesson()
            invizeLesson.setInvizeLessonNumber(n.toString())
            invizeLesson.setDay(dayShedule[0].getDay())
            invizeLesson.setLesson(inviseLessonName)
            improveDay.add(invizeLesson)
            currentLessons.add(n)
        }

        n++
    }
    n = 1

    for (i in 0..< improveDay.size) {
        while (improveDay[j].getLessonNumber() != n.toString()) {
            j++
        }
        normalDay.add(improveDay[j])
        n++
        j = 0
        if (n > 7) break
    }

    if(normalDay[3].getDay().split(", ")[1] == "понедельник"){
        typeOfDay = "monday"
    }
    if (normalDay[3].getLesson().isEmpty() && normalDay[3].getDay().split(", ")[1] == "понедельник" || normalDay[3].getLesson() == inviseLessonName && normalDay[3].getDay().split(", ")[1] == "понедельник") {
        normalDay[3].setLesson("Классный час")
        normalDay[3].setTimeLesson("13:05 — 14:05")
        normalDay[3].setEntity(" ")
        normalDay[3].setAudience("ауд. ")
        typeOfDay = "monday"
    }


    if (doubleLessonOne.getLesson().isNotEmpty()) {
        val doubleLessonTwo = normalDay[doubleLessonOne.getLessonNumber().toInt()-1]
        doubleLessonTwo.setAudience("${doubleLessonTwo.getAudience()}/\n${doubleLessonOne.getAudience()}")
        doubleLessonTwo.setEntity("${doubleLessonTwo.getEntity()}/\n${doubleLessonOne.getEntity()}")
    }

    val dbConnection = DBconnect(context)
    val dbShedule = dbConnection.getAllFromShedule()
    normalDay.forEach { lesson ->
        var bug1 = false // Если полностью не просматривает базу, тогда бывают проблемы. От просто break пришлось отказаться. Ввожу переменную т.к если на одной поре есть null и не null они конфликтуют и хз кто победит.
        var bug2 = false
        trueDays.add(lesson.getDay().split(",")[0])

        if (lesson.getLesson() != inviseLessonName) {
            val allLessons = mutableListOf<List<String>>()
            dbConnection.getAllLessonsFromDB().forEach {
                allLessons.add(listOf(it.lesson_name, it.entity_name))
            }

            val newLesson = LessonsDB(0, currentEntity, lesson.getLesson())
//            kotlin.io.println("${newLesson.lesson_name} !in $allLessonsLessons && ${newLesson.entity_name} !in $allLessonsEntities")
            if (listOf(newLesson.lesson_name, newLesson.entity_name) !in allLessons) {
                println(newLesson)
                dbConnection.insertLesson(LessonsDB(0, currentEntity, lesson.getLesson()))
            }

        }

        if (normalDay[3].getDay().split(", ")[1] == "воскресенье") typeOfDay = "common"
        when (typeOfDay) {
            "common" -> if (lesson.getTimeLesson().isEmpty()) lesson.setTimeLesson(
                lessonsTimeInCommonDay(lesson.getLessonNumber())
            )
            "short"-> if (lesson.getTimeLesson().isEmpty()) lesson.setTimeLesson(
                lessonsTimeInShortDay(lesson.getLessonNumber())
            )
            "monday"-> if (lesson.getTimeLesson().isEmpty()) lesson.setTimeLesson(
                lessonsTimeInMondayDay(lesson.getLessonNumber())
            )
            "uncommon1"-> if (lesson.getTimeLesson().isEmpty()) lesson.setTimeLesson(
                lessonsInUnCommonDay1(lesson.getLessonNumber())
            )
            "uncommon2"-> if (lesson.getTimeLesson().isEmpty()) lesson.setTimeLesson(
                lessonsInUnCommonDay2(lesson.getLessonNumber())
            )
        }
        if (normalDay[3].getLesson() == "Классный час") {
            val currentLesson = lesson.getLessonNumber().toInt()
            if (currentLesson == 4) {
                lesson.setInvizeLessonNumber("")
            }
            if (currentLesson > 4) {
                lesson.setInvizeLessonNumber("${currentLesson-1}")
            }

        }

        for (dbLesson in dbShedule) {
            currentDaysDB.add(dbLesson)
            if (lesson.getDay().split(",")[0] == dbLesson.date!!.split(",")[0] && lesson.getLessonNumber() == dbLesson.lessonNumber && dbLesson.editEntity == currentEntity) {
                if (dbLesson.lesson == inviseLessonName) bug1 = true
                if (dbLesson.lesson != inviseLessonName) bug2 = true

                if (bug1 != bug2) {
                    lesson.setEditLesson()
                    lesson.setLesson(dbLesson.lesson.toString())
                    lesson.setEntity(dbLesson.entity.toString())
                }
                else {
                    lesson.setLesson(dbLesson.lesson.toString())
                    lesson.setEntity(dbLesson.entity.toString())
                }
                if (dbLesson.aud.toString() != "ауд. -1") lesson.setAudience(dbLesson.aud.toString())

            }
        }
    }
    return normalDay
}

suspend fun normalizeWeek(context: Context, trashWeek : MutableList<Shedule>) : MutableList<Shedule> {
    val weekShedule = mutableListOf<Shedule>()

    var currentDay = weekDays.indexOf(functionsApp.getCurrentDayOfWeek())
    var newDay = newDay(LocalDate.now().dayOfMonth-1, monthList[LocalDate.now().monthValue-1])


    for (i in 0..<trashWeek.size) {
        val dayName = weekDays.indexOf(trashWeek[i].getDays()[0].split(", ")[1])

        while (true) {
//            val emptyDay = createEmptyDay(1, newDay, currentDay)
            if (currentDay == 6) {
                val emptyDay = createEmptyDay(context, newDay, currentDay)
//                emptyDay.setDay("${newDay[0]} ${monthList[newDay[1]]}, ${weekDays[currentDay]}")
                weekShedule.add(emptyDay)
                newDay = newDay(newDay[0], monthList[newDay[1]])
                currentDay = 0
            }
            if (dayName == currentDay) {
                val emptyDay = createEmptyDay(context, newDay, currentDay)
//                emptyDay.setDay("${newDay[0]} ${monthList[newDay[1]]}, ${weekDays[currentDay]}")
                weekShedule.add(emptyDay)
                newDay = newDay(newDay[0], monthList[newDay[1]])
                currentDay++
                break
            }
            else {
                val emptyDay = createEmptyDay(context, newDay, currentDay)
//                emptyDay.setDay("${newDay[0]} ${monthList[newDay[1]]}, ${weekDays[currentDay]}")
                weekShedule.add(emptyDay)
            }
            newDay = newDay(newDay[0], monthList[newDay[1]])
            currentDay++
        }
    }

    while (weekShedule[weekShedule.size-1].getDays()[0].split(", ")[1] != "воскресенье") {
        val emptyDay = createEmptyDay(context, newDay, currentDay)
//        emptyDay.setDay("${newDay[0]} ${monthList[newDay[1]]}, ${weekDays[currentDay]}")
        weekShedule.add(emptyDay)
        newDay = newDay(newDay[0], monthList[newDay[1]])
        currentDay++
    }

    for (i in 0..<weekShedule.size) {
        for (j in 0..<trashWeek.size) {
            if (j <= i) {
                if (weekShedule[i].getDays()[0] == trashWeek[j].getDays()[0]) {
                    weekShedule[i] = trashWeek[j]
                }
            }
            else break
        }
    }

    return weekShedule
}


suspend fun newDay(day: Int, month: String) : List<Int> {
    val newDay = listOf(day+1, monthList.indexOf(month))

    when {
        month == "декабря" -> {
            if (day == maxDays[month]) return listOf(1, 0)
            else {
                return newDay
            }
        }
        month == "февраля" -> {
            if (LocalDate.now().year % 4 == 0 && (LocalDate.now().year % 100 != 0 || LocalDate.now().year % 400 == 0) && (month == "февраля")) {
                if (day == maxDays[month]!!+1) return listOf(1, monthList.indexOf(month)+1)
                else {
                    return newDay
                }
            }
            else {
                if (day == maxDays[month]) return listOf(1, monthList.indexOf(month)+1)
                else {
                    return newDay
                }
            }
        }
        else -> {
            if (day == maxDays[month]) return listOf(1, monthList.indexOf(month)+1)
            else {
                return newDay
            }
        }
    }

}

suspend fun createEmptyDay(context: Context, newDay: List<Int>, currentDay: Int) : Shedule {
    val emptyDay = Shedule(mutableListOf())
    val emptyLessons = mutableListOf<Lesson>()
    for (i in 1..7) {
        val emptyLesson = Lesson()
        emptyLesson.setDay("${newDay[0]} ${monthList[newDay[1]]}, ${weekDays[currentDay]}")
        emptyLesson.setLesson(inviseLessonName)
        emptyLesson.setAudience(" ")
        emptyLesson.setEditLesson()
        emptyLesson.setInvizeLessonNumber(i.toString())
        emptyLesson.setEntity(" ")
//        when (typeOfDay) {
//            "common" -> emptyLesson.setTimeLesson(
//                lessonsTimeInCommonDay(i.toString()))
//            "short"-> emptyLesson.setTimeLesson(
//                lessonsTimeInShortDay(i.toString()))
//            "monday"-> emptyLesson.setTimeLesson(
//                lessonsTimeInMondayDay(i.toString()))
//            "uncommon1"-> emptyLesson.setTimeLesson(
//                lessonsInUnCommonDay1(emptyLesson.getLessonNumber()))
//
//        }
        emptyLessons.add(emptyLesson)
    }
    emptyDay.setLessons(normalizeDay(emptyLessons, context).toMutableList())

    return emptyDay
}



suspend fun clearTrashFromDB(context: Context) {
    val dbConnection = DBconnect(context)
    for (i in 0 ..< currentDaysDB.size){
        if (currentDaysDB[i].date!!.split(",")[0] !in trueDays) {
            dbConnection.deleteDayFromDB(currentDaysDB[i])
        }
    }
}


suspend fun writeHistory(context: Context, week: MutableList<Shedule>) {

    var ent = ""
    if (!entity.contains("[0-9]".toRegex())) {
        ent = allTrashTeachers[currentEntity].toString()
    }
    else {
        ent = allTrashGroups[currentEntity].toString()
    }
    println(mainEntity+", "+ent+", "+ currentEntity+", "+ entity)
    if (mainEntity == ent) {
        val db = DBconnect(context)
//        db.getAllHistoryFromDB().forEach {
//            if (it.id in 64..245 ) {
//                db.deleteHistory(it.id!!)
//            }
//        }
        val allHistory = db.getAllHistoryFromDB()
        var history = allHistory
//        allHistory.forEach {
//            println(it)
//        }
        val daysHistory = history.slice(1..history.size step 7).map{ it.date }
        val daysWeek = week.map { it.getDays()[0] }
//        println("history $daysHistory\nweek $daysWeek")


        if (allHistory.isNotEmpty()) history = allHistory.slice(allHistory.size-daysHistory.toSet().intersect(daysWeek.toSet()).size*7..<allHistory.size)
//        val dates = history.map { it.date }
//        println("hsiz ${daysHistory.size}\nwsiz ${daysWeek.size}\nosiz ${daysHistory.intersect(daysWeek).size}\nslise ${dates.toSet()}")


        var k = 0
        week.forEach {
            for (i in 0..<it.getLessons().size) {
//                println("k $k")
                if (history.isNotEmpty()) {

                    if (k < history.size) {
                        var dbRow = history[k]

                        if (dbRow.date == it.getDays()[i]) {
//                            println("${dbRow.date}/if/ ${it.getDays()[i]}")
                            when {
                                it.getLessons()[i] == inviseLessonName && dbRow.lesson != inviseLessonName -> {
                                    db.updateHistoryOnID(dbRow.id!!, it.getEditLessons()[i], it.getLessons()[i], dbRow.lesson!!, it.getEntityes()[i], it.getAudiencies()[i], it.getTimeLessons()[i])
                                }
                                it.getLessons()[i] != inviseLessonName -> {
                                    db.updateHistoryOnID(dbRow.id!!, it.getEditLessons()[i], it.getLessons()[i], it.getLessons()[i], it.getEntityes()[i], it.getAudiencies()[i], it.getTimeLessons()[i])
                                }
                                dbRow.lesson != inviseLessonName -> {
                                    db.updateHistoryOnID(dbRow.id!!, it.getEditLessons()[i], dbRow.lesson!!, dbRow.lesson!!, it.getEntityes()[i], it.getAudiencies()[i], it.getTimeLessons()[i])
                                }
                            }
//                            println(db.getAllHistoryFromDB()[k])
                        }
                        else {
//                            println("${dbRow.date}/else/ ${it.getDays()[i]}")
                            db.insertHistory(it.toHistory(null, i, history[i].deleteLessonName.toString()))
                        }
                    }
                    else {
                        db.insertHistory(it.toHistory(null, i, it.getLessons()[i]))
                    }

                }
                else {
                    db.insertHistory(it.toHistory(null, i, it.getLessons()[i]))
                }
                k++
            }
        }

//        db.getAllHistoryFromDB().forEach { println(it) }

    }



}


fun lessonsTimeInCommonDay(lessonNumber: String): String {
    lateinit var lessonTime: String
    when(lessonNumber) {
        "1" -> lessonTime = "08:00 — 09:30"
        "2" -> lessonTime = "09:40 — 11:10"
        "3" -> lessonTime = "11:30 — 13:00"
        "4" -> lessonTime = "13:10 — 14:40"
        "5" -> lessonTime = "15:00 — 16:30"
        "6" -> lessonTime = "16:40 — 18:10"
        "7" -> lessonTime = "18:20 — 19:50"
    }
    return lessonTime
}

fun lessonsInUnCommonDay1(lessonNumber: String): String {
    lateinit var lessonTime: String
    when(lessonNumber) {
        "1" -> lessonTime = "08:00 — 09:30"
        "2" -> lessonTime = "09:40 — 11:10"
        "3" -> lessonTime = "11:30 — 12:20"
        "4" -> lessonTime = "12:30 — 13:20"
        "5" -> lessonTime = "13:30 — 14:20"
        "6" -> lessonTime = "14:30 — 15:20"
        "7" -> lessonTime = "15:30 — 16:30"
    }
    return lessonTime
}

fun lessonsInUnCommonDay2(lessonNumber: String): String {
    lateinit var lessonTime: String
    when(lessonNumber) {
        "1" -> lessonTime = "08:00 — 09:30"
        "2" -> lessonTime = "09:40 — 11:10"
        "3" -> lessonTime = "11:30 — 13:00"
        "4" -> lessonTime = "13.10 — 14.00"
        "5" -> lessonTime = "14.10 — 15.00"
        "6" -> lessonTime = "15.10 — 16.00"
        "7" -> lessonTime = "16.10 — 17.00"
    }
    return lessonTime
}


fun lessonsTimeInShortDay(lessonNumber: String): String {
    lateinit var lessonTime: String
    when (lessonNumber) {
        "1" -> lessonTime = "08:00 — 08:50"
        "2" -> lessonTime = "09:00 — 09:50"
        "3" -> lessonTime = "10:00 — 10:50"
        "4" -> lessonTime = "11:00 — 11:50"
        "5" -> lessonTime = "12:00 — 12:50"
        "6" -> lessonTime = "13:00 — 13:50"
        "7" -> lessonTime = "14:00 — 14:50"
    }
    return lessonTime
}

fun lessonsTimeInMondayDay(lessonNumber: String): String {
    lateinit var lessonTime: String
    when (lessonNumber) {
        "1" -> lessonTime = "08:00 — 09:30"
        "2" -> lessonTime = "09:40 — 11:10"
        "3" -> lessonTime = "11:30 — 13:00"
        "4" -> lessonTime = "13:05 — 14:05"
        "5" -> lessonTime = "14:10 — 15:40"
        "6" -> lessonTime = "16:00 — 17:30"
        "7" -> lessonTime = "17:40 — 19:10"
    }
    return lessonTime
}



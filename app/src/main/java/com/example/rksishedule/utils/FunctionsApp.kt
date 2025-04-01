package com.example.rksishedule.utils

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.example.rksishedule.R
import com.example.rksishedule.activities.DifferentShedules
import com.example.rksishedule.activities.EditLesson
import com.example.rksishedule.activities.HistoryShedule
import com.example.rksishedule.activities.Planshet
import com.example.rksishedule.activities.Settings
import com.example.rksishedule.data.DBconnect
import com.example.rksishedule.data.currentEntity
import com.example.rksishedule.data.entity
import com.example.rksishedule.data.inviseLessonName
import com.example.rksishedule.data.nightMode
import com.example.rksishedule.database.SheduleDB
import com.example.rksishedule.parser.monthList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.collections.set
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes


class FunctionsApp: AppCompatActivity() {


    fun showMainPopup(context: Context, view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.main_popup, popupMenu.menu)
        popupMenu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(popupMenu,true)
        popupMenu.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.title) {
                ContextCompat.getString(context, R.string.planshet) -> {
                    val i = Intent(context, Planshet::class.java)
                    context.startActivity(i)
                }

                ContextCompat.getString(context, R.string.different_shedules) -> {
                    val i = Intent(context, DifferentShedules::class.java)
                    context.startActivity(i)
                }

                ContextCompat.getString(context, R.string.turtle) -> {
                    showTurtleItemPopup(context)
                }

                ContextCompat.getString(context, R.string.history) -> {
                    if (entity != "null") {
                        val i = Intent(context, HistoryShedule::class.java)
                        context.startActivity(i)
                    }
                }

                ContextCompat.getString(context, R.string.settings) -> {
                    val i = Intent(context, Settings::class.java)
                    context.startActivity(i)
                }
            }
            true
        }
        popupMenu.show()
    }

    fun showPopupItem(context: Context, rvData: Shedule, position: Int) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popup_item) // Указать ваш кастомный layout
        dialog.show()

        val close = dialog.findViewById<ImageButton>(R.id.b_close)
        val menu = dialog.findViewById<ImageView>(R.id.b_menu)
        val lessonNumber = dialog.findViewById<TextView>(R.id.tv_lesson_number)
        val lessonT = dialog.findViewById<TextView>(R.id.tv_lesson)
        val dayT = dialog.findViewById<TextView>(R.id.tv_day)
        val timeT = dialog.findViewById<TextView>(R.id.tv_time)
        val entityT = dialog.findViewById<TextView>(R.id.tv_entity)
        val audT = dialog.findViewById<TextView>(R.id.tv_aud)

        lessonNumber.text = "пара ${rvData.getLessonsNumbers()[position]}"
        lessonT.text = rvData.getLessons()[position]
        dayT.text = rvData.getDays()[position]
        timeT.text = rvData.getTimeLessons()[position]
        entityT.text = rvData.getEntityes()[position]
        audT.text = rvData.getAudiencies()[position]


        close.setOnClickListener { dialog.dismiss() }

        menu.setOnClickListener {
            showNestedItemPopup(context, menu, rvData, position, dialog)
        }
    }

    fun showHistoryPopupItem(context: Context, rvData: SheduleHistory, position: Int) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.history_popup_item) // Указать ваш кастомный layout
        dialog.show()

        val close = dialog.findViewById<ImageButton>(R.id.b_close)
        val lessonNumber = dialog.findViewById<TextView>(R.id.tv_lesson_number)
        val lessonT = dialog.findViewById<TextView>(R.id.tv_lesson)
        val dayT = dialog.findViewById<TextView>(R.id.tv_day)
        val timeT = dialog.findViewById<TextView>(R.id.tv_time)
        val entityT = dialog.findViewById<TextView>(R.id.tv_entity)
        val audT = dialog.findViewById<TextView>(R.id.tv_aud)

        lessonNumber.text = "пара ${rvData.getLessonsNumbers()[position]}"
        lessonT.text = rvData.getDeleteLessons()[position]
        dayT.text = rvData.getDays()[position]
        timeT.text = rvData.getTimeLessons()[position]
        entityT.text = rvData.getEntityes()[position]
        audT.text = rvData.getAudiencies()[position]


        close.setOnClickListener { dialog.dismiss() }
    }

    fun showNestedItemPopup(context: Context, view: View, rvData: Shedule, position: Int, mainDialog: Dialog) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.nested_item_popup, popupMenu.menu)
        popupMenu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(popupMenu,true)

        popupMenu.setOnMenuItemClickListener { item: MenuItem? ->

            when (item!!.title) {
                ContextCompat.getString(context, R.string.edit_card) -> {

                    val i = Intent(context, EditLesson::class.java)
                    i.putExtra("lessonNumber", rvData.getLessonsNumbers()[position])
                    i.putExtra("lesson", rvData.getLessons()[position])
                    i.putExtra("day", rvData.getDays()[position])
                    i.putExtra("entity", rvData.getEntityes()[position])
                    i.putExtra("aud", rvData.getAudiencies()[position])
                    mainDialog.dismiss()
                    context.startActivity(i)

                }

                ContextCompat.getString(context, R.string.delete_card) -> {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle("Удалить")
                    builder.setMessage("Вы уверены, что хотите удалить эту пару?")
                    builder.setPositiveButton(
                        "Да"
                    ) { dialog, id ->
                        val db = DBconnect(context)
                        CoroutineScope(Dispatchers.IO).launch {
                            db.insertShedule(
                                SheduleDB(
                                    null,
                                    true,
                                    rvData.getDays()[position],
                                    rvData.getLessonsNumbers()[position],
                                    inviseLessonName,
                                    "",
                                    "ауд. -1",
                                    currentEntity
                                )
                            )
                            runOnUiThread{mainDialog.dismiss()}

                        }
                    }
                    builder.setNegativeButton(
                        "Нет"
                    ) { dialog, id ->
                        Toast.makeText(context, "Отмена", Toast.LENGTH_SHORT)
                            .show()
                    }
                    builder.show()
                }
            }
            true
        }

        popupMenu.show()
    }

    fun showTurtleItemPopup(context: Context) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popup_edit_shedule_with_turtle)
        dialog.show()

        val turtleText = dialog.findViewById<EditText>(R.id.message)
        val bTurtleEdit = dialog.findViewById<Button>(R.id.use_change)
        val months = hashMapOf("01" to "января", "02" to "февраля", "03" to "марта", "04" to "апреля", "05" to "мая", "06" to "июня", "07" to "июля", "08" to "августа", "09" to "сентября", "10" to "октября", "11" to "ноября", "12" to "декабря")
        val multiString = """
14.01.2025 Пара 5 ИС-37
Вместо Разработка мобильных приложений (Вакансия) будет Физическая культура (Швачич Д.С.).

Пара 6
ИС-37 пары "Программирование на платформе 1С" (Вакансия) не будет.

Пара 7
ИС-37 пары "Программирование на платформе 1С" (Вакансия) не будет.
""".trimIndent()




        bTurtleEdit.setOnClickListener {
            try {
                val turtleTextClean = turtleText.text.toString().replace(" на  на ", " на ")
                val caption = turtleTextClean.split("\n")[0]
                val content = turtleTextClean.split("Пара ")

                var day = caption.split(" ")[0]
                day = "${if (day[0] == '0') day.split(".")[0].slice(1..1) else day.split(".")[0]} ${months[day.split(".")[1]]!!}"
                val lessonNumber = mutableListOf<String>()
                val newLesson = mutableListOf<String>()
                val newPrep = mutableListOf<String>()
                val group = try {
                    caption.split(" ")[3]
                } catch (e: Exception) {
                    content[1].split(" ")[0]
                }


                for (i in 1..<content.size) {
                    val lesson = content[i].split("\n")

//        println("$lesson\n$day\n$group\n${caption[i]}\n${content[i]}")
                    if (!lesson[1].contains(" не будет") || lesson[1].contains(" паре.")) {
                        val lnumber = Regex("\\d")
                        val les = Regex("((?<= будет ).*?(?= \\())")
                        val prep = Regex("((?<=\\().*?(?=\\)))")

                        if (lesson[1].contains(" паре.")) {
                            lessonNumber.add(lnumber.find(lesson[1].split(".) на ")[1])!!.value)
                            newLesson.add(les.find(lesson[1])!!.value)
                            newPrep.add(prep.findAll(lesson[1]).last().value)

                            lessonNumber.add(lesson[0].slice(0..0))
                            println("${lesson[0].slice(0..0)} пары - не будет")
                            newLesson.add(inviseLessonName)
                            newPrep.add(inviseLessonName)
                        } else {
                            lessonNumber.add(lesson[0].slice(0..0))
                            if (lesson[1].contains("Будет")) {
                                val les = Regex("((?<=\\().*?(?=\\)))")
                                val prep = Regex("((?<=Будет пара с ).*?(?=\\())")
                                newLesson.add(les.find(lesson[1])!!.value)
                                newPrep.add(prep.find(lesson[1])!!.value)
                            } else {
                                newLesson.add(les.find(lesson[1])!!.value)
                                newPrep.add(prep.findAll(lesson[1]).last().value)
                            }
                        }

                    }
                    else {
                        lessonNumber.add(lesson[0].slice(0..0))
                        println("${lesson[0].slice(0..0)} пары - не будет")
                        newLesson.add(inviseLessonName)
                        newPrep.add(inviseLessonName)
                    }
                }
                println("$lessonNumber\n$newLesson\n$newPrep")
                if (lessonNumber.size == newLesson.size && lessonNumber.size == newPrep.size) {
                    for (i in 0..<lessonNumber.size) {
                        val db = DBconnect(context)
                        CoroutineScope(Dispatchers.IO).launch {
                            Log.d("in base", "null, true, $day, ${lessonNumber.elementAt(i)}, ${newLesson.elementAt(i)}, ${newPrep.elementAt(i)}, \"ауд. -1\", $group)")
                            db.insertShedule(SheduleDB(null, true, day, lessonNumber.elementAt(i), newLesson.elementAt(i), newPrep.elementAt(i), "ауд. -1", group))
                        }
                    }
////                    [3, 3, 4, 4, 5, 6] примерный вывод
////                    [Иностранный язык в профессиональной деятельности, Иностранный язык в профессиональной деятельности, Иностранный язык в профессиональной деятельности, Иностранный язык в профессиональной деятельности, Системное программирование, delete]
////                    [Лебедева М.В., Прыгунова Т.А., Лебедева М.В., Прыгунова Т.А., Кошкина А.А., delete]
                    dialog.hide()
                }
                else {
                    println("else")
                    throw Exception()
                }
            }
            catch (e: Exception) {
//                turtleText.setText(e.toString())
                Toast.makeText(context, "Не удалось обработать соообщение", Toast.LENGTH_SHORT).show()
            }

        }
    }


    fun isNightMode(context: Context): Boolean {
        val nightModeFlags =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }



    fun selectEntityToast(context: Context) {
        Toast.makeText(context, "Выберите расписание по умолчанию в настройках", Toast.LENGTH_SHORT).show()
    }

    fun ToastRVerror(context: Context, position: Int) {
        Toast.makeText(context, "Can't create bind holder from position $position", Toast.LENGTH_SHORT).show()
    }

    fun refreshPage(intent: Intent) {
        finish()
        startActivity(intent) //startActivity(getIntent())
    }


    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).roundToInt()
    }

    fun countOccurrences(str: String, searchStr: String): Int {
        var count = 0
        var startIndex = 0

        while (startIndex < str.length) {
            val index = str.indexOf(searchStr, startIndex)
            if (index >= 0) {
                count++
                startIndex = index + searchStr.length
            } else {
                break
            }
        }

        return count
    }

    fun getCurrentDayOfWeek(): String {
        val currentDay = LocalDate.now()
        val dayOfWeek = currentDay.dayOfWeek
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ru", "RU"))
    }

    fun getCurrentDayOfWeek(date: String): String {
        val currentDay = LocalDate.parse("${date.split(".")[2]}-${date.split(".")[1]}-${date.split(".")[0]}")
        val dayOfWeek = currentDay.dayOfWeek
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ru", "RU"))
    }

    fun showPopupTimeNotify(context: Context, recyclerData: Shedule, i: Int) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.popup_time_notify) // Указать ваш кастомный layout
        dialog.show()

        val close = dialog.findViewById<ImageButton>(R.id.b_close)
        val state = dialog.findViewById<Switch>(R.id.state)
        val lessons = dialog.findViewById<MySpinner>(R.id.s_lessons)
        val time = dialog.findViewById<TextView>(R.id.tv_time)
        val seekTimeChanger = dialog.findViewById<SeekBar>(R.id.time_changer)
        val message  = dialog.findViewById<EditText>(R.id.et_message)
        val b_useChanges = dialog.findViewById<Button>(R.id.b_use_change)

        var timeStartLesson = ""
        val realLessons = realLessons(recyclerData)
        if (nightMode) {
            lessons.setPopupBackgroundResource(R.color.transparent_black_85)
        }

        val adapterThirdFA =
            ArrayAdapter(context, R.layout.spinner_lesson_popup_item, realLessons.keys.map { it.toString() })
        adapterThirdFA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        lessons.adapter = adapterThirdFA
        lessons.setOnItemSelectedEvenIfUnchangedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                timeStartLesson = realLessons[position]!!
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

        seekTimeChanger.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    time.text = normTime(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    //TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    //TODO("Not yet implemented")
                }

            }
        )

        b_useChanges.setOnClickListener {
            // TODO Плююсь в базу
        }

        close.setOnClickListener { dialog.dismiss() }
    }

    private fun realLessons(shedule: Shedule): HashMap<Int, String> {
        val realLes = HashMap<Int, String>()
        shedule.getLessons().forEachIndexed { j, it ->
            if (it != inviseLessonName) {
                realLes[j] = shedule.getTimeLessons()[j]
            }
        }
        return realLes
    }

    fun normTime(time: Int): String {
        var time = time
        var h = 0
        var m = 0
        while (time > 59) {
            h+=1
            time-=60
        }
        m = time
        if (m.toString().length < 2) {
            return "0$h:0$m"
        }
        else {
            return "0$h:$m"
        }
    }

    fun toLetters(s: String): String {
        val sepDate = s.split(".")
        val sepTime = sepDate[3].split(":")
        return "${sepDate[0]} ${sepDate[2]} ${monthList[sepDate[1].toInt()]} ${sepTime[0]}:${sepTime[1]}"
    }

    fun fromLetters(s: String): String {
        val sepDate = s.split(" ")
        val sepTime = sepDate[3].split(":")
        return "${sepDate[0]} ${monthList.indexOf(sepDate[2])} ${sepDate[1]} ${sepTime[0]}:${sepTime[1]}"
    }

    fun startAlarmTimer(context: Context, time: Int) {
        val alarmManager: AlarmManager = checkNotNull(context.getSystemService())
        val intent = Intent(context, CreateNotify::class.java)
        val alarmIntent1 = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                System.currentTimeMillis() + time.minutes.inWholeMilliseconds,
                alarmIntent1
            ),
            alarmIntent1
        )
    }

    fun cancelAlarmTimer(context: Context) {
        val alarmManager: AlarmManager = checkNotNull(context.getSystemService())
        val intent = Intent(context, CreateNotify::class.java)
        val alarmIntent1 = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(alarmIntent1)
    }


}


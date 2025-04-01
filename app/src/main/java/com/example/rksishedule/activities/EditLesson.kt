package com.example.rksishedule.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rksishedule.data.DBconnect
import com.example.rksishedule.utils.MySpinner
import com.example.rksishedule.R
import com.example.rksishedule.database.SheduleDB
import com.example.rksishedule.data.allGroups
import com.example.rksishedule.data.allTeachers
import com.example.rksishedule.data.currentEntity
import com.example.rksishedule.data.inviseLessonName
import com.example.rksishedule.data.nightMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EditLesson: AppCompatActivity() {

    // Структура:  day, lessonNumber, lesson, entity, aud
    var lessonInfo = mutableListOf<String>()
    var lessonNewInfo = mutableListOf<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_lesson)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit_lesson)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        CoroutineScope(Dispatchers.IO).launch {
            val lessonsNumbers = resources.getStringArray(R.array.lessons_numbers)
            var lessons = mutableListOf<String>()
            val firstFA = resources.getStringArray(R.array.first_floor_auds)
            val secondFA = resources.getStringArray(R.array.second_floor_auds)
            val thirdFA = resources.getStringArray(R.array.third_floor_auds)
            val fourthFA = resources.getStringArray(R.array.fourth_floor_auds)
            val otherFA = resources.getStringArray(R.array.other_auds)

            var entities = allTeachers

            val tAud = findViewById<TextView>(R.id.tv_aud)
            val bBack = findViewById<ImageButton>(R.id.b_back)
            val sNumberOfLesson = findViewById<MySpinner>(R.id.s_lessonNumber)
            val sLessons = findViewById<MySpinner>(R.id.s_lessons)
            val sEntity = findViewById<MySpinner>(R.id.s_entity)
            val sFirstFA = findViewById<MySpinner>(R.id.s_first_floor_auds)
            val sSecondFA = findViewById<MySpinner>(R.id.s_second_floor_auds)
            val sThirdFA = findViewById<MySpinner>(R.id.s_third_floor_auds)
            val sFourthFA = findViewById<MySpinner>(R.id.s_fourth_floor_auds)
            val sOtherFA = findViewById<MySpinner>(R.id.s_other_auds)

            if (nightMode) {
                sNumberOfLesson.setPopupBackgroundResource(R.color.transparent_black_85)
                sLessons.setPopupBackgroundResource(R.color.transparent_black_85)
                sEntity.setPopupBackgroundResource(R.color.transparent_black_85)
                sFirstFA.setPopupBackgroundResource(R.color.transparent_black_85)
                sSecondFA.setPopupBackgroundResource(R.color.transparent_black_85)
                sThirdFA.setPopupBackgroundResource(R.color.transparent_black_85)
                sFourthFA.setPopupBackgroundResource(R.color.transparent_black_85)
                sOtherFA.setPopupBackgroundResource(R.color.transparent_black_85)
            }

            tAud.text = intent.getStringExtra("aud")

            lessonInfo.add(intent.getStringExtra("day")!!)
            lessonInfo.add(intent.getStringExtra("lessonNumber")!!)
            lessonInfo.add(intent.getStringExtra("lesson")!!)
            lessonInfo.add(intent.getStringExtra("entity")!!)
            lessonInfo.add(intent.getStringExtra("aud")!!)


            lessons = DBconnect(applicationContext).findEntityLessons(currentEntity).toMutableList()
            if (lessonInfo[2] == inviseLessonName) lessons.add(0, inviseLessonName)
            runOnUiThread {
                    sNumberOfLesson.setSelection(lessonsNumbers.indexOf(lessonInfo[1]))
                    try {
                        sLessons.setSelection(lessons.indexOf(lessonInfo[2]))
                    }
                    catch (e: Exception) {
                        if (lessonInfo[2] != inviseLessonName) findViewById<TextView>(R.id.tv_lessons_is_empty).visibility = View.VISIBLE
                        else {
                            sLessons.setSelection(0)
                        }
                    }
                    //TODO добавить setSelection для аудитрии
            }
            if (!currentEntity.contains("[0-9]".toRegex())) {
                entities = allGroups
            }



                lessonNewInfo = lessonInfo.toMutableList()


//        val adapterLessons = ArrayAdapter(applicationContext, R.layout.spinner_item, list from BD) // Тут с БД передаются пары для группы выбранной по умолчанию
//        adapterLessons.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        sLessons.adapter = adapterLessons

//        val adapterEntity = ArrayAdapter(applicationContext, R.layout.spinner_item, list from BD) // Тут с БД передаются группы или преподы в зависимости от того выбранное расписание преподское или группы (проверка идёт по наличию цифры в entity карточки. Если цифра есть (за исключением Вак1 и.т.д) -> пихаю группы, если нет - преподов
//        adapterLessons.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        sLessons.adapter = adapterLessons

                val adapterNumberLessons = ArrayAdapter(
                    applicationContext,
                    R.layout.spinner_number_lesson_item,
                    lessonsNumbers
                )
                adapterNumberLessons.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sNumberOfLesson.adapter = adapterNumberLessons

                val adapterLessons =
                    ArrayAdapter(applicationContext, R.layout.spinner_item, lessons)
                adapterLessons.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sLessons.adapter = adapterLessons

                val adapterFirstFA =
                    ArrayAdapter(applicationContext, R.layout.spinner_item, firstFA)
                adapterFirstFA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sFirstFA.adapter = adapterFirstFA

                val adapterSecondFA =
                    ArrayAdapter(applicationContext, R.layout.spinner_item, secondFA)
                adapterSecondFA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sSecondFA.adapter = adapterSecondFA

                val adapterThirdFA =
                    ArrayAdapter(applicationContext, R.layout.spinner_item, thirdFA)
                adapterThirdFA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sThirdFA.adapter = adapterThirdFA

                val adapterFourthFA =
                    ArrayAdapter(applicationContext, R.layout.spinner_item, fourthFA)
                adapterFourthFA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sFourthFA.adapter = adapterFourthFA

                val adapterOtherFA =
                    ArrayAdapter(applicationContext, R.layout.spinner_item, otherFA)
                adapterOtherFA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sOtherFA.adapter = adapterOtherFA

                val adapterEntity =
                    ArrayAdapter(applicationContext, R.layout.spinner_item, entities)
                adapterEntity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sEntity.adapter = adapterEntity






                sNumberOfLesson.setOnItemSelectedEvenIfUnchangedListener(object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT)
                            .show()
                        lessonNewInfo[1] = lessonsNumbers[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                })

                sLessons.setOnItemSelectedEvenIfUnchangedListener(object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT)
                            .show()
                        lessonNewInfo[2] = lessons[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                })

                sFirstFA.setOnItemSelectedEvenIfUnchangedListener(object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT)
                            .show()
                        tAud.text = firstFA[position]
                        lessonNewInfo[4] = "ауд. " + firstFA[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                })

                sSecondFA.setOnItemSelectedEvenIfUnchangedListener(object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT)
                            .show()
                        tAud.text = secondFA[position]
                        lessonNewInfo[4] = "ауд. " + secondFA[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                })

                sThirdFA.setOnItemSelectedEvenIfUnchangedListener(object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT)
                            .show()
                        tAud.text = thirdFA[position]
                        lessonNewInfo[4] = "ауд. " + thirdFA[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                })

                sFourthFA.setOnItemSelectedEvenIfUnchangedListener(object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT)
                            .show()
                        tAud.text = fourthFA[position]
                        lessonNewInfo[4] = "ауд. " + fourthFA[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                })

                sOtherFA.setOnItemSelectedEvenIfUnchangedListener(object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT)
                            .show()
                        tAud.text = otherFA[position]
                        lessonNewInfo.add(4, "ауд. " + otherFA[position])
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                })

                sEntity.setOnItemSelectedEvenIfUnchangedListener(object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT)
                            .show()
                        lessonNewInfo.add(3, entities[position])
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {}
                })


                bBack.setOnClickListener {
                    finish()
                }


            }
    }

    override fun onDestroy() {
        super.onDestroy()
        var lessonIsEdit = false
        for (i in 0..<lessonInfo.size) {
            if (lessonInfo[i] != lessonNewInfo[i] && lessonNewInfo[i] != inviseLessonName) {
                lessonIsEdit = true
            }
            kotlin.io.println("$lessonIsEdit\n${lessonInfo[i]} = ${lessonNewInfo[i]}")
        }
        if (lessonIsEdit) {
            val db = DBconnect(applicationContext)
            CoroutineScope(Dispatchers.IO).launch {
                if (lessonInfo[1] != lessonNewInfo[1]) {
                    db.insertShedule(
                        SheduleDB(
                            null,
                            true,
                            lessonNewInfo[0],
                            lessonInfo[1],
                            inviseLessonName,
                            lessonNewInfo[3],
                            lessonNewInfo[4],
                            currentEntity
                        )
                    )
                }

                db.insertShedule(
                    SheduleDB(
                        null,
                        true,
                        lessonNewInfo[0],
                        lessonNewInfo[1],
                        lessonNewInfo[2],
                        lessonNewInfo[3],
                        lessonNewInfo[4],
                        currentEntity
                    )
                )

                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.edit_is_accepted),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            kotlin.io.println("Изменение")
        }

    }
}
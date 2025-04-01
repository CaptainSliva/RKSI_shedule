package com.example.rksishedule.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.edit
import androidx.room.Room
import com.example.rksishedule.data.DBconnect
import com.example.rksishedule.preferences.DataSettings
import com.example.rksishedule.preferences.DataSettingsKeys
import com.example.rksishedule.utils.MySpinner
import com.example.rksishedule.R
import com.example.rksishedule.data.allGroups
import com.example.rksishedule.data.allTeachers
import com.example.rksishedule.data.allTrashGroups
import com.example.rksishedule.data.allTrashTeachers
import com.example.rksishedule.data.dataStore
import com.example.rksishedule.data.entity
import com.example.rksishedule.data.functionsApp
import com.example.rksishedule.data.inviseLessonName
import com.example.rksishedule.data.nightMode
import com.example.rksishedule.data.sheduleLink
import com.example.rksishedule.database.AppDatabase
import com.example.rksishedule.database.GroupsDB
import com.example.rksishedule.database.TeachersDB
import com.example.rksishedule.parser.getAllGroups
import com.example.rksishedule.parser.getAllPreps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class Settings: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val spinnerGroups = findViewById<MySpinner>(R.id.s_groups)
        val spinnerTeachers = findViewById<MySpinner>(R.id.s_teachers)
        val tvInitialShedule = findViewById<TextView>(R.id.tv_initial_entity)
        val bBack = findViewById<ImageButton>(R.id.b_back)
        val bClearBaseShedule = findViewById<ImageButton>(R.id.b_clear_shedule_base)
        val bClearBaseLessons = findViewById<ImageButton>(R.id.b_clear_lessons_base)
        val bClearBaseHistory = findViewById<ImageButton>(R.id.b_clear_history_base)
        val bUpdateCatalogsEntities = findViewById<ImageButton>(R.id.b_update_entities)
        val etSheduleLink = findViewById<EditText>(R.id.et_shedule_link)
        val state = findViewById<Switch>(R.id.state)
        val seekTimeChanger = findViewById<SeekBar>(R.id.time_changer)
        val timeText = findViewById<TextView>(R.id.tv_time)
        val message = findViewById<EditText>(R.id.et_message)

        if (nightMode) {
            spinnerGroups.setPopupBackgroundResource(R.color.transparent_black_85)
            spinnerTeachers.setPopupBackgroundResource(R.color.transparent_black_85)
        }
        if (sheduleLink != inviseLessonName) etSheduleLink.setText(sheduleLink)
        tvInitialShedule.text =  "${getString(R.string.initial_entity)}:  $entity"

        CoroutineScope(Dispatchers.IO).launch {
            runOnUiThread {

                val adapterGroups = ArrayAdapter(applicationContext,
                    R.layout.spinner_item, allGroups
                )
                adapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerGroups.adapter = adapterGroups
                CoroutineScope(Dispatchers.IO).launch { selectEntity(spinnerGroups) }

                val adapterTeachers = ArrayAdapter(applicationContext,
                    R.layout.spinner_item, allTeachers
                )
                adapterTeachers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTeachers.adapter = adapterTeachers
                CoroutineScope(Dispatchers.IO).launch { selectEntity(spinnerTeachers) }
            }
        }


        spinnerGroups.setOnItemSelectedEvenIfUnchangedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val group = allGroups[position]
                    updateEntity(group)
                    runOnUiThread {
                        tvInitialShedule.text = "${getString(R.string.initial_entity)}:  $group"
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })


        spinnerTeachers.setOnItemSelectedEvenIfUnchangedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val teacher = allTeachers[position]
                    updateEntity(teacher)
                    runOnUiThread {
                        tvInitialShedule.text = "${getString(R.string.initial_entity)}:  $teacher"
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })

        bBack.setOnClickListener {
            finish()
        }

        bClearBaseShedule.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Удалить все изменения расписаний")
            builder.setMessage("Очистить базу?")
            builder.setPositiveButton(
                "Да"
            ) { dialog, id ->
                val db = DBconnect(this)
                CoroutineScope(Dispatchers.IO).launch {
                    db.clearShedule()
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Изменения расписаний удалены", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            builder.setNegativeButton(
                "Нет"
            ) { dialog, id -> Toast.makeText(applicationContext, "Отмена", Toast.LENGTH_SHORT).show() }
            builder.show()
        }

        bClearBaseLessons.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Удалить все названия пар")
            builder.setMessage("Очистить базу?")
            builder.setPositiveButton(
                "Да"
            ) { dialog, id ->
                val db = DBconnect(this)
                CoroutineScope(Dispatchers.IO).launch {
                    db.clearLessons()
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Названия пар удалены", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton(
                "Нет"
            ) { dialog, id -> Toast.makeText(applicationContext, "Отмена", Toast.LENGTH_SHORT).show() }
            builder.show()
        }

        bClearBaseHistory.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Удалить все пары в истории")
            builder.setMessage("Очистить базу?")
            builder.setPositiveButton(
                "Да"
            ) { dialog, id ->
                val db = DBconnect(this)
                CoroutineScope(Dispatchers.IO).launch {
                    db.clearHistory()
                    runOnUiThread {
                        Toast.makeText(applicationContext, "История пар удалена", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton(
                "Нет"
            ) { dialog, id -> Toast.makeText(applicationContext, "Отмена", Toast.LENGTH_SHORT).show() }
            builder.show()
        }

        bUpdateCatalogsEntities.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Обновить списки групп и преподавателей")
            builder.setMessage("Обновить базу?")
            builder.setPositiveButton(
                "Да"
            ) { dialog, id ->
                val db = DBconnect(this)
                CoroutineScope(Dispatchers.IO).launch {
                    CoroutineScope(Dispatchers.IO).launch {
                        db.clearGroups()
                        allTrashGroups = getAllGroups()
                        allTrashGroups.forEach {
                            allGroups.add(it.value)
                            db.insertGroup(GroupsDB(0, it.key, it.value))
                        }

                        db.clearTeachers()
                        allTrashTeachers = getAllPreps()
                        allTrashTeachers.forEach {
                            allTeachers.add(it.value)
                            db.insertTeacher(TeachersDB(0, it.key, it.value))
                        }
                    }
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Списки групп и преподавателей обновлены", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton(
                "Нет"
            ) { dialog, id -> Toast.makeText(applicationContext, "Отмена", Toast.LENGTH_SHORT).show() }
            builder.show()
        }

        seekTimeChanger.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    timeText.text = functionsApp.normTime(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    //TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    //TODO("Not yet implemented")
                }

            }
        )

        state.setOnCheckedChangeListener { buttonView, state ->
            // TODO тут в базу плевать (штука может в true и false)
            val db = DBconnect(this)
            CoroutineScope(Dispatchers.IO).launch {
                db.getAllNotificationsFromDB().forEach {
                    // тут меняю состояние каждого на state + перезаписываем сообщение и время до срабатывания
                    // тут берём все уведомления и ставим state в true или false. Надо думать как уведомления делать. Удалять уведомление после того как при парсинге дня уведомления не стало
                }
            }
            println("message - ${message.text}")
        }




    }

    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch {
            val etSheduleLink = findViewById<EditText>(R.id.et_shedule_link)
            dataStore.edit { preferences ->
                val link = etSheduleLink.text.toString()
                preferences[DataSettingsKeys.SHEDULE_LINK] = link
                sheduleLink = link
            }
        }
    }


    suspend fun updateEntity(user_group: String) {
        dataStore.edit { preferences ->
            preferences[DataSettingsKeys.USER_ENTITY] = user_group
        }
        entity = user_group
    }

    suspend fun selectEntity(spinner: MySpinner) {

        val userPreferencesFlow: Flow<DataSettings> = dataStore.data.map {
                preferences ->
            DataSettings(preferences[DataSettingsKeys.USER_ENTITY].toString(), preferences[DataSettingsKeys.USER_VIEW].toString(), preferences[DataSettingsKeys.SHEDULE_LINK].toString())
        }
        userPreferencesFlow.collect { dataSettings ->
            val mainEntity = dataSettings.user_entity

            runOnUiThread {
                if (mainEntity in allGroups && spinner == findViewById<MySpinner>(R.id.s_groups)) {
                    spinner.setSelection(allGroups.indexOf(mainEntity))
                }
                if (mainEntity in allTeachers && spinner == findViewById<MySpinner>(R.id.s_teachers)) {
                    spinner.setSelection(allTeachers.indexOf(mainEntity))
                }
                println("tag")

            }
        }



    }

}
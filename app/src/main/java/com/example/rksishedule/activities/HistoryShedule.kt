package com.example.rksishedule.activities

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.edit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rksishedule.R
import com.example.rksishedule.data.DBconnect
import com.example.rksishedule.data.RVhistoryAdapter
import com.example.rksishedule.data.RVweekAdapter
import com.example.rksishedule.data.allTrashGroups
import com.example.rksishedule.data.allTrashTeachers
import com.example.rksishedule.data.currentEntity
import com.example.rksishedule.data.dataStore
import com.example.rksishedule.data.entityIsNotSelect
import com.example.rksishedule.data.functionsApp
import com.example.rksishedule.data.uView
import com.example.rksishedule.data.historyDataRV
import com.example.rksishedule.data.mainEntity
import com.example.rksishedule.parser.getWeekShedule
import com.example.rksishedule.parser.lessonsInUnCommonDay1
import com.example.rksishedule.parser.lessonsInUnCommonDay2
import com.example.rksishedule.parser.lessonsTimeInCommonDay
import com.example.rksishedule.parser.lessonsTimeInMondayDay
import com.example.rksishedule.parser.lessonsTimeInShortDay
import com.example.rksishedule.parser.monthList
import com.example.rksishedule.parser.typeOfDay
import com.example.rksishedule.preferences.DataSettings
import com.example.rksishedule.preferences.DataSettingsKeys
import com.example.rksishedule.utils.Lesson
import com.example.rksishedule.utils.LessonHistory
import com.example.rksishedule.utils.Shedule
import com.example.rksishedule.utils.SheduleHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate

class HistoryShedule : AppCompatActivity() {
    lateinit private var whatsParse: TextView
    lateinit private var entity: String
    var toIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history_shedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val shedule = findViewById<RecyclerView>(R.id.rv_shedule)
        val bBack = findViewById<ImageButton>(R.id.b_back)
        whatsParse = findViewById(R.id.tv_what_is_parse)
        val oneEl = findViewById<ImageView>(R.id.iv_one_el)
        val threeEl = findViewById<ImageView>(R.id.iv_three_el)
        val easyEl = findViewById<ImageView>(R.id.iv_easy_view)
        val pbLoading = findViewById<ProgressBar>(R.id.i_loading)


        val layoutManager =
            GridLayoutManager(applicationContext, 1, GridLayoutManager.HORIZONTAL, false)
        shedule.setHasFixedSize(true)
        shedule.layoutManager = layoutManager


        CoroutineScope(Dispatchers.IO).launch {

            runOnUiThread {
                CoroutineScope(Dispatchers.IO).launch {
                    setWeekShedule(
                        oneEl,
                        threeEl,
                        easyEl,
                        pbLoading,
                        shedule
                    )
                }
            }
        }





        oneEl.setOnClickListener {

            if (entityIsNotSelect) {
                functionsApp.selectEntityToast(applicationContext)
            } else {
                pbLoading.visibility = ProgressBar.VISIBLE
                CoroutineScope(Dispatchers.IO).launch { updateView(getString(R.string.code_one_el)) }
                oneEl.setBackgroundResource(R.drawable.active)
                threeEl.setBackgroundResource(R.drawable.unactive)
                easyEl.setBackgroundResource(R.drawable.unactive)
                pbLoading.visibility = ProgressBar.INVISIBLE
            }

        }

        threeEl.setOnClickListener {

            if (entityIsNotSelect) {
                functionsApp.selectEntityToast(applicationContext)
            } else {
                pbLoading.visibility = ProgressBar.VISIBLE
                CoroutineScope(Dispatchers.IO).launch { updateView(getString(R.string.code_three_el)) }
                oneEl.setBackgroundResource(R.drawable.unactive)
                threeEl.setBackgroundResource(R.drawable.active)
                easyEl.setBackgroundResource(R.drawable.unactive)
                pbLoading.visibility = ProgressBar.INVISIBLE
            }

        }

        easyEl.setOnClickListener {

            if (entityIsNotSelect) {
                functionsApp.selectEntityToast(applicationContext)
            } else {
                pbLoading.visibility = ProgressBar.VISIBLE
                CoroutineScope(Dispatchers.IO).launch { updateView(getString(R.string.code_easy_el)) }
                oneEl.setBackgroundResource(R.drawable.unactive)
                threeEl.setBackgroundResource(R.drawable.unactive)
                easyEl.setBackgroundResource(R.drawable.active)
                pbLoading.visibility = ProgressBar.INVISIBLE
            }

        }

        bBack.setOnClickListener {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }



    suspend fun updateView(user_view: String) {
        dataStore.edit { preferences ->
            preferences[DataSettingsKeys.USER_VIEW] = user_view
        }
        uView = user_view
    }


    fun loadHistory(pbLoading: ProgressBar, shedule: RecyclerView) {
        val db = DBconnect(applicationContext)

        pbLoading.visibility = ProgressBar.VISIBLE
        whatsParse.textSize = 23F

        CoroutineScope(Dispatchers.IO).launch {
            historyDataRV.clear()

            var h = db.getAllHistoryFromDB()
            var entHistory = ""
            try {
                entHistory = db.getAllHistoryFromDB()[0].entityForHistory.toString()
            }catch (e: Exception){
                if (!entity.contains("[0-9]".toRegex())) {
                    getWeekShedule("teacher",
                        allTrashTeachers[mainEntity].toString(), applicationContext)
                }
                else {
                    getWeekShedule("group",
                        allTrashGroups[mainEntity].toString(), applicationContext)
                }
                h = db.getAllHistoryFromDB()
                entHistory = db.getAllHistoryFromDB()[0].entityForHistory.toString()
            }
            val historyShedule = mutableListOf<SheduleHistory>()
            val oneDay = mutableListOf<LessonHistory>()

            h.forEachIndexed { i, it ->
                if (oneDay.isNotEmpty()) {
                    if (it.date != oneDay[oneDay.size-1].getDay()) {
                        historyShedule.add(SheduleHistory(oneDay.toMutableList()))
                        oneDay.clear()
                    }
                }
                oneDay.add(LessonHistory(it.edit, it.date!!, it.lessonNumber!!, it.lesson!!, it.entity!!, it.aud!!, it.timeLesson!!, it.deleteLessonName!!))
                if ( i == h.size-1) {
                    historyShedule.add(SheduleHistory(oneDay.toMutableList()))
                }


                if (i > 0 && "${LocalDate.now().dayOfMonth}.${LocalDate.now().monthValue}" == "${it.date.split(" ")[0]}.${monthList.indexOf(it.date.split(" ")[1].replace(",", ""))+1}"){
                    if (h[i-1].date != LocalDate.now().toString()) {
                        toIndex = i/7
                    }
                }
            }
            runOnUiThread {
                whatsParse.text = "${getString(R.string.history)} $entHistory"
                historyShedule.forEach {
                    val rv = RecyclerView(applicationContext)
                    rv.layoutManager = GridLayoutManager(applicationContext, 1)
                    rv.adapter = RVhistoryAdapter(this@HistoryShedule, it)
                    historyDataRV.add(rv)

                }

                val adapter = RVweekAdapter(applicationContext, historyDataRV)
                shedule.adapter = adapter
                adapter.notifyItemRangeChanged(0, historyDataRV.size)
                when {
                    uView == "one" -> {
                        shedule.scrollToPosition(toIndex)
                    }
                    uView == "three" -> {
                        if (toIndex-2 >= 0) {
                            println("threee $toIndex")
                            shedule.scrollToPosition(toIndex)
                        }
                        else {
                            shedule.scrollToPosition(toIndex)
                        }
                    }
                    uView == "easy" -> {
                        if (toIndex+4 <= adapter.itemCount) {
                            shedule.scrollToPosition(toIndex)
                        }
                        else {
                            shedule.scrollToPosition(toIndex)
                        }
                    }
                }

                //shedule.scrollToPosition(historyDataRV.size*7-1)



            }


            pbLoading.visibility = ProgressBar.INVISIBLE
        }
    }


    suspend fun setWeekShedule(
        oneEl: ImageView,
        threeEl: ImageView,
        easyEl: ImageView,
        pbLoading: ProgressBar,
        shedule: RecyclerView
    ) {
        val userPreferencesFlow: Flow<DataSettings> = dataStore.data.map { preferences ->
            DataSettings(
                preferences[DataSettingsKeys.USER_ENTITY].toString(),
                preferences[DataSettingsKeys.USER_VIEW].toString(),
                preferences[DataSettingsKeys.SHEDULE_LINK].toString()
            )
        }
        userPreferencesFlow.collect { dataSettings ->

            entity = dataSettings.user_entity

            uView = dataSettings.user_view
            if (uView == "null") updateView(getString(R.string.code_one_el))

            runOnUiThread {
                loadHistory(pbLoading, shedule)
                if (entity == "null") {
                    entityIsNotSelect = true
                    oneEl.setColorFilter(R.color.unactive)
                    threeEl.setColorFilter(R.color.unactive)
                    easyEl.setColorFilter(R.color.unactive)
                }

                oneEl.setBackgroundResource(R.drawable.unactive)
                threeEl.setBackgroundResource(R.drawable.unactive)
                easyEl.setBackgroundResource(R.drawable.unactive)
                when (uView) {
                    "one" -> oneEl.setBackgroundResource(R.drawable.active)
                    "three" -> threeEl.setBackgroundResource(R.drawable.active)
                    "easy" -> easyEl.setBackgroundResource(R.drawable.active)
                }
            }
        }
    }



}


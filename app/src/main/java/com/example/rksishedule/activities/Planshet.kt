package com.example.rksishedule.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rksishedule.R
import com.example.rksishedule.preferences.DataSettings
import com.example.rksishedule.preferences.DataSettingsKeys
import com.example.rksishedule.parser.ParsePlanshetteShedule
import com.example.rksishedule.utils.PlanshetInfo
import com.example.rksishedule.data.RVadapter
import com.example.rksishedule.data.RVweekAdapter
import com.example.rksishedule.data.allGroups
import com.example.rksishedule.data.allTeachers
import com.example.rksishedule.data.allTrashGroups
import com.example.rksishedule.data.allTrashTeachers
import com.example.rksishedule.data.currentDay
import com.example.rksishedule.data.currentEntity
import com.example.rksishedule.data.dataStore
import com.example.rksishedule.data.entity
import com.example.rksishedule.data.functionsApp
import com.example.rksishedule.data.isRefreshed
import com.example.rksishedule.data.nightMode
import com.example.rksishedule.data.sheduleLink
import com.example.rksishedule.data.weekDataRV
import com.example.rksishedule.parser.clearTrashFromDB
import com.example.rksishedule.parser.getWeekShedule
import com.example.rksishedule.utils.MySpinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.concurrent.timer


class Planshet: AppCompatActivity() {
    
    var parseEntity = currentEntity
    var pDays = mutableListOf<String>()
    var sheduleParse = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_planshet)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_planshet)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val parsePlanshette = ParsePlanshetteShedule()


        val sGroups = findViewById<MySpinner>(R.id.s_groups)
        val sTeachers = findViewById<MySpinner>(R.id.s_teachers)
        val sDays = findViewById<MySpinner>(R.id.s_days)

        val lessonNumber = findViewById<EditText>(R.id.et_lesson_number)
        val table = findViewById<TableLayout>(R.id.t_shedule)
        val parse = findViewById<Button>(R.id.b_parse)
        val bBack = findViewById<ImageButton>(R.id.b_back)
        val pbLoading = findViewById<ProgressBar>(R.id.loading)
        val tableEntity: TextView = findViewById(R.id.textView3)

        currentDay = ParsePlanshetteShedule().normalizeDate(LocalDate.now().dayOfMonth, LocalDate.now().monthValue, LocalDate.now().year)
        val rows = 7
        var k = 0
        val newRow = mutableListOf<TableRow>().apply { for (i in 0..<rows) add(TableRow(applicationContext)) }


        if (nightMode) {
            sGroups.setPopupBackgroundResource(R.color.transparent_black_85)
            sTeachers.setPopupBackgroundResource(R.color.transparent_black_85)
            sDays.setPopupBackgroundResource(R.color.transparent_black_85)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val userPreferencesFlow: Flow<DataSettings> = dataStore.data.map {
                    preferences ->
                DataSettings(preferences[DataSettingsKeys.USER_ENTITY].toString(), preferences[DataSettingsKeys.USER_VIEW].toString(), preferences[DataSettingsKeys.SHEDULE_LINK].toString())
            }
            userPreferencesFlow.collect { dataSettings ->
                sheduleLink = dataSettings.shedule_link

            }
        }
        val adapterGroups = ArrayAdapter(applicationContext,
            R.layout.spinner_item, allTrashGroups.values.toList())
        adapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val adapterTeachers = ArrayAdapter(applicationContext,
            R.layout.spinner_item, allTrashTeachers.values.toList())
        adapterTeachers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        CoroutineScope(Dispatchers.IO).launch {
            pDays = parsePlanshette.GSparseDays()

            runOnUiThread {
                val adapterDays = ArrayAdapter(applicationContext,
                    R.layout.spinner_item, pDays)
                adapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sDays.adapter = adapterDays
                CoroutineScope(Dispatchers.IO).launch {
                    runOnUiThread {
                        if (pDays.isNotEmpty()) {
                            if (currentDay in pDays) {
                                sheduleParse = currentDay
                                sDays.setSelection(pDays.indexOf(currentDay))
                            } else {
                                sheduleParse = pDays[pDays.size-1]
                                sDays.setSelection(pDays.size-1)
                            }
                        }
                    }
                }
            }

            pbLoading.visibility = ProgressBar.INVISIBLE
        }

        sGroups.adapter = adapterGroups
        sTeachers.adapter = adapterTeachers

        if (!parseEntity.contains("[0-9]".toRegex())) {
            sTeachers.setSelection(allTrashTeachers.keys.toList().indexOf(parseEntity))
        }
        else {
            sGroups.setSelection(allTrashGroups.values.indexOf(parseEntity))
        }






        for (i in 0..< rows*3) {
            if (i%3 == 0 && i != 0) k ++
            val tv = TextView(applicationContext)
            tv.setBackgroundResource(R.drawable.white_border)
            tv.setTextColor(resources.getColor(R.color.whiteno))
            tv.setPadding(functionsApp.dpToPx(applicationContext, 4))
            newRow[k].addView(tv)
        }
        newRow.apply { forEach { table.addView(it) } }




            parse.setOnClickListener {
                for (i in 1..7) {
                    val j = 3 * i
                    val tLesnNum = getCellAtPos(table, j) as TextView
                    val tAud = getCellAtPos(table, j + 1) as TextView
                    val tEnt = getCellAtPos(table, j + 2) as TextView
                    tLesnNum.text = ""
                    tAud.text = ""
                    tEnt.text = ""
                }
                if (!parseEntity.contains("[0-9]".toRegex())) {
                    tableEntity.text = getString(R.string.group)
                }
                else {
                    tableEntity.text = getString(R.string.prep)
                }
                if (sheduleLink.contains("https://drive.google.com/drive/folders/")) {
                    pbLoading.visibility = ProgressBar.VISIBLE

                    CoroutineScope(Dispatchers.IO).launch {
                        parsePlanshette.GSparse(applicationContext, sheduleParse)

                        var sheduleInfo = mutableListOf<PlanshetInfo>()

                        val isDigit = try {
                            lessonNumber.text.toString().toInt()
                            true
                        } catch (e: Exception) {
                            false
                        }

                        if (lessonNumber.text.toString() == "") {
                            sheduleInfo = parsePlanshette.findXlsxInfo(
                                applicationContext,
                                parseEntity,
                                sheduleParse
                            )
                        }
                        else { if (isDigit) {
                            if (lessonNumber.text.toString().toInt() in 1..7) {
                                sheduleInfo = parsePlanshette.findXlsxInfo(
                                    applicationContext,
                                    parseEntity,
                                    lessonNumber.text.toString().toInt(),
                                    sheduleParse
                                )
                            }
                        }}


                        runOnUiThread {
                            println("SH info - ${sheduleInfo.size}")
                            when {
                                (sheduleInfo.size == 1) -> {
                                    if (sheduleInfo[0].aud == "" && sheduleInfo[0].res) {
                                        Toast.makeText(
                                            applicationContext,
                                            getString(R.string.no_lesson),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    if (sheduleInfo[0].aud == "0" && sheduleInfo[0].res) {
                                        Toast.makeText(
                                            applicationContext,
                                            getString(R.string.dont_find_lesson),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    if (sheduleInfo[0] == PlanshetInfo("0", "0")) {
                                        Toast.makeText(
                                            applicationContext,
                                            getString(R.string.load_file_error),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    } else {
                                        val j = 3 * lessonNumber.text.toString().toInt()
                                        val tLesnNum = getCellAtPos(table, j) as TextView
                                        val tAud = getCellAtPos(table, j + 1) as TextView
                                        val tEnt = getCellAtPos(table, j + 2) as TextView
                                        tLesnNum.text = lessonNumber.text.toString() + "\n".repeat(
                                            functionsApp.countOccurrences(
                                                sheduleInfo[0].aud,
                                                "\n"
                                            )
                                        )
                                        tAud.text = sheduleInfo[0].aud
                                        tEnt.text = sheduleInfo[0].secondEntity
                                    }
                                }


                                else -> {
                                    if (sheduleInfo.size == 1) {
                                        val tLesnNum = getCellAtPos(
                                            table,
                                            lessonNumber.text.toString().toInt()
                                        ) as TextView
                                        val tAud = getCellAtPos(
                                            table,
                                            lessonNumber.text.toString().toInt() + 1
                                        ) as TextView
                                        val tEnt = getCellAtPos(
                                            table,
                                            lessonNumber.text.toString().toInt() + 2
                                        ) as TextView
                                        tLesnNum.text = lessonNumber.text.toString() + "\n".repeat(
                                            functionsApp.countOccurrences(
                                                sheduleInfo[0].aud,
                                                "\n"
                                            )
                                        )
                                        tAud.text = sheduleInfo[0].aud
                                        tEnt.text = sheduleInfo[0].secondEntity
                                    } else {
                                        try {
                                            for (i in 0..<sheduleInfo.size) {
                                                val j = (i + 1) * 3
    //                                        println("$i, $j")
                                                val tLesnNum = getCellAtPos(table, j) as TextView
                                                val tAud = getCellAtPos(table, j + 1) as TextView
                                                val tEnt = getCellAtPos(table, j + 2) as TextView

                                                tAud.text = sheduleInfo[i].aud
                                                tEnt.text = sheduleInfo[i].secondEntity
                                                if (functionsApp.getCurrentDayOfWeek(sheduleParse) == "понедельник") {//functionsApp.getCurrentDayOfWeek() ==
                                                        if (i+1 == 4) {
                                                        tLesnNum.text = "классный\nчас" + "\n".repeat(
                                                            functionsApp.countOccurrences(
                                                                sheduleInfo[i].aud,
                                                                "\n"
                                                            )
                                                        )
                                                        tAud.text = "${tAud.text}\n"
                                                        tEnt.text = "${tEnt.text}\n"
                                                    }
                                                    if (i+1 > 4) {
                                                        tLesnNum.text = "${i}" + "\n".repeat(
                                                            functionsApp.countOccurrences(
                                                                sheduleInfo[i].aud,
                                                                "\n"
                                                            )
                                                        )
                                                    }
                                                    if (i+1 < 4) {
                                                        tLesnNum.text = "${i+1}" + "\n".repeat(
                                                            functionsApp.countOccurrences(
                                                                sheduleInfo[i].aud,
                                                                "\n"
                                                            )
                                                        )
                                                    }
                                                }
                                                else {
                                                    tLesnNum.text = "${i + 1}" + "\n".repeat(
                                                        functionsApp.countOccurrences(
                                                            sheduleInfo[i].aud,
                                                            "\n"
                                                        )
                                                    )
                                                }
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                applicationContext,
                                                getString(R.string.update_table_error),
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    }

                                }
                            }
                            pbLoading.visibility = ProgressBar.INVISIBLE
                        }
                    }
                }
                else {
                    Toast.makeText(applicationContext, getString(R.string.wrong_link), Toast.LENGTH_LONG).show()
                }
            }


        bBack.setOnClickListener {
            finish()
        }


        sGroups.setOnItemSelectedEvenIfUnchangedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                parseEntity = allTrashGroups.keys.elementAt(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

        sTeachers.setOnItemSelectedEvenIfUnchangedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                parseEntity = allTrashTeachers.keys.elementAt(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

        sDays.setOnItemSelectedEvenIfUnchangedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                println("POS ${pDays[position]}, $position")
                sheduleParse = pDays[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }


    override fun onResume() {
        super.onResume()
    }


    private fun getCellAtPos(table: TableLayout, pos: Int): View { // 3 - количество столбцов
        val row = table.getChildAt(pos/3) as TableRow
        return row.getChildAt(pos%3)
    }
}
package com.example.rksishedule.activities

import android.os.Bundle
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
import com.example.rksishedule.data.entity
import com.example.rksishedule.data.entityIsNotSelect
import com.example.rksishedule.data.functionsApp
import com.example.rksishedule.data.historyDataRV
import com.example.rksishedule.data.isRefreshed
import com.example.rksishedule.data.sheduleLink
import com.example.rksishedule.data.uView
import com.example.rksishedule.parser.getSheduleOnThisDay
import com.example.rksishedule.parser.getWeekShedule
import com.example.rksishedule.preferences.DataSettings
import com.example.rksishedule.preferences.DataSettingsKeys
import com.example.rksishedule.utils.LessonHistory
import com.example.rksishedule.utils.MySpinner
import com.example.rksishedule.utils.SheduleHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EmerganceMode : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_emergance_mode)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val menu = findViewById<ImageView>(R.id.menu)
        val whatsParse = findViewById<TextView>(R.id.tv_what_is_parse)
        val pbLoading = findViewById<ProgressBar>(R.id.i_loading)
        val textShedule = findViewById<TextView>(R.id.shedule_info)

        CoroutineScope(Dispatchers.IO).launch {
            val userPreferencesFlow: Flow<DataSettings> = dataStore.data.map {
                    preferences ->
                DataSettings(preferences[DataSettingsKeys.USER_ENTITY].toString(), preferences[DataSettingsKeys.USER_VIEW].toString(), preferences[DataSettingsKeys.SHEDULE_LINK].toString())
            }
            userPreferencesFlow.collect { dataSettings ->
                entity = dataSettings.user_entity
                try {
                    currentEntity = allTrashGroups[entity]!!
                }catch (e: Exception) {currentEntity = allTrashTeachers[entity]!! }
                val shedule = loadSheduleFromSite(pbLoading)
                runOnUiThread {
                    println("TITITIT "+entity)
                    whatsParse.text = entity
                    shedule.forEach {
                        val currentText = textShedule.text
                        val les = it.replace("<p>", "").replace("</p>", "").replace("<br>", "").replace("<b>", "\n").replace("</b>", "\n").replace("., ", "\n")
                        textShedule.text = "$currentText\n\n$les"
                    }
                }
            }

        }
        menu.setOnClickListener {
            functionsApp.showMainPopup(this, menu)

        }


    }



    fun loadSheduleFromSite(pbLoading: ProgressBar): MutableList<String> {

        if (!entity.contains("[0-9]".toRegex())) {
            pbLoading.visibility = ProgressBar.INVISIBLE
            return getSheduleOnThisDay("teacher", entity)
        }
        else {
            pbLoading.visibility = ProgressBar.INVISIBLE
            return getSheduleOnThisDay("group", entity)
        }

    }


}
package com.example.rksishedule.activities

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RouteListingPreference.Item
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.alpha
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.edit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rksishedule.preferences.DataSettings
import com.example.rksishedule.preferences.DataSettingsKeys
import com.example.rksishedule.database.GroupsDB
import com.example.rksishedule.utils.MySpinner
import com.example.rksishedule.R
import com.example.rksishedule.data.DBconnect
import com.example.rksishedule.data.RVadapter
import com.example.rksishedule.data.RVweekAdapter
import com.example.rksishedule.data.allGroups
import com.example.rksishedule.data.allTeachers
import com.example.rksishedule.data.allTrashGroups
import com.example.rksishedule.data.allTrashTeachers
import com.example.rksishedule.data.currentEntity
import com.example.rksishedule.data.dataStore
import com.example.rksishedule.data.entity
import com.example.rksishedule.data.entityIsNotSelect
import com.example.rksishedule.data.functionsApp
import com.example.rksishedule.data.isRefreshed
import com.example.rksishedule.data.mainEntity
import com.example.rksishedule.data.nightMode
import com.example.rksishedule.data.sheduleLink
import com.example.rksishedule.data.start
import com.example.rksishedule.data.uView
import com.example.rksishedule.data.weekDataRV
import com.example.rksishedule.database.TeachersDB
import com.example.rksishedule.parser.clearTrashFromDB
import com.example.rksishedule.parser.getAllGroups
import com.example.rksishedule.parser.getAllPreps
import com.example.rksishedule.parser.getSheduleOnThisDay
import com.example.rksishedule.parser.getTypeOfWeek
import com.example.rksishedule.parser.getWeekShedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch




class MainActivity : AppCompatActivity() {

    private val INTERNET_PERMISSION_CODE = 1
    private val ACCESS_NETWORK_STATE_PERMISSION_CODE = 2
    private val ACCESS_NOTIFY_PERMISSION_CODE = 3
    lateinit var whatsParse : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        println("""В Postman все отображается корректно .
И на сервере и в Android Studio стоит по умолчанию UTF-8.
Запросы идентичные различаются количеством передаваемых параметров и наличием заголовка. """)
        val shedule = findViewById<RecyclerView>(R.id.rv_shedule)
        val menu = findViewById<ImageView>(R.id.menu)
        val groups = findViewById<MySpinner>(R.id.spin_groups)
        val teachers = findViewById<MySpinner>(R.id.spin_teachers)
        whatsParse = findViewById(R.id.tv_what_is_parse)
        val typeOfWeek = findViewById<TextView>(R.id.tv_type_of_week)
        val refresh = findViewById<ImageView>(R.id.refresh_page)
        val oneEl = findViewById<ImageView>(R.id.iv_one_el)
        val threeEl = findViewById<ImageView>(R.id.iv_three_el)
        val easyEl = findViewById<ImageView>(R.id.iv_easy_view)
        val pbLoading = findViewById<ProgressBar>(R.id.i_loading)


        val db = DBconnect(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
//            db.clearGroups()
//            db.clearTeachers()
//            db.clearHistory()
//            db.clearShedule()
        }

        requestInternetPermission()
        //// When building the database, add the migration
//        val database = Room.databaseBuilder(
//            context.applicationContext,
//            YourDatabase::class.java,
//            "your_database_name"
//        )
//            .addMigrations(migration_1_2)
//            .build()


        if (functionsApp.isNightMode(applicationContext)) {
            nightMode = true
            groups.setPopupBackgroundResource(R.color.transparent_black_85)
            teachers.setPopupBackgroundResource(R.color.transparent_black_85)
        }
        whatsParse.text = entity


        val layoutManager = GridLayoutManager(applicationContext, 1, GridLayoutManager.HORIZONTAL, false)
        shedule.layoutManager = layoutManager


        CoroutineScope(Dispatchers.IO).launch {

//            val h = db.getAllHistoryFromDB()
//            println("Расписание из истории $h")
//            h.forEach {
//                println(it.toString())
//            }
//            if (h.isEmpty()) {
//                println("H пустой")
//            }
//            if (h.isNotEmpty()) {
//                println("H not пустой")
//            }



            typeOfWeek.text = getTypeOfWeek()
        }
        CoroutineScope(Dispatchers.IO).launch {
            if (db.getAllGroupsFromDB().isEmpty()) {
                allTrashGroups = getAllGroups()
                allTrashGroups.forEach { (k, v) ->
                    allGroups.add(v)
                    db.insertGroup(GroupsDB(0, k, v))
                }
            }
            else {
                db.getAllGroupsFromDB().forEach {
                    allTrashGroups[it.group_name] = it.group_value
                    if (it.group_name !in allGroups) allGroups.add(it.group_name)
                }
            }


            runOnUiThread {
                val adapterGroups = ArrayAdapter(applicationContext,
                    R.layout.spinner_item, allGroups
                )

                adapterGroups.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                groups.adapter = adapterGroups

                CoroutineScope(Dispatchers.IO).launch { setWeekShedule(groups, oneEl, threeEl, easyEl) }
            }
        }



        CoroutineScope(Dispatchers.IO).launch {

            if (db.getAllTeachersFromDB().isEmpty()) {
                allTrashTeachers = getAllPreps()
                allTrashTeachers.forEach { (k, v)->
                    allTeachers.add(v)
                    db.insertTeacher(TeachersDB(0, k, v))
                }

            }
            else {
                db.getAllTeachersFromDB().forEach {
                    allTrashTeachers[it.teacher_name] = it.teacher_value
                    if (it.teacher_name !in allTeachers) allTeachers.add(it.teacher_name)
                }
            }
            runOnUiThread {

                val adapterTeachers = ArrayAdapter(applicationContext,
                    R.layout.spinner_item, allTeachers
                )
                adapterTeachers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                teachers.adapter = adapterTeachers
                CoroutineScope(Dispatchers.IO).launch { setWeekShedule(teachers, oneEl, threeEl, easyEl) }
            }

        }



        groups.setOnItemSelectedEvenIfUnchangedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                pbLoading.visibility = ProgressBar.VISIBLE
                whatsParse.text = allGroups[position]
                whatsParse.textSize = 23F
                currentEntity = allTrashGroups.keys.elementAt(position)
                println(allTrashGroups[allGroups[position]].toString())
                isRefreshed = true
                Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    weekDataRV.clear()
                    var groupIsReal = false
                    try {
                        getWeekShedule("group", currentEntity, applicationContext)
                        groupIsReal = true
                    }catch (e: Exception){
                        try {
                            getSheduleOnThisDay("group", currentEntity).forEach {
                                if (it.contains("ауд.")) {
                                    startActivity(Intent(applicationContext, EmerganceMode::class.java))
                                }
                            }
                        } catch (e: Exception) {
                            runOnUiThread{
                                whatsParse.text = "${getString(R.string.shedule_group)}\n${currentEntity}\n${getString(R.string.dont_find)}"
                                whatsParse.textSize = 11F
                            }
                        }
                    }

                    if (groupIsReal) {
                        val weekShedule =  getWeekShedule("group",
                            currentEntity,
                            applicationContext
                        )

                        clearTrashFromDB(applicationContext)
                        runOnUiThread {
                            weekShedule.forEach {
                                val rv = RecyclerView(applicationContext)
                                rv.layoutManager = GridLayoutManager(applicationContext, 1)
                                rv.adapter = RVadapter(this@MainActivity, it)
                                rv.setHasFixedSize(true)
                                weekDataRV.add(rv)

                            }
                            val adapter = RVweekAdapter(applicationContext, weekDataRV)
                            shedule.adapter = adapter
                            adapter.notifyItemRangeChanged(0, weekDataRV.size)
                        }
                    }

                    pbLoading.visibility = ProgressBar.INVISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })


        teachers.setOnItemSelectedEvenIfUnchangedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                pbLoading.visibility = ProgressBar.VISIBLE
                whatsParse.text = allTeachers[position]
                whatsParse.textSize = 23F
                currentEntity = allTrashTeachers.keys.elementAt(position)
                isRefreshed = true
                Toast.makeText(applicationContext, position.toString(), Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    weekDataRV.clear()
                    var teacherIsReal = false
                    try {
                        getWeekShedule("teacher", currentEntity, applicationContext)
                        teacherIsReal = true
                    }catch (e: Exception){
                        try {
                            getSheduleOnThisDay("teacher", currentEntity).forEach {
                                if (it.contains("ауд.")) {
                                    startActivity(Intent(applicationContext, EmerganceMode::class.java))
                                }
                            }
                        } catch (e: Exception) {
                            runOnUiThread{
                                whatsParse.text = "${getString(R.string.shedule_teacher)}\n${currentEntity}\n${getString(R.string.dont_find)}"
                                whatsParse.textSize = 11F
                            }
                        }
                    }

                    if (teacherIsReal) {
                        val weekShedule = getWeekShedule(
                            "teacher",
                            currentEntity,
                            applicationContext
                        )
                        clearTrashFromDB(applicationContext)
                        runOnUiThread {
                            weekShedule.forEach {
                                val rv = RecyclerView(applicationContext)
                                rv.layoutManager = GridLayoutManager(applicationContext, 1)
                                rv.adapter = RVadapter(this@MainActivity, it)
                                weekDataRV.add(rv)
                            }
                            val adapter = RVweekAdapter(applicationContext, weekDataRV)
                            shedule.adapter = adapter
                            adapter.notifyItemRangeChanged(0, weekDataRV.size)
                        }
                    }
                    pbLoading.visibility = ProgressBar.INVISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })


        refresh.setOnClickListener {
            isRefreshed = true
            finish()
            startActivity(intent)
            CoroutineScope(Dispatchers.IO).launch {
//                db.clearLessons()
            }
//            createNotificationChannel(this) //TODO

        }

        oneEl.setOnClickListener {

            if (entityIsNotSelect) {
                functionsApp.selectEntityToast(applicationContext)
            }
            else {
                pbLoading.visibility = ProgressBar.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {updateView(getString(R.string.code_one_el))}
                oneEl.setBackgroundResource(R.drawable.active)
                threeEl.setBackgroundResource(R.drawable.unactive)
                easyEl.setBackgroundResource(R.drawable.unactive)
                pbLoading.visibility = ProgressBar.INVISIBLE
            }

        }

        threeEl.setOnClickListener {

            if (entityIsNotSelect) {
                functionsApp.selectEntityToast(applicationContext)
            }
            else {
                pbLoading.visibility = ProgressBar.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {updateView(getString(R.string.code_three_el))}
                oneEl.setBackgroundResource(R.drawable.unactive)
                threeEl.setBackgroundResource(R.drawable.active)
                easyEl.setBackgroundResource(R.drawable.unactive)
                pbLoading.visibility = ProgressBar.INVISIBLE
            }

        }

        easyEl.setOnClickListener {

            if (entityIsNotSelect) {
                functionsApp.selectEntityToast(applicationContext)
            }
            else {
                pbLoading.visibility = ProgressBar.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {updateView(getString(R.string.code_easy_el))}
                oneEl.setBackgroundResource(R.drawable.unactive)
                threeEl.setBackgroundResource(R.drawable.unactive)
                easyEl.setBackgroundResource(R.drawable.active)
                pbLoading.visibility = ProgressBar.INVISIBLE
            }

        }

        menu.setOnClickListener {
            functionsApp.showMainPopup(this, menu)

        }
    }


    override fun onResume() {
        super.onResume()
        kotlin.io.println("Resume!")
    }
    override fun onDestroy() {
        super.onDestroy()

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


    suspend fun setWeekShedule(spinner: MySpinner, oneEl: ImageView, threeEl: ImageView, easyEl: ImageView) {
        val userPreferencesFlow: Flow<DataSettings> = dataStore.data.map {
                preferences ->
            DataSettings(preferences[DataSettingsKeys.USER_ENTITY].toString(), preferences[DataSettingsKeys.USER_VIEW].toString(), preferences[DataSettingsKeys.SHEDULE_LINK].toString())
        }
        userPreferencesFlow.collect { dataSettings ->
            sheduleLink = dataSettings.shedule_link

            entity = dataSettings.user_entity
            if (isRefreshed) {
                entity = currentEntity
            }
            uView = dataSettings.user_view
            if (uView == "null") updateView(getString(R.string.code_one_el))

            runOnUiThread {
                if (entity in allTrashGroups.keys && spinner == findViewById<MySpinner>(R.id.spin_groups)) {
                    spinner.setSelection(allGroups.indexOf(entity))
                    currentEntity = entity
                    if (start) mainEntity = allTrashGroups[dataSettings.user_entity].toString()
                    start = false
                }
                if (entity in allTrashTeachers.keys && spinner == findViewById<MySpinner>(R.id.spin_teachers)) {
                    spinner.setSelection(allTeachers.indexOf(entity))
                    currentEntity = entity
                    if (start) mainEntity = allTrashTeachers[dataSettings.user_entity].toString()

                }
                if (entity == "null") {
                    entityIsNotSelect = true
                    oneEl.setColorFilter(R.color.unactive)
                    threeEl.setColorFilter(R.color.unactive)
                    easyEl.setColorFilter(R.color.unactive)
                    repeat(6) {
                        Toast.makeText(applicationContext, getString(R.string.choose_default_shedule_1), Toast.LENGTH_SHORT).show()
                        Toast.makeText(applicationContext, getString(R.string.choose_default_shedule_2), Toast.LENGTH_SHORT).show()
                    }
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

//    private fun createNotificationChannel(context: Context) {
//        val imgBitmap = BitmapFactory.decodeResource(resources, R.drawable.arharova)
//
//        var builder = NotificationCompat.Builder(context, "0")
//            .setSmallIcon(R.drawable.baseline_notifications_active_24)
//            .setContentTitle("Катя")
//            .setContentText("Суперниндзя финал на СТС в воскресенье в 17:00")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setLargeIcon(imgBitmap)
//            .setStyle(NotificationCompat.BigPictureStyle()
//                .bigPicture(imgBitmap)
//                .bigLargeIcon(null as Bitmap?))
//            .setAutoCancel(true)
//            .build()
//
//        val nManager = NotificationManagerCompat.from(context)
//
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(context, "Выдайте разрешение на уведомления", Toast.LENGTH_SHORT).show()
//            return
//        }
//        nManager.notify(1, builder)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel("0", "0", NotificationManager.IMPORTANCE_DEFAULT).apply {
//                description = "0"
//            }
//            val nManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            nManager.createNotificationChannel(channel)
//        }
//    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestInternetPermission(){
        //request the READ_CONTACTS permission
//        val permission1 = arrayOf(android.Manifest.permission.INTERNET)
//        ActivityCompat.requestPermissions(this, permission1, INTERNET_PERMISSION_CODE)
//
//        val permission2 = arrayOf(android.Manifest.permission.ACCESS_NETWORK_STATE)
//        ActivityCompat.requestPermissions(this, permission2, ACCESS_NETWORK_STATE_PERMISSION_CODE)
//
//        val permission3 = arrayOf(android.Manifest.permission.POST_NOTIFICATIONS)
//        ActivityCompat.requestPermissions(this, permission3, ACCESS_NOTIFY_PERMISSION_CODE)

        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val isEnabled = notificationManager.areNotificationsEnabled()
        if (!isEnabled) {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
            this.startActivity(intent)
        }
    }

    fun checkNotificationPermissions(context: Context): Boolean {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check if notification permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val isEnabled = notificationManager.areNotificationsEnabled()

            if (!isEnabled) {
                // Open the app notification settings if notifications are not enabled
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                context.startActivity(intent)

                val intent2 = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent2.setData(Uri.parse("package:" + context.packageName))
                context.startActivity(intent2)

                return false
            }
        } else {
            val areEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()

            if (!areEnabled) {
                // Open the app notification settings if notifications are not enabled
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                context.startActivity(intent)

                return false
            }
        }

        // Permissions are granted
        return true
    }
}





fun println(tag: String, s: Any) {
    Log.i(tag, s.toString())
}
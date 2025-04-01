package com.example.rksishedule.data

import com.example.rksishedule.utils.Shedule
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import com.example.rksishedule.R
import com.example.rksishedule.utils.SheduleHistory


class RVhistoryAdapter (
    val context: Context,
    private var recyclerData: SheduleHistory
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val DAY_VIEW_TYPE = 0
    private val SHEDULE_VIEW_TYPE = 1

    private var dateClickListener: OnClickListener? = null

//    var viewType = -1

//    fun RVadapter(
//        item: Shedule,
//        dateClickListener: OnClickListener
//    ) {
//        this.recyclerData = item
//        this.dateClickListener = dateClickListener
//    }

    override fun getItemViewType(position: Int): Int {
//        kotlin.io.println("pos is $position")
        if (position == 0) return DAY_VIEW_TYPE
        if (position > 0) return SHEDULE_VIEW_TYPE
        throw IllegalArgumentException(
            "Can't find view type for position $position"
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var inflater: LayoutInflater? = LayoutInflater.from(parent.context)
        lateinit var holder: RecyclerView.ViewHolder
//        this.viewType++
        if (viewType == DAY_VIEW_TYPE) {
            holder = DayViewHolder(inflater!!.inflate(R.layout.card_day, parent, false))
        }
        else if (viewType == SHEDULE_VIEW_TYPE) {
            when(uView) {
                getString(context, R.string.code_one_el) -> holder = SheduleViewHolder(inflater!!.inflate(
                    R.layout.lesson_card_one, parent, false))
                getString(context, R.string.code_three_el) -> holder = SheduleViewHolder(inflater!!.inflate(
                    R.layout.lesson_card_three, parent, false))
                getString(context, R.string.code_easy_el) -> holder = SheduleViewHolder(inflater!!.inflate(
                    R.layout.lesson_card_easy, parent, false))
            }
        }

        else {
            throw IllegalArgumentException(
                "Can't create view holder from view type $viewType"
            )
        }

        return holder
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = recyclerData
        var viewType = getItemViewType(position)
        val positionLesson = position-1

//        kotlin.io.println("$viewType\nholder - $holder")
        if (viewType == DAY_VIEW_TYPE) {
            val dayViewHolder = holder as DayViewHolder

            if (uView == "easy") {
                dayViewHolder.day.textSize = 9F
            }
            dayViewHolder.day.text = "${item.getDays()[position].split(", ")[0]}\n${item.getDays()[position].split(", ")[1]}"
        }
        if (viewType == SHEDULE_VIEW_TYPE) {
            val sheduleViewHolder = holder as SheduleViewHolder
            if (item.getLessons()[positionLesson] == inviseLessonName && item.getDeleteLessons()[positionLesson] == inviseLessonName && !item.getAudiencies()[positionLesson].contains("ауд.")) {
                sheduleViewHolder.card.setBackgroundResource(R.color.full_transparent)

                sheduleViewHolder.lessonNumber.text = "пара ${item.getLessonsNumbers()[positionLesson]}"
                sheduleViewHolder.lessonNumber.setTextColor(ContextCompat.getColor(context,
                    R.color.full_text_transparent
                ))

                sheduleViewHolder.time.text = item.getTimeLessons()[positionLesson]
                sheduleViewHolder.time.setTextColor(ContextCompat.getColor(context,
                    R.color.full_text_transparent
                ))
            }
            else {
                sheduleViewHolder.lesson.text = item.getLessons()[positionLesson]
                if (item.getEditLessons()[positionLesson]) {
                    holder.container.setBackgroundResource(R.color.edit_lesson)
                }
                if (item.getLessons()[positionLesson] == inviseLessonName && item.getDeleteLessons()[positionLesson] != inviseLessonName ||  item.getLessons()[positionLesson] == inviseLessonName && item.getAudiencies()[positionLesson].contains("ауд.")) {
                    holder.container.setBackgroundResource(R.color.delete_lesson)
                    sheduleViewHolder.lesson.text = item.getDeleteLessons()[positionLesson]
                }
//                println("${item.getEditLessons()[position]}, $position, ${item.getLessons()[positionLesson]}, ${item.getDeleteLessons()[positionLesson]}, ${item.getDays()[positionLesson]}")
                sheduleViewHolder.audience.text = item.getAudiencies()[positionLesson]
                sheduleViewHolder.entity.text = item.getEntityes()[positionLesson]
                sheduleViewHolder.time.text = item.getTimeLessons()[positionLesson]
                sheduleViewHolder.lessonNumber.text = if (item.getLessonsNumbers()[positionLesson].isNotEmpty()) "пара ${item.getLessonsNumbers()[positionLesson]}" else "классный час"
            }
        }
//        else {
//            FunctionsApp().ToastRVerror(context, position)
//            throw IllegalArgumentException(
//                "Can't create bind holder from position $position"
//            )
//        }
    }

    override fun getItemCount(): Int {
        return recyclerData.getLessons().size+1
    }


    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val card = itemView.findViewById<CardView>(R.id.card)
        internal val day = itemView.findViewById<TextView>(R.id.tv_day)
    }

    inner class SheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener {
        internal val card = itemView.findViewById<CardView>(R.id.card)
        internal val container = itemView.findViewById<RelativeLayout>(R.id.rl_container)
        internal val lesson = itemView.findViewById<TextView>(R.id.tv_lesson)
        internal val audience = itemView.findViewById<TextView>(R.id.tv_audience)
        internal val entity = itemView.findViewById<TextView>(R.id.tv_entity)
        internal val time = itemView.findViewById<TextView>(R.id.tv_lesson_time)
        internal val lessonNumber = itemView.findViewById<TextView>(R.id.lesson_number)

        init {
            card.setOnClickListener {
                functionsApp.showHistoryPopupItem(context, recyclerData, position-1)
            }
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }
    }


}
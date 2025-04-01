package com.example.rksishedule.data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rksishedule.R
import com.example.rksishedule.utils.FunctionsApp

class RVweekAdapter (
    private val context: Context,
    private val recyclerDataArrayList: MutableList<RecyclerView>
) : RecyclerView.Adapter<RVweekAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.day_rv, parent, false)
        return RecyclerViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = recyclerDataArrayList[position]
        val functionsApp = FunctionsApp()
        when (uView) {
            getString(context, R.string.code_three_el) -> holder.card.layoutParams = RelativeLayout.LayoutParams(functionsApp.dpToPx(context, 138), RelativeLayout.LayoutParams.MATCH_PARENT)
            getString(context, R.string.code_easy_el) -> holder.card.layoutParams = RelativeLayout.LayoutParams(functionsApp.dpToPx(context, 70), RelativeLayout.LayoutParams.MATCH_PARENT)
        }
        holder.RVday.layoutManager = GridLayoutManager(context, 1)
        item.setHasFixedSize(true)
        holder.RVday.adapter = item.adapter


    }

    override fun getItemCount(): Int {
        return recyclerDataArrayList.size
    }

    inner class RecyclerViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        internal val card = itemView.findViewById<CardView>(R.id.card)
        internal val RVday = itemView.findViewById<RecyclerView>(R.id.rv_day)

        init {

//            itemView.alpha = 0.5F
        }
    }


}
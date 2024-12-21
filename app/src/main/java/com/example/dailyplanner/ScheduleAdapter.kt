package com.example.dailyplanner

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColor
import java.time.format.DateTimeFormatter

class ScheduleAdapter(context: Context, listSchedule: List<Schedule>) :
    ArrayAdapter<Schedule?>(context, R.layout.list_item, listSchedule)
{
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val listSchedule: List<Schedule> = listSchedule

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val view = inflater.inflate(R.layout.list_item, parent, false)

        val titleView = view.findViewById<TextView>(R.id.title)
        val descriptionView = view.findViewById<TextView>(R.id.description)
        val dateView = view.findViewById<TextView>(R.id.date)
        val timeView = view.findViewById<TextView>(R.id.time)
        val checkBoxIsDone = view.findViewById<CheckBox>(R.id.checkBoxIsDone)
        val item = view.findViewById<ConstraintLayout>(R.id.item)


        val currentNightMode: Int = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO)
        {
            titleView.setTextColor(Color.BLACK)
            descriptionView.setTextColor(Color.BLACK)
            dateView.setTextColor(Color.BLACK)
            timeView.setTextColor(Color.BLACK)
        }
        else if(currentNightMode == Configuration.UI_MODE_NIGHT_YES)
        {
            titleView.setTextColor(Color.WHITE)
            descriptionView.setTextColor(Color.WHITE)
            dateView.setTextColor(Color.WHITE)
            timeView.setTextColor(Color.WHITE)
        }

        val schedule: Schedule = listSchedule[position]

        titleView.text = schedule.title
        descriptionView.text = schedule.description
        dateView.text = schedule.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        timeView.text = schedule.time.format(DateTimeFormatter.ofPattern("HH:mm"))
        if(schedule.idPriority == 1)
        {
            item.setBackgroundColor(Color.RED)
        }
        else if(schedule.idPriority == 2)
        {
            item.setBackgroundColor(Color.rgb(255, 165, 0))
        }
        else
        {
            item.setBackgroundColor(Color.rgb(0,128,0))
        }

        if(schedule.isDone == 1)
            checkBoxIsDone.isChecked = true
        else
            checkBoxIsDone.isChecked = false

        return view
    }
}
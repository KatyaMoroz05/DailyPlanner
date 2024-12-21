package com.example.dailyplanner.ui.schedule

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailyplanner.Schedule

class ScheduleViewModel : ViewModel()
{
    private val _listSchedule = MutableLiveData<List<Schedule>>()

    fun setSchedule(input: List<Schedule>)
    {
        _listSchedule.value = input
    }
    fun getSchedule(): MutableLiveData<List<Schedule>> = _listSchedule
}
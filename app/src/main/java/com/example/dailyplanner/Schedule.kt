package com.example.dailyplanner

import java.time.LocalDate
import java.time.LocalTime

data class Schedule(var id: Int, var date: LocalDate, var time: LocalTime,
                    var title: String, var description: String, var idPriority: Int,
                    var isDone: Int, var isDeleted: Int)

package com.example.dailyplanner

import java.time.LocalDate

data class User(var id: Int, var name: String, var gender: Int,
                var birthday: LocalDate )
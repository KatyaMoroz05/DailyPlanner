package com.example.dailyplanner.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailyplanner.User

class ProfileViewModel : ViewModel()
{
    private val _user = MutableLiveData<User>()
    fun setUser(input: User)
    {
        _user.value = input
    }
    fun getUser(): MutableLiveData<User> = _user
}
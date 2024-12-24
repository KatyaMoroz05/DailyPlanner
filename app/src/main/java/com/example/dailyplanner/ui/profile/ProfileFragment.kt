package com.example.dailyplanner.ui.profile

import android.content.res.Configuration
import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.dailyplanner.DatabaseHelper
import com.example.dailyplanner.R
import com.example.dailyplanner.User
import com.example.dailyplanner.databinding.FragmentProfileBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var profileViewModel: ProfileViewModel? = null
    private var profilePhoto: ImageView? = null
    private var editTextName: EditText? = null
    private var editTextGender: EditText? = null
    private var editTextBirthday: EditText? = null
    private var textViewTheme: TextView? = null
    private var buttonUpdate: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        profilePhoto = binding.profilePhoto
        editTextName = binding.editTextName
        editTextGender = binding.editTextGender
        editTextBirthday = binding.editTextBirthday
        textViewTheme = binding.textViewTheme
        buttonUpdate = binding.buttonUpdate
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        updateProfile()

        buttonUpdate!!.setOnClickListener {
            if(editTextName!!.text.isNotEmpty() && editTextGender!!.text.isNotEmpty() &&
                editTextBirthday!!.text.isNotEmpty())
            {
                val gender: Int
                val birthday: LocalDate
                val db = activity?.applicationContext?.let { DatabaseHelper(it, null) }

                if(db == null)
                {
                    return@setOnClickListener
                }
                try
                {
                    gender = db.selectIdInGendersByGender(editTextGender!!.text.toString())
                }
                catch (ex: Exception)
                {
                    val toast: Toast = Toast.makeText(activity?.applicationContext,
                        "Incorrect gender format!", Toast.LENGTH_LONG)
                    toast.show()
                    return@setOnClickListener
                }

                try
                {
                    birthday = LocalDate.parse(editTextBirthday!!.text,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                }
                catch (ex: DateTimeParseException)
                {
                    val toast: Toast = Toast.makeText(activity?.applicationContext,
                        "Incorrect date format!", Toast.LENGTH_LONG)
                    toast.show()
                    return@setOnClickListener
                }
                if(profileViewModel!!.getUser().value == null)
                {
                    db.addUser(
                        name = editTextName!!.text.toString(),
                        gender = gender,
                        birthday = birthday
                    )
                }
                else
                {
                    db.updateUser(
                        id = profileViewModel!!.getUser().value!!.id,
                        name = editTextName!!.text.toString(),
                        gender = gender,
                        birthday = birthday
                    )
                }

                db.close()
                val toast: Toast = Toast.makeText(activity?.applicationContext,
                    "The data is recorded!", Toast.LENGTH_LONG)
                toast.show()
            }
            else
            {
                val toast: Toast = Toast.makeText(activity?.applicationContext,
                    "Fill in all the fields!", Toast.LENGTH_LONG)
                toast.show()
            }

            updateProfile()
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        updateProfile()
    }

    private fun updateProfile()
    {
        val db = activity?.applicationContext?.let { DatabaseHelper(it, null) }
        if(db == null)
        {
            return
        }

        val cursor = db.getUsers()
        if (cursor.moveToFirst())
        {
            val user: User = User(
                id = cursor.getInt(0),
                name = cursor.getString(1),
                gender = cursor.getInt(2),
                birthday = LocalDate.parse(cursor.getString(3))
            )
            profileViewModel!!.setUser(user)
        }

        val currentNightMode: Int = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO)
        {
            textViewTheme!!.setText(R.string.light)
        }
        else if(currentNightMode == Configuration.UI_MODE_NIGHT_YES)
        {
            textViewTheme!!.setText(R.string.dark)
        }

        profileViewModel!!.getUser().observe(viewLifecycleOwner)
        {
            editTextName?.setText(it.name)

            val gender = db.selectGenderInGendersById(it.gender)
            editTextGender?.setText(gender)

            if(gender == "Woman")
            {
                profilePhoto!!.setImageResource(R.drawable.icon_user_woman)
            }
            else
            {
                profilePhoto!!.setImageResource(R.drawable.icon_user_man)
            }

            editTextBirthday?.setText(it.birthday.format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy")))
        }

    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }
}
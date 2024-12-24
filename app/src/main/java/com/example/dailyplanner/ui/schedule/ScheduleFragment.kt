package com.example.dailyplanner.ui.schedule

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dailyplanner.DatabaseHelper
import com.example.dailyplanner.R
import com.example.dailyplanner.Schedule
import com.example.dailyplanner.ScheduleAdapter
import com.example.dailyplanner.databinding.FragmentScheduleBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar


class ScheduleFragment : Fragment()
{
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private lateinit var dateEdt: EditText
    private var scheduleViewModel: ScheduleViewModel? = null
    private var listViewSchedule: ListView? = null
    private var buttonAdd: Button? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        scheduleViewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
        dateEdt = binding.idEdtDate
        listViewSchedule = binding.listViewSchedule
        buttonAdd = binding.buttonAdd


        buttonAdd!!.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Filling")

            val title = EditText(context)
            val description = EditText(context)
            val priority = EditText(context)
            val timePicker = TimePicker(context)
            val linearLayout = LinearLayout(context)

            title.inputType = InputType.TYPE_CLASS_TEXT
            description.inputType = InputType.TYPE_CLASS_TEXT
            priority.inputType = InputType.TYPE_CLASS_NUMBER
            title.hint = "Input title"
            description.hint = "Input description"
            priority.hint = "Input priority (1 - !!!, 2 - !!, 3 - !)"

            linearLayout.orientation = LinearLayout.VERTICAL

            linearLayout.addView(title)
            linearLayout.addView(description)
            linearLayout.addView(priority)
            linearLayout.addView(timePicker)

            builder.setView(linearLayout)

            val db = activity?.let { DatabaseHelper(it.applicationContext, null) }

            builder.setPositiveButton("OK",
                DialogInterface.OnClickListener{
                   dialog,
                   which ->
                    var date: LocalDate
                    var time: LocalTime

                    if(priority.text.isEmpty())
                    {
                        val toast: Toast = Toast.makeText(activity?.applicationContext,
                            "Fill in the priority field!", Toast.LENGTH_LONG)
                        toast.show()
                        return@OnClickListener
                    }

                    val priorityId = priority.text.toString().toInt()
                    if(priorityId < 1 || priorityId > 3)
                    {
                        val toast: Toast = Toast.makeText(activity?.applicationContext,
                            "Unknown priority!", Toast.LENGTH_LONG)
                        toast.show()
                        return@OnClickListener
                    }

                    try
                    {
                        date = LocalDate.parse(dateEdt.text,
                            DateTimeFormatter.ofPattern("dd.MM.yyyy"))

                        val hourStr: String
                        val minuteStr: String

                        if(timePicker.hour < 10)
                            hourStr = "0${timePicker.hour}"
                        else
                            hourStr = timePicker.hour.toString()

                        if(timePicker.minute < 10)
                            minuteStr = "0${timePicker.minute}"
                        else
                            minuteStr = (timePicker.minute).toString()
                        time = LocalTime.parse("${hourStr}:${minuteStr}",
                            DateTimeFormatter.ofPattern("HH:mm"))
                    }
                    catch (ex: DateTimeParseException)
                    {
                        val toast: Toast = Toast.makeText(activity?.applicationContext,
                            "Incorrect date format!", Toast.LENGTH_LONG)
                        toast.show()
                        return@OnClickListener
                    }

                    db?.addScheduleRecord(
                        date,
                        time,
                        title.text.toString(),
                        description.text.toString(),
                        priorityId)
                    updateListViewSchedule(date)
                })
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            builder.show()
        }

        dateEdt.setOnClickListener {
            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    val dayOfMonthStr: String
                    val monthOfYearStr: String

                    if(dayOfMonth < 10)
                        dayOfMonthStr = "0$dayOfMonth"
                    else
                        dayOfMonthStr = dayOfMonth.toString()

                    if(monthOfYear < 10)
                        monthOfYearStr = "0${monthOfYear + 1}"
                    else
                        monthOfYearStr = (monthOfYear + 1).toString()
                    val date = ("$dayOfMonthStr.$monthOfYearStr.$year")
                    dateEdt.setText(date)
                    updateListViewSchedule(LocalDate.parse(date,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        listViewSchedule!!.setOnItemClickListener { parent, view, position, id ->
            val checkBox = view.findViewById<CheckBox>(R.id.checkBoxIsDone)

            val date = LocalDate.parse(dateEdt.text,
                DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            val db = activity?.let { DatabaseHelper(it.applicationContext, null) }
            val list = scheduleViewModel!!.getSchedule().value?.filter {
                it.date == date && it.isDeleted == 0
            }

            val record = list?.get(position)
            if (record != null)
            {
                db?.setIsDoneScheduleRecord(record.id, !checkBox.isChecked)
            }

            updateListViewSchedule(date)
        }

        listViewSchedule!!.setOnItemLongClickListener { parent, view, position, id ->

            val item = parent.getItemAtPosition(position)
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Delete?")
            builder.setPositiveButton("OK",
                DialogInterface.OnClickListener{
                        dialog,
                        which ->
                    /*val date = LocalDate.parse(dateEdt.text,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy"))

                    val db = activity?.let { DatabaseHelper(it.applicationContext, null) }
                    var record: Schedule
                    scheduleViewModel!!.getSchedule().observe(viewLifecycleOwner)
                    { it ->
                        record = it.filter {
                            it.date == date }[position]
                        db?.setDeletedScheduleRecord(record.id)
                    }
                    updateListViewSchedule(date)*/
                })
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            builder.show()
            true
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val date = LocalDate.now()
        dateEdt.setText(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
        updateListViewSchedule(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateListViewSchedule(date: LocalDate?)
    {
        val db = activity?.let { DatabaseHelper(it.applicationContext, null) }
        val listSchedule: MutableList<Schedule> = mutableListOf()

        val cursor = db?.getSchedule()
        if(cursor?.moveToFirst() == false)
        {
            return
        }
        do
        {
            if(cursor == null)
            {
                return
            }
            val schedule: Schedule = Schedule(
                id = cursor.getInt(0),
                date = LocalDateTime.parse(cursor.getString(1)).toLocalDate(),
                time = LocalDateTime.parse(cursor.getString(1)).toLocalTime(),
                title = cursor.getString(2),
                description = cursor.getString(3),
                idPriority = cursor.getInt(4),
                isDone = cursor.getInt(5),
                isDeleted = cursor.getInt(6)
            )

            listSchedule.add(schedule)
        }
        while (cursor?.moveToNext() == true)

        scheduleViewModel!!.setSchedule(listSchedule)

        scheduleViewModel!!.getSchedule().observe(viewLifecycleOwner)
        {
            val adapter = activity?.applicationContext?.let {
                    it1 -> ScheduleAdapter(it1, it.filter { it.date == date && it.isDeleted == 0 })
            }

            listViewSchedule!!.adapter = adapter
        }

    }
}
package com.henriqueapps.administraoDeApartamentos.date

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(val callback: (result: String) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener{

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val year: Int = c.get(Calendar.YEAR)
        val month: Int = c.get(Calendar.MONTH)
        val day: Int = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(picker: DatePicker?, year: Int, month: Int, day: Int) {
        var monthString: String
        var dayString: String
        val yearString = year.toString()

        if ((month + 1) in 1..9){
            monthString = "0${month+1}"
        }else{
            monthString = (month+1).toString()
        }
        if (day in 1..9){
            dayString = "0${day}"
        }else{
            dayString = day.toString()
        }

        callback("$dayString/$monthString/$yearString")
    }
}
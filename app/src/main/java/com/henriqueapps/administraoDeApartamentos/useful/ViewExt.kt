package com.henriqueapps.administraoDeApartamentos.useful

import android.view.KeyEvent
import android.widget.EditText
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/** escuta de enter */
fun EditText.setOnEnterKeyListener(action: () -> Unit = {}) {
    setOnKeyListener { _, keyCode, event ->
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
            action()
            return@setOnKeyListener true
        }
        return@setOnKeyListener false
    }
}

fun decimalFormat(number: Double): String {

    val standart = "###,###.00"

    val decimalFormatSymbols = DecimalFormatSymbols(Locale("pt", "Brazil"))
    decimalFormatSymbols.decimalSeparator = ','
    decimalFormatSymbols.groupingSeparator = '.'

    val df = DecimalFormat(standart, decimalFormatSymbols)

    return df.format(number)
}

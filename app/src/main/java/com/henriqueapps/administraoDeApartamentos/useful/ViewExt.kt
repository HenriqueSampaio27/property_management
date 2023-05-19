package com.henriqueapps.administraoDeApartamentos.useful

import android.view.KeyEvent
import android.widget.EditText

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

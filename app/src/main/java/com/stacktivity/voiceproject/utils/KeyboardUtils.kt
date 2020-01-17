package com.stacktivity.voiceproject.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.stacktivity.voiceproject.App

fun showKeyboard(editText: EditText) {
    val imm = App.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun hideKeyboard(editText: EditText) {
    val imm = App.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(editText.windowToken, 0)
}
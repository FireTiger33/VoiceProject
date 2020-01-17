package com.stacktivity.voiceproject.utils

import android.content.Context
import android.widget.EditText
import com.stacktivity.voiceproject.R
import java.nio.charset.Charset
import java.util.regex.Pattern

fun isEnteredTextValid(
    context: Context, editText: EditText, resFieldName: Int,
    isFullName: Boolean, useCyrillic: Boolean
): Boolean {
    val text = editText.text.toString().trim { it <= ' ' }
    if (text.isEmpty()) {
        return false
    }
    val letters = if(useCyrillic) "a-zA-Zа-яА-ЯёЁ" else "a-zA-Z"
    val specSym: String = if (isFullName) "" else " "
    val p: Pattern = Pattern.compile("^[$letters][${letters}0-9$specSym]{2," + (MAX_FULLNAME_LENGTH - 1) + "}+$")
    val m = p.matcher(text)

    return if (!m.matches()) {
        showError(editText, context, resFieldName)
        false
    } else {
        true
    }
}

fun isLoginValid(context: Context, editText: EditText): Boolean {
    return isEnteredTextValid(context, editText, R.string.login,
        isFullName = true,
        useCyrillic = false
    )
//        return String(text.toByteArray(Charsets.UTF_8), Charsets.UTF_8)
}

fun isFoolNameValid(context: Context, editText: EditText): Boolean {
    return isEnteredTextValid(context, editText, R.string.full_name,
        isFullName = false,
        useCyrillic = true
    )
}

fun isPassValid(context: Context, editText: EditText): Boolean {
    return isEnteredTextValid(context, editText, R.string.password,
        isFullName = false,
        useCyrillic = false
    )
}

fun showError(editText: EditText, context: Context, resFieldName: Int) {
    editText.error = String.format(context.getString(R.string.error_name_must_not_contain_special_characters_from_app),
        context.getString(resFieldName),
        MAX_FULLNAME_LENGTH)
}
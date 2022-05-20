package org.sopt.daangnmarket_android.util

import android.content.Context
import android.widget.Toast

var toast: Toast? = null
fun Context.shortToast(message: String) {
    when (toast) {
        null -> toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        else -> toast?.setText(message)
    }
    toast?.show()
}
package com.siscofran.loplop.utils

import android.content.Context
import android.util.Log

fun logi(msg: String){
    Log.i("==LOPLOP==", "logi: $msg")
}

fun loge(msg: String){
    Log.e("==LOPLOP==", "loge: $msg")
}

fun Context.saveName(name: String, date: String) {
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    pref.edit().putString("name", name).apply()
    pref.edit().putString("date", date).apply()
}

fun Context.prefGetDate(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("date", "") ?: ""
}
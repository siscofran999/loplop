package com.siscofran.loplop.utils

import android.content.Context
import android.util.Log
import java.util.*

fun logi(msg: String){
    Log.i("==LOPLOP==", "logi: $msg")
}

fun loge(msg: String){
    Log.e("==LOPLOP==", "loge: $msg")
}

fun getAge(year: Int, month: Int, day: Int): Int {
    val dob = Calendar.getInstance()
    val today = Calendar.getInstance()

    dob.set(year, month, day)

    var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) age--

    return age
}

fun Context.saveName(name: String, date: String) {
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    pref.edit().putString("name", name).apply()
    pref.edit().putString("date", date).apply()
}

fun Context.saveGender(gender: String, interest: String) {
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    pref.edit().putString("gender", gender).apply()
    pref.edit().putString("interest", interest).apply()
}

fun Context.saveImage(position: Int, image: String) {
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    pref.edit().putString("image$position", image).apply()
}

fun Context.prefGetDate(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("date", "") ?: ""
}

fun Context.prefGetGender(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("gender", "") ?: ""
}

fun Context.prefGetInterest(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("interest", "") ?: ""
}

fun Context.prefGetImage1(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("image1", "") ?: ""
}

fun Context.prefGetImage2(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("image2", "") ?: ""
}

fun Context.prefGetImage3(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("image3", "") ?: ""
}

fun Context.prefGetImage4(): String{
    val pref = this.getSharedPreferences("profile", Context.MODE_PRIVATE)
    return pref.getString("image4", "") ?: ""
}


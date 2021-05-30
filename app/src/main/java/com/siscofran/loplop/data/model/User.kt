package com.siscofran.loplop.data.model

data class User(
        val name: String = "",
        val dateBorn: String = "",
        val gender: String = "",
        val interest: String = "",
        val photo: ArrayList<String> = ArrayList(),
        val hobby: ArrayList<String> = ArrayList(),
        val email: String = ""
)
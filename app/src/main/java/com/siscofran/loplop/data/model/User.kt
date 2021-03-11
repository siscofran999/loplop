package com.siscofran.loplop.data.model

data class User(
        val name: String,
        val dateBorn: String,
        val gender: String,
        val interest: String,
        val photo: ArrayList<String>,
        val hobby: ArrayList<String>,
        val email: String
)
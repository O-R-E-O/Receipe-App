package com.example.assignment2_MAD_19012021092

data class User(
    val uid: String = "",
    val userName: String = "",
    val contactNumber : String = "",
    val bio: String = "",
    val createdReceipes: ArrayList<String> = arrayListOf()
)

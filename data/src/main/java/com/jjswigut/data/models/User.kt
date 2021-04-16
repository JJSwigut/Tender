package com.jjswigut.data.models

data class User(
    val userId: String? = null,
    val email: String? = null,
    val profilePhotoUrl: String? = null,
    val name: String? = null,
    val userEvents: ArrayList<Event>? = arrayListOf(),
    val userGroups: ArrayList<Group>? = arrayListOf()
)  
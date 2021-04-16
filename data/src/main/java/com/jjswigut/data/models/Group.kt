package com.jjswigut.data.models

data class Group(
    val groupId: String? = null,
    val groupName: String? = null,
    val users: List<MinimalUser>? = null
)

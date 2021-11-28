package com.example.finalassignment.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "active_user_table")
data class ActiveUser(
    @PrimaryKey
    val id: Int,
    var active: String

)



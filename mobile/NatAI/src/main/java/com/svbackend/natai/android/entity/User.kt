package com.svbackend.natai.android.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

// logged in user.
@Entity(indices = [Index(value = ["cloudId"], unique = true)])
data class User(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var cloudId: String,
    var email: String,
    var name: String,
    var apiToken: String,

) {

}


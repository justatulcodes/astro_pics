package com.expeknow.nasabrowser.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageData (

    @PrimaryKey(autoGenerate = true)
    var id : Int,

    var title : String,
    var date : String,
    var thumbUrl : String,
    var hdUrl : String,
    var description: String

)
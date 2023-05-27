package com.expeknow.nasabrowser.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImagesDao {

    @Insert
    suspend fun addImage(image: ImageData)

    @Query("SELECT * FROM images")
    fun getAllImagse() : LiveData<List<ImageData>>

    @Query("DELETE FROM images WHERE thumbUrl = :url")
    fun deleteImageByUrl(url: String)

    @Query("SELECT EXISTS (SELECT * FROM images WHERE thumbUrl = :url)")
    fun isAlreadySaved(url: String) : Boolean

    @Delete
    suspend fun deleteImage(image: ImageData)
}
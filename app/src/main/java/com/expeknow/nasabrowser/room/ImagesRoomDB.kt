package com.expeknow.nasabrowser.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(ImageData::class)], version = 1)
abstract class ImagesRoomDB : RoomDatabase() {

    abstract fun imagesDao() : ImagesDao

    companion object {
        @Volatile
        private var INSTANCE : ImagesRoomDB? = null

        fun getInstance(context: Context): ImagesRoomDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImagesRoomDB::class.java,
                    "images"
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                instance
            }
        }
    }
}
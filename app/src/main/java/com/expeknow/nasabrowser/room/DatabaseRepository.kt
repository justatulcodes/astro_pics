package com.expeknow.nasabrowser.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseRepository(private val imagesDao: ImagesDao) {

    val allImages : LiveData<List<ImageData>> = imagesDao.getAllImagse()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun addImageData(newImageData: ImageData) {
        coroutineScope.launch(Dispatchers.IO) {
            if (!imagesDao.isAlreadySaved(newImageData.thumbUrl)) {
                imagesDao.addImage(newImageData)
            }
        }
    }

    fun deleteImageByUrl(url: String) {
        coroutineScope.launch(Dispatchers.IO) {
            imagesDao.deleteImageByUrl(url)
        }
    }

}
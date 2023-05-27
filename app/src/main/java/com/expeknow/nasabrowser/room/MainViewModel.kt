package com.expeknow.nasabrowser.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainViewModel(application: Application) : AndroidViewModel(application) {

    val allImages : LiveData<List<ImageData>>
    private val repository: DatabaseRepository

    init {
        val imagesDao = ImagesRoomDB.getInstance(application).imagesDao()
        repository = DatabaseRepository(imagesDao)
        allImages = repository.allImages

    }

    fun deleteImageByUrl(url: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteImageByUrl(url)
        }
    }


    fun addImage(imageData: ImageData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addImageData(imageData)
        }
    }

}

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
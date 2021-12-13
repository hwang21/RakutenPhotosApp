package com.rakuten.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.rakuten.photos.model.Photo

class DetailViewModel(application: Application): AndroidViewModel(application) {

    val photo = MutableLiveData<Photo>()

    fun setPhoto(_photo: Photo) {
        photo.value = _photo
    }

}
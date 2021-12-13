package com.rakuten.photos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.rakuten.photos.api.RetrofitInstance
import com.rakuten.photos.repository.Repository
import com.rakuten.photos.utils.Resource
import kotlinx.coroutines.Dispatchers

class MainViewModel(application: Application): AndroidViewModel(application) {

    val title = MutableLiveData<String>()

    private val repository: Repository = Repository(RetrofitInstance.apiService)

    fun setTitle(_title: String) {
        title.value = _title
    }

    fun getData() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val data = repository.getData()
            emit(Resource.success(data = data.photos.photo))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}
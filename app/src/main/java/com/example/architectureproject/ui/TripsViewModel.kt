package com.example.architectureproject.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.architectureproject.data.models.Trip
import com.example.architectureproject.data.repository.TripRepository
import com.example.architectureproject.ui.location_character.LocationUpdatesLiveData
import kotlinx.coroutines.launch

class TripsViewModel(application : Application) : AndroidViewModel(application) {

    private val repository = TripRepository(application)
    val trips : LiveData<List<Trip>>? = repository.getTrips()

    val location : LiveData<String> = LocationUpdatesLiveData(application)

    private val _locationVM = MutableLiveData<String>()
    val locationVM : LiveData<String> get() = _locationVM

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri : LiveData<Uri?> get() = _imageUri

    private val _title = MutableLiveData<String>()
    val title : LiveData<String> get() = _title

    private val _description = MutableLiveData<String>()
    val description : LiveData<String> get() = _description

    private val _imageLabeling = MutableLiveData<String>()
    val imageLabeling : LiveData<String> get() = _imageLabeling

    fun setTitle(title : String){
        _title.postValue(title)
    }

    fun setDescription(description : String){
        _description.postValue(description)
    }

    fun setImageLabeling(imageLabeling : String){
        _imageLabeling.postValue(imageLabeling)
    }

    fun setImageUri(uri : Uri?){
        _imageUri.postValue(uri)
    }

    fun setLocation(location : String){
        _locationVM.postValue(location)
    }


    fun addTrip(trip : Trip){
        viewModelScope.launch {
            repository.addTrip(trip)
        }
    }

    fun deleteTrip(trip : Trip){
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }

    fun clearTemporaryData() {
        null.also { _imageUri.value = it }
        _title.value = ""
        _description.value = ""
    }

    private val _chosenTrip  = MutableLiveData<Trip>()
    val chosenTrip : LiveData<Trip> get() = _chosenTrip

    fun setTrip(trip : Trip){
        _chosenTrip.value = trip
    }


    fun updateTrip(trip: Trip, newTitle: String, newDescription: String, newPhoto: String?, newLocation: String) {
        trip.title = newTitle
        trip.description = newDescription
        trip.photo = newPhoto
        trip.location = newLocation
        viewModelScope.launch {
            repository.updateTrip(trip)
        }
        _chosenTrip.value = trip
    }


}
package com.example.architectureproject.data.repository

import android.app.Application
import com.example.architectureproject.data.local_db.TripDao
import com.example.architectureproject.data.local_db.TripDataBase
import com.example.architectureproject.data.models.Trip

class TripRepository(application: Application) {

    private var tripDao : TripDao?

    init {
        val db = TripDataBase.getDatabase(application.applicationContext)
        tripDao = db?.tripsDao()
    }

    fun getTrips() = tripDao?.getTrips()

    suspend fun addTrip(trip : Trip){
        tripDao?.addTrip(trip)
    }

    suspend fun updateTrip(trip : Trip){
        tripDao?.updateTrip(trip)
    }

    suspend fun deleteTrip(trip : Trip){
        tripDao?.deleteTrip(trip)
    }
}
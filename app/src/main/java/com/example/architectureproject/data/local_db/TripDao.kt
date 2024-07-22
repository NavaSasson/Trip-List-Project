package com.example.architectureproject.data.local_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.architectureproject.data.models.Trip

@Dao
interface TripDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrip(trip : Trip)

    @Delete
    suspend fun deleteTrip(vararg trip: Trip)

    @Update
    suspend fun updateTrip(trip : Trip)

    @Query("SELECT * FROM trips ORDER BY trip_title ASC")
    fun getTrips() : LiveData<List<Trip>>

    @Query("SELECT * FROM trips WHERE id LIKE :id")
    fun getTrip(id : Int) : Trip
}
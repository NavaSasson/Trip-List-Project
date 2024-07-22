package com.example.architectureproject.data.local_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.architectureproject.data.models.Trip

@Database(entities = [Trip::class], version = 1, exportSchema = false)
abstract class TripDataBase : RoomDatabase(){

    abstract fun tripsDao() : TripDao

    companion object{

        @Volatile
        private var instance:TripDataBase? = null

        fun getDatabase(context: Context) = instance?: synchronized(this){
            Room.databaseBuilder(context.applicationContext, TripDataBase::class.java, "trips_database")
                .build()

        }
    }
}
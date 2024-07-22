package com.example.architectureproject.data.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "trips")
data class Trip(
    @ColumnInfo(name = "trip_title")
    var title : String,

    @ColumnInfo(name = "trip_description")
    var description:String,

    @ColumnInfo(name = "image")
    var photo:String?,

    @ColumnInfo(name = "trip_location")
    var location:String) : Parcelable {

    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true) //create primary key to any row in the table in ascending order and manage it itself
    var id : Int = 0
}
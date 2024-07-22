package com.example.architectureproject.ui.location_character

import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LocationUpdatesLiveData(context: Context) : LiveData<String>() {

    private val locationClient : FusedLocationProviderClient
        = LocationServices.getFusedLocationProviderClient(context)

    private val geocoder by lazy {
        Geocoder(context)
    }

    private val job = Job()

    private val scope = CoroutineScope(job + Dispatchers.IO)

    private val locationRequest = LocationRequest.create().apply {
        priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let {

                scope.launch {
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    //postValue(addresses!![0].countryName)
                    //postValue(addresses!![0].getAddressLine(0))
                    val address = addresses?.get(0)
                    address?.let {
                        val country = address.countryName ?: "Unknown Country"
                        val city = address.locality ?: "Unknown City"
                        postValue("$city, $country")
                    }
                }
            }
        }
    }

    override fun onActive() {
        super.onActive()
        try {
            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }catch (e: SecurityException) {
            Log.d("LocationUpdatesLiveData", "Missing Location Permission")
        }
    }

    override fun onInactive() {
        super.onInactive()
        job.cancel()
        locationClient.removeLocationUpdates(locationCallback)
    }
}
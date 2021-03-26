package com.jjswigut.search.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class LocationHelper {

    var userLocation: Location? = null


    private val REQUEST_LOCATION_PERMISSION = 1


    private fun requestLocationPermission(activity: Activity) {
        requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )
    }

    fun getLocationPermission(activity: Activity): Boolean {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission(activity)
            return false
        }
        return true
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(
        flc: FusedLocationProviderClient,
        activity: Activity
    ) {
        if (getLocationPermission(activity)) {
            flc.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    userLocation = location
                } else {
                    requestCurrentLocation(flc, activity)
                }
            }
        } else requestLocationPermission(activity)
    }

    @SuppressLint("MissingPermission")
    fun requestCurrentLocation(
        flc: FusedLocationProviderClient,
        context: Context
    ) {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 20 * 1000
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (locationResult.locations.isNotEmpty()) {
                    locationResult.locations.firstOrNull()?.let {
                        userLocation = it
                    }
                } else Toast.makeText(
                    context,
                    "Unable to get Location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        flc.removeLocationUpdates(locationCallback)
    }
}
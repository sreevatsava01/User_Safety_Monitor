package com.example.myapplication

import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingRequest
//import com.google.maps.model.LatLng
import com.google.android.gms.maps.model.LatLng


class GeofenceHelper(base: Context?) : ContextWrapper(base) {
    private var geofence: Geofence? = null
    private var pendingIntent: PendingIntent? = null

    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).build()
    }

    fun getGeofence(ID: String, latLng: LatLng, radius: Float, transitionTypes: Int): Geofence {
        return Geofence.Builder().setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setRequestId(ID).setTransitionTypes(transitionTypes).setLoiteringDelay(5000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE).build()
    }

    fun getPendingIntent(): PendingIntent? {
        if (pendingIntent != null) {
            return pendingIntent
        }
        val intent = Intent(this, MapsActivity::class.java)
        pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        return pendingIntent
    }

    fun getErrorString(e: Exception): String {
        if (e is ApiException) {
            when (e.statusCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return "GEOFENCE_NOT_AVAILABLE"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return "GEOFENCE_TOO_MANY_GEOFENCES"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return "GEOFENCE_TOO_MANY_PENDING_INTENTS"
            }
        }
        return e.localizedMessage
    }

    companion object {
        private const val TAG = "GeofenceHelper"
    }
}
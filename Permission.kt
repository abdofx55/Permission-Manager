package com.example.newcanalcollection.utils

import android.Manifest.permission.*

sealed class Permission(vararg val permissions: String) {
    // Individual permissions
    object Camera : Permission(CAMERA)

    // Bundled permissions
    object AllPermissions : Permission(
        ACCESS_NETWORK_STATE,
        INTERNET,
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE,
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION
    )


    // Grouped permissions
    object Network : Permission(ACCESS_NETWORK_STATE, INTERNET)
    object Storage : Permission(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
    object Location : Permission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)


    companion object {
        fun from(permission: String) = when (permission) {
            ACCESS_NETWORK_STATE, INTERNET -> Network
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE -> Storage
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION -> Location
            CAMERA -> Camera
            else -> throw IllegalArgumentException("Unknown permission: $permission")
        }
    }
}

package com.hansung.capstone.retrofit

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.checkSelfPermission

object Permissions {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val permissionsLocation = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS
    )
    val permissionsCamera = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}

object PermissionUtils {
    fun checkPermissions(activity: Activity, perms: Array<String>): List<String> {
        return perms.filter {
            checkSelfPermission(
                activity,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
    }
}
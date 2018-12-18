package com.gmediasolutions.anurakti.alert

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

/**
 * Created by Arpita Patel on 16-11-2018
 */

class CheckPermission {

    val permissionCode = 100

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    internal fun ischeckandrequestPermission(appCompatActivity: AppCompatActivity): Boolean {

        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
            Manifest.permission.GET_ACCOUNTS, Manifest.permission.CALL_PHONE
        )

        val listPermissionNeeded = ArrayList<String>()
        for (permision in permissions) {
            if (ContextCompat.checkSelfPermission(appCompatActivity, permision) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permision)
            }
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                appCompatActivity,
                listPermissionNeeded.toArray(arrayOfNulls(listPermissionNeeded.size)),
                permissionCode
            )
            return false
        }
        return true

    }


}
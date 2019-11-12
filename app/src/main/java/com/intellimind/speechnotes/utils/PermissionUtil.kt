package com.intellimind.speechnotes.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.intellimind.speechnotes.app.SpeechApplication
import com.intellimind.speechnotes.common.PermissionListener

object PermissionUtil {

    const val MY_PERMISSIONS_RECORD_AUDIO = 1
    const val PREFS_FILE_NAME = "is_first"


    /**
     * Method for checking microphone permission has granted
     */
    fun checkIsAudioPermissionEnabled(activity: Activity): Boolean {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    /**
     * Method for requesting access to microphone
     */
    fun requestRecordAudioAccessPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            MY_PERMISSIONS_RECORD_AUDIO)
    }
    fun shouldAskPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    private fun shouldAskPermission(context: Context, permission: String): Boolean {
        if (shouldAskPermission()) {
            val permissionResult = ActivityCompat.checkSelfPermission(context, permission)
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }
    private fun firstTimeAskingPermission(permission: String, isFirstTime: Boolean) {
        val sharedPreference = SpeechApplication.getContext().getSharedPreferences(PREFS_FILE_NAME,
            Context.MODE_PRIVATE
        )
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }


    private fun isFirstTimeAskingPermission(permission: String): Boolean {
        return SpeechApplication.getContext().getSharedPreferences(PREFS_FILE_NAME,
            Context.MODE_PRIVATE
        ).getBoolean(permission, true);
    }

    fun checkPermission(activity: Activity, permission: String, listener: PermissionListener) {
        /*
        * If permission is not granted
        * */
        if (shouldAskPermission(SpeechApplication.getContext(), permission)) {
            /*
            * If permission denied previously
            * */
            if (activity.shouldShowRequestPermissionRationale(permission)) {
                listener.onPermissionPreviouslyDenied()
            } else {
                /*
                * Permission denied or first time requested
                * */
                if (isFirstTimeAskingPermission(permission)) {
                    firstTimeAskingPermission(permission, false)
                    listener.onPermissionAsk()
                } else {
                    /*
                    * Handle the feature without permission or ask user to manually allow permission
                    * */
                    listener.onPermissionDisabled()
                }
            }
        } else {
            listener.onPermissionGranted()
        }
    }
}
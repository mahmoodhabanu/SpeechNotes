package com.intellimind.speechnotes.common

interface PermissionListener {

    /*
    * Callback to ask permission
    * */
    fun onPermissionAsk()

    /*
    * Callback on permission denied
    * */
    fun onPermissionPreviouslyDenied()

    /*
    * Callback on permission "Never show again" checked and denied
    * */
    fun onPermissionDisabled()

    /*
    * Callback on permission granted
    * */
    fun onPermissionGranted()

}
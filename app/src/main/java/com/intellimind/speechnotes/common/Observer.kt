package com.intellimind.speechnotes.common

interface Observer{
    fun onObserve(requestCode: Int, requestMessage: String?)
}
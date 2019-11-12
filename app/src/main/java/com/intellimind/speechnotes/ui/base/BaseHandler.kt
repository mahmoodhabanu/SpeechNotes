package com.intellimind.speechnotes.ui.base

import android.view.View

interface BaseHandler<T> {

    /**
     * This method handles onClick from xml
     */
    fun onClick(view: View, data: T?)
}
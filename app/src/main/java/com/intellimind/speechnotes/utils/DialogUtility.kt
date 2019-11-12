package com.intellimind.speechnotes.utils

import android.content.Context
import com.intellimind.speechnotes.R
import com.intellimind.speechnotes.common.Observer

object DialogUtility {
    const val ALLOW_SETTINGS = 1

    fun showDialogue(context: Context, message: String, observer: Observer) {

        val builder = android.app.AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.allow)) { dialogInterface, i ->
                //
                handleButtonClick(observer, ALLOW_SETTINGS)
            }.setNegativeButton(android.R.string.cancel) { dialogInterface, i ->     dialogInterface.cancel(); }

        builder.show()

    }

    private fun handleButtonClick(lis:Observer, id: Int) {
        lis.onObserve(id, "")
    }


}
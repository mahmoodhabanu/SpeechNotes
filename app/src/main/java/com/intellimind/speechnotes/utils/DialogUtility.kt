package com.intellimind.speechnotes.utils

import android.content.Context
import com.intellimind.speechnotes.R

object DialogUtility {

    fun showDialogue(context: Context, message: String) {

        val builder = android.app.AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.ok)) { dialogInterface, i ->
                dialogInterface.dismiss()
            }

        builder.show()
    }
}
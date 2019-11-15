package com.intellimind.speechnotes.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object RxObservable {

    /**
     * This method listens the edit text changes and publish the subject when editText changes
     */
    fun editTextListener(editText: EditText): Observable<Boolean> {

        val subject = PublishSubject.create<Boolean>()
        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                /*EditText after text changed*/
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                /*EditText before text changed*/
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                subject.onNext(true)
            }
        })
        return subject
    }
}
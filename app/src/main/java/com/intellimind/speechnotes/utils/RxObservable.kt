package com.intellimind.speechnotes.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Shabana A on 16/8/18.
 */

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

    fun recyclerViewListener(recyclerView: RecyclerView): Observable<Boolean> {
        val subject = PublishSubject.create<Boolean>()
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                subject.onNext(true)
                return v?.onTouchEvent(event) ?: true
            }
        })
        return subject
    }


    fun editTextActionNextListener(editText: EditText): Observable<Boolean> {
        val subject = PublishSubject.create<Boolean>()
        editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                subject.onNext(true)

                return@OnEditorActionListener true
            }
            false
        })
        return subject
    }


    fun editTextActionDoneListener(editText: EditText): Observable<Boolean> {
        val subject = PublishSubject.create<Boolean>()
        editText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                subject.onNext(true)

                return@OnEditorActionListener true
            }
            false
        })
        return subject
    }


}
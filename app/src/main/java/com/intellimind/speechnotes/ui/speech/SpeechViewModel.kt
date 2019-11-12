package com.intellimind.speechnotes.ui.speech

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import com.intellimind.speechnotes.remote.AppDataBase
import com.intellimind.speechnotes.remote.Speech
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SpeechViewModel: ViewModel() {

    var suggestions: MutableLiveData<List<Speech>> = MutableLiveData()
    private val disposable = CompositeDisposable()

    fun getSuggestions(speechText: String?) {
        var suggestion: Observable<List<Speech>> = Observable.just(speechText).flatMap { results ->
            AppDataBase.getAppDatabase()?.speechDao()?.getSuggestions(speechText)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }

    private fun setCuggestionData(data: List<Speech>){

    }

}
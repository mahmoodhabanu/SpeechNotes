package com.intellimind.speechnotes.ui.speech

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import com.intellimind.speechnotes.remote.AppDataBase
import com.intellimind.speechnotes.remote.Speech
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers

class SpeechViewModel: ViewModel() {

    var suggestions: MutableLiveData<Observable<List<Speech>>> = MutableLiveData()

    fun getSuggestions(speechText: String?) {
        suggestions.postValue(Observable.just(speechText).flatMap { results ->
            AppDataBase.getAppDatabase()?.speechDao()?.getSuggestions("%$results%")
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()))

    }

    fun addSpeechText(speechText: String?){
        Completable.fromAction(Action {
            AppDataBase.getAppDatabase()?.speechDao()?.addSpeechText(Speech(speechText = speechText))
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                val test = "Coompleted"
                test.length
            }

    }

  
}
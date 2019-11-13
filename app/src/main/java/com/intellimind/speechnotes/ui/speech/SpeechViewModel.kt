package com.intellimind.speechnotes.ui.speech

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import com.intellimind.speechnotes.remote.AppDataBase
import com.intellimind.speechnotes.remote.Speech
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.Flowable




class SpeechViewModel: ViewModel() {

    var suggestions: MutableLiveData<List<Speech>> = MutableLiveData()

    fun getSuggestions(speechText: String?) {
        //suggestions.postValue(
//            Observable.just(speechText).flatMap { results ->
//            AppDataBase.getAppDatabase()?.speechDao()?.getSuggestions()
//        }.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread()).subscribe { users ->
//                    suggestions.value = users
//                }

        AppDataBase.getAppDatabase()?.speechDao()?.getSuggestions("%$speechText%")?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Consumer<List<Speech>> {
                override fun accept(employees: List<Speech>) {
                    Log.e("suggestion ", ""+employees.size)
                    suggestions.value = employees
                }
            })


    }

    fun addSpeechText(speechText: String?){
        Completable.fromAction({
            AppDataBase.getAppDatabase()?.speechDao()?.addSpeechText(Speech(speechText = speechText))
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}

                override fun onComplete() {
                    Log.e("onComplete "," add")
                }

                override fun onError(e: Throwable) {
                    Log.e("onError "," add" + e.message)
                }
            })

    }

  
}
package com.intellimind.speechnotes.ui.speech

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intellimind.speechnotes.remote.AppDataBase
import com.intellimind.speechnotes.remote.Speech
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.preference.PreferenceManager
import com.intellimind.speechnotes.app.SpeechApplication


class SpeechViewModel: ViewModel() {

    var suggestions: MutableLiveData<List<Speech>> = MutableLiveData()

    fun getSuggestions(speechText: String?) {
        AppDataBase.getAppDatabase()?.speechDao()?.getSuggestions("%$speechText%")?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Consumer<List<Speech>> {
                override fun accept(speechList: List<Speech>) {
                    Log.e("suggestion ", ""+speechList.size)
                    getSuggestionList(speechList,speechText!!)
                }
            })
    }

    fun addSpeechText(speechText: String?){
        Completable.fromAction {
            AppDataBase.getAppDatabase()?.speechDao()?.addSpeechText(Speech(speechText = speechText))
        }.subscribeOn(Schedulers.io())
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

    fun getSuggestionList( speechList:List<Speech>, speechText:String){
        var newList:ArrayList<Speech> = ArrayList()
        for(speech in speechList) {
            val string = speech.speechText
            if(speech.speechText?.length!! >= speechText.length) {
                newList.add(speech)

            }
        }
        suggestions.value = newList


    }

    fun addDefaultSuggestions(list: List<Speech>?){
        Completable.fromAction {
            AppDataBase.getAppDatabase()?.speechDao()?.addSuggestions(list)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}

                override fun onComplete() {
                    Log.e("onComplete "," add List")
                }

                override fun onError(e: Throwable) {
                    Log.e("onError "," add List" + e.message)
                }
            })
    }


    fun ckeckFirstLaunch(): Boolean {
        var isFirstTimeLaunch: Boolean

        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(SpeechApplication.getContext())
        preferences.run {
            isFirstTimeLaunch = if (preferences.contains(IS_LAUNCH_FIRST_TIME)) {
                false
            } else {
                preferences.edit().putBoolean(IS_LAUNCH_FIRST_TIME, true).apply()
                true
            }
        }

        return isFirstTimeLaunch

    }
    companion object {
        const val IS_LAUNCH_FIRST_TIME = "isFirstTime"
    }
}
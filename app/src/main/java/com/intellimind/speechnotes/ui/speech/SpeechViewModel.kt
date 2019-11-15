package com.intellimind.speechnotes.ui.speech

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.intellimind.speechnotes.remote.AppDataBase
import com.intellimind.speechnotes.remote.Speech
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import android.preference.PreferenceManager
import com.intellimind.speechnotes.app.SpeechApplication


class SpeechViewModel: ViewModel() {

    var suggestions: MutableLiveData<List<Speech>> = MutableLiveData()

    /**
     * This method to fetch suggestions for a given text from db
     */
    fun getSuggestions(speechText: String?) {
        AppDataBase.getAppDatabase()?.speechDao()?.getSuggestions("%$speechText%")?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Consumer<List<Speech>> {
                override fun accept(speechList: List<Speech>) {
                    Log.e("suggestion ", ""+speechList.size)
                    getSuggestionList(speechList,speechText!!)
                }
            })
    }

    /**
     * This method adds the new words spoken by user to db
     */
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
    /**
     * This method to filter the results by length
     */
    fun getSuggestionList( speechList:List<Speech>, speechText:String){
        var newList:ArrayList<Speech> = ArrayList()
        for(speech in speechList) {
            if(speech.speechText?.length!! >= speechText.length) {
                newList.add(speech)

            }
        }
        suggestions.value = newList


    }

    /**
     * This method adds the default suggestions to db
     */
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

    /**
     * This method checks it is initial launch or not
     */
    fun checkFirstLaunch(): Boolean {
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
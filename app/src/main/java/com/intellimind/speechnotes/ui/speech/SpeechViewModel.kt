package com.intellimind.speechnotes.ui.speech

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
//            AppDataBase.getAppDatabase()?.speechDao()?.getSuggestions("%$speechText%")?.subscribeOn(Schedulers.io())?.flatMapIterable {
//                    results -> results;
//            }?.filter(object : Predicate<Speech>{
//                override fun test(t: Speech): Boolean {
//                    return speechText?.length!! <= t.speechText?.length!!
//                }
//            })?.toList()?.observeOn(AndroidSchedulers.mainThread())
//                ?.subscribe(object : Consumer<List<Speech>> {
//                override fun accept(employees: List<Speech>) {
//                    Log.e("suggestion ", ""+employees.size)
//                    suggestions.value = employees
//                }
//            });

//        AppDataBase.getAppDatabase()?.speechDao()?.getSuggestions("%$speechText%")?.observeOn(AndroidSchedulers.mainThread())?.toObservable()?.
//            subscribeOn(Schedulers.io())?.flatMapIterable( {
//                override fun apply(results: List<Speech> ) {
//                    return results;
//                }
//            })
//            .filter(new Predicate<MyClassA>() {
//                @Override public boolean test(MyClassA v) {
//                    return v.value2 == 10;
//                }
//            })
//            .toList()
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe();
//            flatMap { results -> Observable.fromIterable(results) }?.map { result -> {
//            if(speechText?.length!! <= result.speechText?.length!!) {
//                result.isShow = true;
//
//            }
//        }
//
//        }?.toList()?.subscribe { response ->
//            suggestions.value = response as List<Speech>
//         }



        AppDataBase.getAppDatabase()?.speechDao()?.getSuggestions("%$speechText%")?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Consumer<List<Speech>> {
                override fun accept(speechList: List<Speech>) {
                    Log.e("suggestion ", ""+speechList.size)
                    getSuggestionList(speechList,speechText!!)
                }
            });


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

  
}
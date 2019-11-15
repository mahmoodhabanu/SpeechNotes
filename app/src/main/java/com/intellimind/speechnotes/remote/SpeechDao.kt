package com.intellimind.speechnotes.remote

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
interface SpeechDao {
    @Query("SELECT * FROM speechInfo WHERE speechtext LIKE :speechText" )
    fun getSuggestions(speechText: String?) : Flowable<List<Speech>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSpeechText(speech: Speech)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSuggestions(suggestionList: List<Speech>?)
}
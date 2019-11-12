package com.intellimind.speechnotes.remote

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Observable

@Dao
interface SpeechDao {
    @Query("SELECT * FROM speechInfo WHERE speechText LIKE '%:speechText%'")
    fun getSuggestions(speechText: String?) : Observable<List<Speech>>
}
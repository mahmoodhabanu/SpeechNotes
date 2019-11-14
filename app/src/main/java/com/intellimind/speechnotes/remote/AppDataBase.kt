package com.intellimind.speechnotes.remote

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.intellimind.speechnotes.app.SpeechApplication

@Database(entities = arrayOf(Speech::class), version = 3)
abstract class AppDataBase: RoomDatabase() {

    companion object {
        private var INSTANCE: AppDataBase? = null
        fun getAppDatabase(): AppDataBase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    SpeechApplication.getContext(),
                    AppDataBase::class.java,
                    "speech"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE
        }
    }

    abstract fun speechDao(): SpeechDao
}
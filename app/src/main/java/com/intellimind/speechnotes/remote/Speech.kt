package com.intellimind.speechnotes.remote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "speechInfo")
class Speech {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private var mId: Int = 0

    private var speechText: String? = null
}
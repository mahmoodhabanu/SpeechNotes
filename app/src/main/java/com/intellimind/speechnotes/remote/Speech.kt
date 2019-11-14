package com.intellimind.speechnotes.remote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.intellimind.speechnotes.ui.base.BaseModel

@Entity(tableName = "speechInfo")
data class Speech (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var mId: Int = 0,
    @ColumnInfo(name = "speechtext")
    var speechText: String? = null

) :BaseModel() {
    var isShow = false
}
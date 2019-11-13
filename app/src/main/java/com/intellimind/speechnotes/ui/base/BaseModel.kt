package com.intellimind.speechnotes.ui.base

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.databinding.BaseObservable

open class BaseModel() : BaseObservable(), Parcelable, BaseHandler<BaseModel> {

    override fun onClick(view: View, data: BaseModel?) {
    }

    var layoutResId: Int = 0

    constructor(parcel: Parcel) : this() {
        layoutResId = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(layoutResId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BaseModel> {
        override fun createFromParcel(parcel: Parcel): BaseModel {
            return BaseModel(parcel)
        }

        override fun newArray(size: Int): Array<BaseModel?> {
            return arrayOfNulls(size)
        }
    }
}
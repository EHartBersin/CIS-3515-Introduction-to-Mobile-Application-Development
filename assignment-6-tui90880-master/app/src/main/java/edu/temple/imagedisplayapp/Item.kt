package edu.temple.imagedisplayapp

import android.os.Parcel
import android.os.Parcelable

/**
 * The Item data class implements the _Parcelable_ interface
 * which allows it to be passed between activities.
 * This could also be accomplished
 * by implementing the _Serializable_ interface
 */
data class Item(val resourceId: Int, val description: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(resourceId)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}
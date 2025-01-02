package com.example.todoapp.entity

import android.os.Parcel
import android.os.Parcelable

data class Task(
    val title: String = "",
    val description: String = "",
    var isUrgent: Boolean = false,
    var isCompleted: Boolean = false,
    var id: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: ""
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
        dest.writeString(description)
        dest.writeByte(if (isUrgent) 1 else 0)
        dest.writeByte(if (isCompleted) 1 else 0)
        dest.writeString(id)
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
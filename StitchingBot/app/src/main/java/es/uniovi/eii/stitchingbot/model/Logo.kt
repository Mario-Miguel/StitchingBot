package es.uniovi.eii.stitchingbot.model

import android.os.Parcel
import android.os.Parcelable

data class Logo(
    val id: Int = 0,
    val imgUrl: String? = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(imgUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Logo> {
        override fun createFromParcel(parcel: Parcel): Logo {
            return Logo(parcel)
        }

        override fun newArray(size: Int): Array<Logo?> {
            return arrayOfNulls(size)
        }
    }

}
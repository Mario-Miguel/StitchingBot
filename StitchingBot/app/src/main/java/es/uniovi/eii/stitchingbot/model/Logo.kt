package es.uniovi.eii.stitchingbot.model

import android.os.Parcel
import android.os.Parcelable

data class Logo(
    var id: Int = 0,
    var imgUrl: String? = "",
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

    override fun equals(other: Any?): Boolean {
        return if (other is Logo)
            this.imgUrl == other.imgUrl
        else
            false
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (imgUrl?.hashCode() ?: 0)
        return result
    }
}
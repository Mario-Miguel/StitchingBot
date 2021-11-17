package es.uniovi.eii.stitchingbot.model

import android.os.Parcel
import android.os.Parcelable

data class SewingMachine(
    var id: Int = 0,
    var name: String? = "",
    var imgUrl: String? = "",
    var motorSteps: Int = 467
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(imgUrl)
        parcel.writeInt(motorSteps)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SewingMachine> {
        override fun createFromParcel(parcel: Parcel): SewingMachine {
            return SewingMachine(parcel)
        }

        override fun newArray(size: Int): Array<SewingMachine?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is SewingMachine)
            this.imgUrl == other.imgUrl
                    && this.motorSteps == other.motorSteps
                    && this.name == other.name
        else
            false
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (imgUrl?.hashCode() ?: 0)
        result = 31 * result + motorSteps
        return result
    }
}
package ws.grigory.currencycalculator

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency(var name: String, var rate: Float, var count: Float) : Parcelable, Cloneable {

    @IgnoredOnParcel
    var value = 0f

    constructor(name: String) : this(name, 1f, 1f)

    fun isInfinity(): Boolean =
        (value == Float.NEGATIVE_INFINITY) || (value == Float.POSITIVE_INFINITY)

    fun getBaseValue(): Float {
        return checkInfinity((value / count) * rate)
    }

    fun setValue(strValue: StringBuilder) {
        this.value = checkInfinity(Constants.DF.parse(strValue.toString())!!.toFloat())
    }

    fun calculateValue(baseValue: Float) {
        this.value = checkInfinity((baseValue / rate) * count)
    }

    fun plus(value: Float) {
        this.value = checkInfinity(value + this.value)
    }

    fun minus(value: Float) {
        this.value = checkInfinity(value - this.value)
    }

    fun mul(value: Float) {
        this.value = checkInfinity(value * this.value)
    }

    fun div(value: Float) {
        this.value =
            if (this.value == 0f) {
                if (value < 0) Float.NEGATIVE_INFINITY else Float.POSITIVE_INFINITY
            } else {
                checkInfinity(value / this.value)
            }
    }

    private fun checkInfinity(value: Float): Float =
        if (value > Constants.MAX_VALUE) Float.POSITIVE_INFINITY else
            if (value < Constants.MIN_VALUE) Float.NEGATIVE_INFINITY else value

}
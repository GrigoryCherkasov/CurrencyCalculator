package ws.grigory.currencycalculator.calculator

import ws.grigory.currencycalculator.Constants.DF
import ws.grigory.currencycalculator.Constants.EMPTY
import ws.grigory.currencycalculator.Constants.ZERO
import ws.grigory.currencycalculator.Currency
import kotlin.math.roundToLong

open class Display(var currencyIndex: Int, var currencies: ArrayList<Currency>) {
    protected open val display = StringBuilder()
    val value: String
        get() = if (currencyIndex < 0 || currencyIndex >= currencies.size) EMPTY else display.toString()
    val currencyName: String
        get() = if (currencyIndex < 0 || currencyIndex >= currencies.size) EMPTY else currencies[currencyIndex].name

    fun shift() {
        if (currencyIndex >= 0 && currencyIndex < currencies.size) {
            currencyIndex = if (currencyIndex == 0) currencies.size - 1 else --currencyIndex
            writeCurrencyValueToDisplay()
        }
    }

    fun calculateCurrencyValueToDisplay(baseValue: Float) {
        if (currencyIndex >= 0 && currencyIndex < currencies.size) {
            currencies[currencyIndex].calculateValue(baseValue)
            writeCurrencyValueToDisplay()
        }
    }

    open fun writeCurrencyValueToDisplay() {
        clearDisplay()
        display.append(
            if (currencyIndex >= currencies.size || currencies[currencyIndex].value == 0F) EMPTY
            else floatToString(currencies[currencyIndex].value)
        )
    }

    fun zeroingDisplay() {
        if (currencyIndex >= 0 && currencyIndex < currencies.size) {
            currencies[currencyIndex].value = 0F
        }
        clearDisplay()
    }

    fun clearDisplay() {
        display.apply { setLength(0) }
    }

    protected fun floatToString(value: Float): String {
        return when (value) {
            Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY -> {
                value.toString()
            }

            0F -> {
                ZERO
            }

            else -> {
                DF.format((value * 100f).roundToLong() / 100f)
            }
        }
    }
}
package ws.grigory.currencycalculator.calculator

import ws.grigory.currencycalculator.Constants.BS
import ws.grigory.currencycalculator.Constants.DS
import ws.grigory.currencycalculator.Constants.EVAL
import ws.grigory.currencycalculator.Constants.ZERO_CHAR
import ws.grigory.currencycalculator.Currency
import kotlin.collections.ArrayList

class MainDisplay(currencyIndex: Int, currencies: ArrayList<Currency>) :
    Display(currencyIndex, currencies) {
    val expression = StringBuilder()
    val mainDisplayCurrency: Currency
        get() = currencies[currencyIndex]

    fun processDisplayChar(isNewValue: Boolean, c: Char): Boolean {
        if (isNewValue) {
            clearDisplay()
        }
        var result = isNewValue
        with(display) {

            if (BS == c) {
                if (isNotEmpty()) {
                    delete(length - 1, length)
                }
                result = if (isNotEmpty())
                    false
                else {
                    clearDisplay()
                    append(ZERO_CHAR)
                    true
                }
            } else {

                val indexDS: Int = indexOf(DS)

                if (indexDS < 0 || (c != DS && length - indexDS < 3)) {
                    if (length == 1 && this[0] == ZERO_CHAR && c != DS) {
                        delete(0, 1)
                    }
                    if (isEmpty() && c == DS) {
                        append(ZERO_CHAR)
                    }
                    append(c)
                    result = false
                }
            }
        }
        return result
    }

    override fun writeCurrencyValueToDisplay() {
        clearDisplay()
        display.append(floatToString(currencies[currencyIndex].value))
    }

    fun writeDisplayValueToCurrency() {
        mainDisplayCurrency.setValue(display)

        if (mainDisplayCurrency.isInfinity()) {
            writeCurrencyValueToDisplay()
        }
    }

    fun createExpression(operation: Char, yRegistry: Float) {
        with(expression) {
            setLength(0)
            if (!mainDisplayCurrency.isInfinity() && operation != EVAL) {
                append(floatToString(yRegistry))
                append(' ')
                append(operation)
            }
        }
    }
}
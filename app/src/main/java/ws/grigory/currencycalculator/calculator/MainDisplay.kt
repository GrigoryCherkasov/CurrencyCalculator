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

    fun processDisplayChar(isNewValue: Boolean, c: Char): Boolean {
        if (isNewValue) {
            clearDisplay()
        }
        var result = isNewValue
        val displayLength: Int = display.length
        if (BS == c) {
            result = if (displayLength > 1) {
                display.delete(displayLength - 1, displayLength)
                false
            } else {
                clearDisplay()
                display.append(ZERO_CHAR)
                true
            }
        } else {

            val indexDS: Int = display.indexOf(DS)

            if (indexDS < 0 || (c != DS && displayLength - indexDS < 3)) {
                if (displayLength == 1 && display[0] == ZERO_CHAR && c != DS) {
                    display.delete(0, 1)
                }
                if(displayLength == 0 && c == DS){
                    display.append(ZERO_CHAR)
                }
                display.append(c)
                result = false
            }
        }
        return result
    }

    override fun writeCurrencyValueToDisplay() {
        clearDisplay()
        display.append(floatToString(currencies[currencyIndex].value))
    }

    fun writeDisplayValueToCurrency() {
        getMainDisplayCurrency().setValue(display)

        if(getMainDisplayCurrency().isInfinity()){
            writeCurrencyValueToDisplay()
        }
    }

    fun createExpression(operation: Char, yRegistry: Float) {
        expression.setLength(0)
        if (!getMainDisplayCurrency().isInfinity() && operation != EVAL) {
            expression.append(floatToString(yRegistry))
            expression.append(' ')
            expression.append(operation)
        }
    }

    fun getMainDisplayCurrency() : Currency {
        return currencies[currencyIndex]
    }
}
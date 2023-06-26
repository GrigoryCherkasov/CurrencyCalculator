package ws.grigory.currencycalculator.calculator

import android.content.Context
import ws.grigory.currencycalculator.Constants.C
import ws.grigory.currencycalculator.Constants.DIV
import ws.grigory.currencycalculator.Constants.EVAL
import ws.grigory.currencycalculator.Constants.MINUS
import ws.grigory.currencycalculator.Constants.MUL
import ws.grigory.currencycalculator.Constants.PLUS
import ws.grigory.currencycalculator.Constants.SHIFT
import ws.grigory.currencycalculator.Constants.ZERO_CHAR

import ws.grigory.currencycalculator.Currency
import ws.grigory.currencycalculator.WidgetParameters

class Calculator (context: Context) {
    private val operations = charArrayOf(PLUS, MINUS, MUL, DIV, EVAL).toHashSet()
    private var yRegistry: Float = 0F
    private var operation: Char = EVAL
    private var isNewValue: Boolean = true
    private lateinit var displays: Array<Display>
    private lateinit var mainDisplay: MainDisplay
    private lateinit var currencies: ArrayList<Currency>
    var invalidated = true

    init{
        loadCurrencies(context)
        mainDisplay.processDisplayChar(true, ZERO_CHAR)
        invalidated = true
    }

    fun setData(data: Char , context: Context) {
        invalidated = false
        val currency: Currency  = mainDisplay.getMainDisplayCurrency()
        if (C == data) {
            clear()
        } else {
            if (!mainDisplay.getMainDisplayCurrency().isInfinity()) {
                if (SHIFT == data) {
                    shift(context)
                } else {
                    if (!operations.contains(data)) {
                        isNewValue = mainDisplay.processDisplayChar(isNewValue, data)
                        mainDisplay.writeDisplayValueToCurrency()
                    } else {
                        if (!isNewValue) {
                            when (operation) {
                                PLUS -> currency.plus(yRegistry)
                                MINUS -> currency.minus(yRegistry)
                                MUL -> currency.mul(yRegistry)
                                DIV -> currency.div(yRegistry)
                            }
                            mainDisplay.writeCurrencyValueToDisplay()
                        }

                        if (currency.isInfinity() || data == EVAL) {
                            clearRegistries()
                        } else {
                            operation = data
                            yRegistry = currency.value
                        }
                        isNewValue = true
                    }
                    mainDisplay.createExpression(operation, yRegistry)
                    calculateCurrencies(currency.value)
                }
            }
        }
    }

    private fun calculateCurrencies(value: Float) {
        mainDisplay.getMainDisplayCurrency().value = value
        val baseValue: Float = mainDisplay.getMainDisplayCurrency().getBaseValue()
        displays.filter { display: Display -> display !is  MainDisplay}.
            forEach { display ->  display.calculateCurrencyValueToDisplay(baseValue)}
    }

    private fun shift(context: Context) {
        displays.forEach {display -> display.shift() }
        clearRegistries()
        WidgetParameters.getWidgetParameters(context).saveParameters(
            context, currencies, displays[0].currencyIndex)
    }

    fun reset(context: Context){
        loadCurrencies(context)
        displays.forEachIndexed { index, display ->
            run {
                display.currencyIndex = index
                display.currencies = currencies
            }
        }
        clear()
    }

    private fun clear() {
        clearRegistries()
        displays.forEach { display ->  display.zeroingDisplay()}

        mainDisplay.processDisplayChar(true, ZERO_CHAR)
        mainDisplay.createExpression(operation, yRegistry)
    }

    private fun clearRegistries() {
        yRegistry = 0F
        operation = EVAL
        isNewValue = true
        invalidated = true
    }

    fun getDisplays(): Array<Display> {
        return displays
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadCurrencies(context: Context) {
        val widgetParameters: WidgetParameters = WidgetParameters.getWidgetParameters(context)

        currencies = widgetParameters.currencies.clone() as ArrayList<Currency>

        var currencyIndex: Int = widgetParameters.mainDisplayCurrencyIndex

        mainDisplay = MainDisplay(currencyIndex, currencies)
        currencyIndex = if (currencyIndex == 0) currencies.size - 1 else --currencyIndex
        val display1 = Display(currencyIndex, currencies)
        currencyIndex = if (currencyIndex == 0) currencies.size - 1 else --currencyIndex
        val display2 = Display(currencyIndex, currencies)

        displays = arrayOf(mainDisplay, display1, display2)
    }

    fun invalidate(){
        clearRegistries()
        mainDisplay.createExpression(EVAL, 0F)
    }
}
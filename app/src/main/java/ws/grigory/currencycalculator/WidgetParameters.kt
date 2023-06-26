package ws.grigory.currencycalculator

import android.content.Context
import android.content.SharedPreferences
import ws.grigory.currencycalculator.Constants.EMPTY
import java.text.DecimalFormat

private const val CCWIDGET_KEY = "CCWK"
private const val MAIN_CURRENCY_NAME = "MCN"
private const val CURRENCY1_NAME = "C1N"
private const val CURRENCY1_RATE = "C1R"
private const val CURRENCY1_COUNT = "C1C"
private const val CURRENCY2_NAME = "C2N"
private const val CURRENCY2_RATE = "C2R"
private const val CURRENCY2_COUNT = "C2C"
private const val MAIN_DISPLAY_CURRENCY_INDEX = "MDCI"

object Constants {
    const val CURRENCY_LIST_START = "\"symbols\":"
    const val CURRENCY_RATES_START = "\"rates\":"
    const val URL_CURRENCY_LIST = "https://api.exchangerate.host/symbols"
    const val URL_CURRENCY_RATE = "https://api.exchangerate.host/latest?base=%s"
    const val RU_LANGUAGE = "ru"

    const val BUTTON_CODE = "BUTTON_CODE"
    const val CLASS_CODE = "CLASS_CODE"
    const val INVALIDATE = "INVALIDATE"
    const val ADD = "ADD"
    const val EDIT = "EDIT"
    const val EMPTY = ""
    const val ZERO = "0"
    const val ONE = "1"

    const val ZERO_CHAR = '0'
    const val PLUS = '+'
    const val MINUS = '\u2013'
    const val MUL = '\u00D7'
    const val DIV = '\u00F7'
    const val EVAL = '='
    const val C = 'C'
    const val BS = '\u232B'
    const val SHIFT = '\u21F5'
    const val CURRENCIES = "CURRENCIES"
    const val DIGITS = "0123456789,."
    const val MIN_VALUE = -21474836
    const val MAX_VALUE = 21474836

    private const val DOT = '.'
    private const val COMMA = ','

    val DF: DecimalFormat = DecimalFormat("#.##")
    val DS = DF.decimalFormatSymbols.decimalSeparator

    fun truncate(text: CharSequence): CharSequence {
        return if (text.length < 3) text else text.subSequence(0, 3)
    }

    fun charSequenceToFloat(text: CharSequence): Float {
        return try {
            text.toString().replace(COMMA, DOT).toFloat()
        } catch (ignored: Exception) {
            Float.NaN
        }
    }

    fun floatToCharSequence(number: Float): CharSequence =
            if (number == 0F) ZERO else DF.format(number)
}

class WidgetParameters {

    lateinit var currencies: ArrayList<Currency>

    var mainDisplayCurrencyIndex = 0

    companion object {
        private var widgetParameters: WidgetParameters? = null
        @Synchronized
        fun getWidgetParameters(context: Context): WidgetParameters {
            if (widgetParameters == null) {
                widgetParameters = WidgetParameters()
            }
            widgetParameters!!.loadParameters(context)
            return widgetParameters as WidgetParameters
        }
    }

    fun loadParameters(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            CCWIDGET_KEY,
            Context.MODE_PRIVATE
        )
        var currencyName: String = sharedPreferences.getString(MAIN_CURRENCY_NAME, EMPTY) as String
        if (EMPTY != currencyName) {
            currencies = ArrayList(3)
            currencies.add(Currency(currencyName))
            currencyName = sharedPreferences.getString(CURRENCY1_NAME, EMPTY) as String
            var currencyRate: Float = sharedPreferences.getFloat(CURRENCY1_RATE, 0F)
            var currencyCount: Float = sharedPreferences.getFloat(CURRENCY1_COUNT, 0F)
            if (EMPTY != currencyName && currencyRate != 0f && currencyCount != 0F) {
                currencies.add(Currency(currencyName, currencyRate, currencyCount))
                currencyName = sharedPreferences.getString(CURRENCY2_NAME, EMPTY) as String
                currencyRate = sharedPreferences.getFloat(CURRENCY2_RATE, 0F)
                currencyCount = sharedPreferences.getFloat(CURRENCY2_COUNT, 0F)
                if (EMPTY != currencyName && currencyRate != 0f && currencyCount != 0F) {
                    currencies.add(Currency(currencyName, currencyRate, currencyCount))
                }
            }
            mainDisplayCurrencyIndex = sharedPreferences.getInt(MAIN_DISPLAY_CURRENCY_INDEX, 0)
        } else {
            currencies = arrayListOf(Currency(EMPTY))
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun saveParameters(context: Context, data: ArrayList<Currency>, mainDisplayCurrencyIndex: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(CCWIDGET_KEY,
            Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        currencies = data.clone() as ArrayList<Currency>

        if (currencies.size > 0 && EMPTY != currencies[0].name) {
            editor.putString(MAIN_CURRENCY_NAME, currencies[0].name)

            if (currencies.size > 1) {
                editor.putString(CURRENCY1_NAME, currencies[1].name)
                editor.putFloat(CURRENCY1_RATE, currencies[1].rate)
                editor.putFloat(CURRENCY1_COUNT, currencies[1].count)
                if (currencies.size > 2) {
                    editor.putString(CURRENCY2_NAME, currencies[2].name)
                    editor.putFloat(CURRENCY2_RATE, currencies[2].rate)
                    editor.putFloat(CURRENCY2_COUNT, currencies[2].count)
                } else {
                    removeCurrency2(editor)
                }

            } else {
                removeCurrency1(editor)
                removeCurrency2(editor)
            }
            this.mainDisplayCurrencyIndex = mainDisplayCurrencyIndex
            editor.putInt(MAIN_DISPLAY_CURRENCY_INDEX, this.mainDisplayCurrencyIndex)
        } else {
            editor.remove(MAIN_CURRENCY_NAME)
            removeCurrency1(editor)
            removeCurrency2(editor)
        }
        editor.apply()
    }

    private fun removeCurrency1(editor: SharedPreferences.Editor) {
        editor.remove(CURRENCY1_NAME)
        editor.remove(CURRENCY1_RATE)
        editor.remove(CURRENCY1_COUNT)
    }

    private fun removeCurrency2(editor: SharedPreferences.Editor) {
        editor.remove(CURRENCY2_NAME)
        editor.remove(CURRENCY2_RATE)
        editor.remove(CURRENCY2_COUNT)
    }

}
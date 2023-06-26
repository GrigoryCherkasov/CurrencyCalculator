package ws.grigory.currencycalculator

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import ws.grigory.currencycalculator.Constants.CURRENCY_LIST_START
import ws.grigory.currencycalculator.Constants.CURRENCY_RATES_START
import ws.grigory.currencycalculator.Constants.RU_LANGUAGE
import ws.grigory.currencycalculator.Constants.URL_CURRENCY_LIST
import ws.grigory.currencycalculator.Constants.URL_CURRENCY_RATE
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class RateLoader {

    class CurrencyData {
        lateinit var description: String
        lateinit var code: String
    }

    fun getCurrenciesList(context: Context): MutableList<CurrencyData> {
        var currencyList: MutableList<CurrencyData> = ArrayList()
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(URL_CURRENCY_LIST).build()
        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body
                if (responseBody != null) {
                    var data = responseBody.string()
                    val startPos: Int = data.indexOf(CURRENCY_LIST_START)
                    data = data.substring(
                        startPos + CURRENCY_LIST_START.length,
                        data.length - 1
                    )
                    val currencyData =
                        Gson().fromJson<Map<String, CurrencyData>>(
                            data,
                            object :
                                TypeToken<Map<String?, CurrencyData?>?>() {}.type
                        )
                    currencyList = ArrayList(currencyData.values)
                    if (RU_LANGUAGE == Locale.getDefault().language) {
                        val codes: Array<String> =
                            context.resources.getStringArray(R.array.currency_code)
                        val names: Array<String> =
                            context.resources.getStringArray(R.array.currency_code_value)
                        for (i in codes.indices) {
                            val currency = currencyData[codes[i]]
                            if (currency != null) {
                                currency.description = names[i]
                            }
                        }
                    }
                }
            }
        } catch (ignore: Exception) {
        }
        return currencyList
    }

    fun getCurrencyRate(baseCode: String?): MutableMap<String, Float> {
        var rateList: MutableMap<String, Float> = HashMap()
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(
            String.format(URL_CURRENCY_RATE, baseCode)
        ).build()
        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body
                if (responseBody != null) {
                    var data = responseBody.string()
                    val startPos: Int = data.indexOf(CURRENCY_RATES_START)
                    data = data.substring(
                        startPos + CURRENCY_RATES_START.length,
                        data.length - 1
                    )
                    rateList = Gson().fromJson(
                        data,
                        object : TypeToken<Map<String, Float>>() {}.type
                    )
                }
            }
        } catch (ignore: Exception) {
        }
        return rateList
    }
}
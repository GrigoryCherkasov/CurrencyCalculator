package ws.grigory.currencycalculator

import android.content.Context
import android.content.res.XmlResourceParser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser.END_DOCUMENT
import org.xmlpull.v1.XmlPullParser.START_TAG
import ws.grigory.currencycalculator.Constants.CURRENCY_LIST_START
import ws.grigory.currencycalculator.Constants.CURRENCY_RATES_START
import ws.grigory.currencycalculator.Constants.RU_LANGUAGE
import ws.grigory.currencycalculator.Constants.URL_CURRENCY_LIST
import ws.grigory.currencycalculator.Constants.URL_CURRENCY_RATE
import java.util.Locale

private const val USD = "USD"
private const val ENTRY = "entry"
private const val KEY = "key"

class RateLoader {
    class CurrencyData(var code: String, var description: String)

    fun getCurrenciesList(context: Context): List<CurrencyData> {
        val currencyList = mutableListOf<CurrencyData>()
        runCatching {
            OkHttpClient().newCall(Request.Builder().url(URL_CURRENCY_LIST).build()).execute()
                .body?.let { responseBody ->
                    var data = responseBody.string()
                    data = data.substring(
                        data.indexOf(CURRENCY_LIST_START) + CURRENCY_LIST_START.length,
                        data.length - 1
                    )

                    val russianNames =
                        if (RU_LANGUAGE == Locale.getDefault().language) getHashMapResource(
                            context
                        ) else mapOf()

                    currencyList.addAll(
                        Gson().fromJson<Map<String, String>>(
                            data,
                            object :
                                TypeToken<Map<String?, String?>>() {}.type
                        )
                            .map { currency ->
                                CurrencyData(
                                    currency.key,
                                    russianNames[currency.key] ?: currency.value
                                )
                            }
                    )
                }
        }
        return currencyList
    }

    fun getCurrencyRate(baseCode: String?): MutableMap<String, Float> {
        var resultMap = mutableMapOf<String, Float>()
        runCatching {
            OkHttpClient().newCall(Request.Builder().url(URL_CURRENCY_RATE).build())
                .execute()
                .body?.let { responseBody ->
                    var data = responseBody.string()
                    data = data.substring(
                        data.indexOf(CURRENCY_RATES_START) + CURRENCY_RATES_START.length,
                        data.length - 1
                    )

                    resultMap.putAll(Gson().fromJson<Map<String, Float>?>(
                        data,
                        object : TypeToken<Map<String, Float>>() {}.type
                    )
                        .mapKeys { (key, _) -> key.substring(3) })
                    if(resultMap.containsKey(baseCode)) {
                        val rateUSD: Float = if (USD == baseCode) 1f else resultMap[baseCode]!!
                        resultMap.remove(baseCode)
                        resultMap = resultMap
                            .mapValues { (_, value) -> value / rateUSD }
                            .toMutableMap()

                        if (USD != baseCode) {
                            resultMap[USD] = 1 / rateUSD
                        }
                    } else {
                        resultMap.clear()
                    }
                }
        }
        return resultMap
    }

    private fun getHashMapResource(c: Context): Map<String, String> {
        return runCatching {
            c.resources.getXml(R.xml.currency_map).parseXml { parser ->
                val key = parser.getAttributeValue(null, KEY).orEmpty()
                val value = parser.nextText().orEmpty()
                key to value
            }
        }.getOrDefault(mapOf())
    }

    private fun XmlResourceParser.parseXml(
        action: (XmlResourceParser) -> Pair<String, String>
    ): Map<String, String> {
        val map = mutableMapOf<String, String>()
        while (next() != END_DOCUMENT) {
            if (eventType == START_TAG && name == ENTRY) {
                val (key, value) = action(this)
                map[key] = value
            }
        }
        return map
    }
}
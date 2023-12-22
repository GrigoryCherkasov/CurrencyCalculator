package ws.grigory.currencycalculator

import android.content.Context
import android.content.res.XmlResourceParser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser.END_DOCUMENT
import org.xmlpull.v1.XmlPullParser.START_TAG
import ws.grigory.currencycalculator.Constants.RU_LANGUAGE
import java.util.Locale

private const val USD = "USD"
private const val ENTRY = "entry"
private const val CODE = "code"
private const val CURRENCY_RATES_START = "\"rates\":"
private const val URL_CURRENCY_RATE = "https://open.er-api.com/v6/latest/USD"

class RateLoader {
    class CurrencyData(var code: String, var description: String)

    fun getCurrenciesList(context: Context): List<CurrencyData> {
        return getHashMapResource(
            context,
            if (RU_LANGUAGE == Locale.getDefault().language)
                R.xml.currency_list_ru
            else
                R.xml.currency_list_en
        )
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

                    resultMap.putAll(
                        Gson().fromJson<Map<String, Float>?>(
                            data,
                            object : TypeToken<Map<String, Float>>() {}.type
                        )
                    )

                    if (resultMap.containsKey(baseCode)) {
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

    private fun getHashMapResource(c: Context, currencyListXML: Int): List<CurrencyData> {
        return runCatching {
            c.resources.getXml(currencyListXML).parseXml { parser ->
                val code = parser.getAttributeValue(null, CODE).orEmpty()
                val description = parser.nextText().orEmpty()
                code to description
            }
        }.getOrDefault(listOf())
    }

    private fun XmlResourceParser.parseXml(
        action: (XmlResourceParser) -> Pair<String, String>
    ): List<CurrencyData> {
        val currencyDataList = mutableListOf<CurrencyData>()
        while (next() != END_DOCUMENT) {
            if (eventType == START_TAG && name == ENTRY) {
                val (code, description) = action(this)
                currencyDataList.add(CurrencyData(code, description))
            }
        }
        return currencyDataList
    }
}
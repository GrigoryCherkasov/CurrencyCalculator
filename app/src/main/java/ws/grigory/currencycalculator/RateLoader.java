package ws.grigory.currencycalculator;

import android.content.Context;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.util.*;


public class RateLoader {

    private static final String CURRENCY_LIST_START = "\"symbols\":";
    private static final String CURRENCY_RATES_START = "\"rates\":";
    private static final String URL_CURRENCY_LIST = "https://api.exchangerate.host/symbols";
    private static final String URL_CURRENCY_RATE = "https://api.exchangerate.host/latest?base=%s";

    private static final String RU_LANGUAGE = "ru";

    public static class CurrencyData {
        public String description;
        public String code;
    }


    public List<CurrencyData> getCurrenciesList(Context context) {

        List<CurrencyData> currencyList = null;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(URL_CURRENCY_LIST).build();
        try {
            try (Response response = client.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String data = responseBody.string();
                    int startPos = data.indexOf(CURRENCY_LIST_START);
                    data = data.substring(startPos + CURRENCY_LIST_START.length(),
                            data.length() - 1);
                    Map<String, CurrencyData> currencyData = new Gson().fromJson(data,
                            new TypeToken<Map<String, CurrencyData>>() {}.getType());
                    currencyList = new ArrayList<>(currencyData.values());

                    if(RU_LANGUAGE.equals(Locale.getDefault().getLanguage())){
                        String[] codes = context.getResources().getStringArray(R.array.currency_code);
                        String[] names = context.getResources().getStringArray(R.array.currency_code_value);
                        for(int i = 0; i < codes.length; i++){
                            CurrencyData currency = currencyData.get(codes[i]);
                            if(currency != null){
                                currency.description = names[i];
                            }
                        }
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return currencyList;
    }

    public Map<String, Float> getCurrencyRate(String baseCode){

        Map<String, Float> rateList = null;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(String.format(URL_CURRENCY_RATE, baseCode)).
                build();
        try {
            try (Response response = client.newCall(request).execute()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String data = responseBody.string();
                    int startPos = data.indexOf(CURRENCY_RATES_START);
                    data = data.substring(startPos + CURRENCY_RATES_START.length(),
                            data.length() - 1);
                    rateList = new Gson().fromJson(data,
                            new TypeToken<Map<String, Float>>() {}.getType());
                }
            }
        } catch (Exception ignore) {}
        return rateList;
    }
}
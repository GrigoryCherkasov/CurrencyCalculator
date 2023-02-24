package ws.grigory.currencycalculator;

import static ws.grigory.currencycalculator.CCWidgetLarge.COMMA;
import static ws.grigory.currencycalculator.CCWidgetLarge.DF;
import static ws.grigory.currencycalculator.CCWidgetLarge.DOT;
import static ws.grigory.currencycalculator.CCWidgetLarge.DS;
import static ws.grigory.currencycalculator.calculator.Display.EMPTY;
import static ws.grigory.currencycalculator.calculator.Display.ZERO;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Objects;

public class WidgetParameters {
    public static final int MIN_VALUE = -21474836;
    public static final int MAX_VALUE = 21474836;
    public static final String CCWIDGET_KEY = "CCWK";
    private final String MAIN_CURRENCY_NAME = "MCN";
    private final String CURRENCY1_NAME = "C1N";
    private final String CURRENCY1_RATE = "C1R";
    private final String CURRENCY1_COUNT = "C1C";
    private final String CURRENCY2_NAME = "C2N";
    private final String CURRENCY2_RATE = "C2R";
    private final String CURRENCY2_COUNT = "C2C";
    private final String MAIN_DISPLAY_CURRENCY_INDEX = "MDCI";
    private static WidgetParameters widgetParameters;

    public ArrayList<Currency> currencies = null;

    public int mainDisplayCurrencyIndex = 0;

    public static synchronized WidgetParameters getWidgetParameters(Context context) {
        if (widgetParameters == null) {
            widgetParameters = new WidgetParameters();
        }
        widgetParameters.loadParameters(context);
        return widgetParameters;
    }

    public void loadParameters(Context context) {
        currencies = null;

        SharedPreferences sharedPreferences = context.getSharedPreferences(CCWIDGET_KEY,
                Context.MODE_PRIVATE);

        String currencyName = sharedPreferences.getString(MAIN_CURRENCY_NAME, EMPTY);

        if (!EMPTY.equals(currencyName)) {
            currencies = new ArrayList<>(3);
            currencies.add(new Currency(currencyName));

            currencyName = sharedPreferences.getString(CURRENCY1_NAME, EMPTY);
            float currencyRate = sharedPreferences.getFloat(CURRENCY1_RATE, 0);
            float currencyCount = sharedPreferences.getFloat(CURRENCY1_COUNT, 0);

            if (!EMPTY.equals(currencyName) && currencyRate != 0 && currencyCount != 0) {
                currencies.add(new Currency(currencyName, currencyRate, currencyCount));

                currencyName = sharedPreferences.getString(CURRENCY2_NAME, EMPTY);
                currencyRate = sharedPreferences.getFloat(CURRENCY2_RATE, 0);
                currencyCount = sharedPreferences.getFloat(CURRENCY2_COUNT, 0);
                if (!EMPTY.equals(currencyName) && currencyRate != 0 && currencyCount != 0) {
                    currencies.add(new Currency(currencyName, currencyRate, currencyCount));
                }
            }
            mainDisplayCurrencyIndex = sharedPreferences.getInt(MAIN_DISPLAY_CURRENCY_INDEX, 0);
        }
    }

    public void saveParameters(Context context, ArrayList<Currency> data, int mainDisplayCurrencyIndex) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CCWIDGET_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (data != null){
            //noinspection unchecked
            currencies = (ArrayList<Currency>)data.clone();
        } else if(currencies != null) {
                currencies.clear();
                currencies = null;
        }

        if (currencies != null && currencies. size() > 0 && !EMPTY.equals(currencies.get(0).name)) {
            editor.putString(MAIN_CURRENCY_NAME, currencies.get(0).name);

            if (currencies.size() > 1) {
                editor.putString(CURRENCY1_NAME, currencies.get(1).name);
                editor.putFloat(CURRENCY1_RATE, currencies.get(1).rate);
                editor.putFloat(CURRENCY1_COUNT, currencies.get(1).count);
                if (currencies.size() > 2) {
                    editor.putString(CURRENCY2_NAME, currencies.get(2).name);
                    editor.putFloat(CURRENCY2_RATE, currencies.get(2).rate);
                    editor.putFloat(CURRENCY2_COUNT, currencies.get(2).count);
                } else {
                    removeCurrency2(editor);
                }

            } else {
                removeCurrency1(editor);
                removeCurrency2(editor);
            }
            this.mainDisplayCurrencyIndex = mainDisplayCurrencyIndex;
            editor.putInt(MAIN_DISPLAY_CURRENCY_INDEX, this.mainDisplayCurrencyIndex);
        } else {
            editor.remove(MAIN_CURRENCY_NAME);
            removeCurrency1(editor);
            removeCurrency2(editor);
        }
        editor.apply();
    }

    private void removeCurrency1(SharedPreferences.Editor editor) {
        editor.remove(CURRENCY1_NAME);
        editor.remove(CURRENCY1_RATE);
        editor.remove(CURRENCY1_COUNT);
    }

    private void removeCurrency2(SharedPreferences.Editor editor) {
        editor.remove(CURRENCY2_NAME);
        editor.remove(CURRENCY2_RATE);
        editor.remove(CURRENCY2_COUNT);
    }

    public static CharSequence truncate(CharSequence text) {
        return text.length() < 3 ? text : text.subSequence(0, 3);
    }
    public static  CharSequence toNumberText(EditText editText) {
        CharSequence result = null;
        try {
            result = floatToCharSequence(charSequenceToFloat(editText.getText()));
        } catch (Exception ignored) {
        }
        return result;
    }

    public static float charSequenceToFloat(CharSequence text) {
        float result = 0;
        try {
            result = Objects.requireNonNull(DF.parse(text.toString().
                            replace(DOT, DS).
                            replace(COMMA, DS))).
                    floatValue();
        } catch (Exception ignored) {
        }
        return result;
    }
    public static CharSequence floatToCharSequence(float number) {
        if (number == 0) {
            return ZERO;
        } else {
            return DF.format(number);
        }
    }
    public static float checkInfinity(float value) {
        float middleValue = value;

        if (value > MAX_VALUE) {
            middleValue = Float.POSITIVE_INFINITY;
        } else if (value < MIN_VALUE) {
            middleValue = Float.NEGATIVE_INFINITY;
        }
        return middleValue;
    }
}

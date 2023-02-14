package ws.grigory.currencycalculator.calculator;

import static ws.grigory.currencycalculator.CCWidgetLarge.DF;

import java.util.ArrayList;

import ws.grigory.currencycalculator.Currency;

public class Display {
    public static final String EMPTY = "";
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public ArrayList<Currency> currencies;
    public int currencyIndex;
    protected final StringBuilder display = new StringBuilder();

    public Display(int currencyIndex, ArrayList<Currency> currencies) {
        this.currencies = currencies;
        this.currencyIndex = currencyIndex;
    }

    public void shift() {
        if(currencyIndex < currencies.size()) {
            currencyIndex = currencyIndex == 0 ? currencies.size() - 1 : --currencyIndex;
            writeCurrencyValueToDisplay();
        }
    }
    public void writeCurrencyValueToDisplay() {
        clearDisplay();
        display.append((currencyIndex >= currencies.size() ||
                currencies.get(currencyIndex).value == 0) ? EMPTY :
                floatToString(currencies.get(currencyIndex).value));
    }

    public void calculateCurrencyValueToDisplay(float baseValue) {
        if(currencyIndex < currencies.size()) {
            currencies.get(currencyIndex).calculateValue(baseValue);
            writeCurrencyValueToDisplay();
        }
    }

    public void zeroingDisplay() {
        if(currencyIndex < currencies.size()){
            currencies.get(currencyIndex).value = 0;
        }
        display.setLength(0);
    }

    public void clearDisplay() {
        display.setLength(0);
    }

    protected String floatToString(float value) {
        if(value == Float.NEGATIVE_INFINITY || value == Float.POSITIVE_INFINITY) {
            return Float.toString(value);
        } else if(value == 0) {
            return ZERO;
        } else {
            return DF.format(Math.round(value * 100) / 100f);
        }
    }

    public String getValue() {
        return currencyIndex >= currencies.size() ? EMPTY : display.toString();
    }

    public String getCurrencyName() {
        return currencyIndex >= currencies.size() ? EMPTY : currencies.get(currencyIndex).name;
    }
}

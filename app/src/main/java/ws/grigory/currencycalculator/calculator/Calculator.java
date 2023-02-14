package ws.grigory.currencycalculator.calculator;

import static ws.grigory.currencycalculator.CCWidgetLarge.C;
import static ws.grigory.currencycalculator.CCWidgetLarge.DIV;
import static ws.grigory.currencycalculator.CCWidgetLarge.EVAL;
import static ws.grigory.currencycalculator.CCWidgetLarge.MINUS;
import static ws.grigory.currencycalculator.CCWidgetLarge.MUL;
import static ws.grigory.currencycalculator.CCWidgetLarge.PLUS;
import static ws.grigory.currencycalculator.CCWidgetLarge.SHIFT;
import static ws.grigory.currencycalculator.CCWidgetLarge.ZERO_CHAR;
import static ws.grigory.currencycalculator.calculator.Display.EMPTY;

import android.content.Context;

import java.util.ArrayList;

import ws.grigory.currencycalculator.Currency;
import ws.grigory.currencycalculator.WidgetParameters;

public class Calculator {
    private static final String OPERATIONS = String.valueOf(new char[]{PLUS, MINUS, MUL, DIV, EVAL});
    private float yRegistry = 0;
    private char operation = EVAL;
    private final Display[] displays = new Display[3];
    private final MainDisplay mainDisplay;
    private boolean isNewValue = true;
    private ArrayList<Currency> currencies;

    public Calculator(Context context) {
        loadCurrencies(context);
        mainDisplay = new MainDisplay(0, this.currencies);
        displays[0] = mainDisplay;
        displays[1] = new Display(1, this.currencies);
        displays[2] = new Display(2, this.currencies);

        mainDisplay.processDisplayChar(true, ZERO_CHAR);
    }

    public void setData(char data) {
        Currency currency = mainDisplay.getMainDisplayCurrency();
        if (C == data) {
            clear();
        } else {
            if (!mainDisplay.getMainDisplayCurrency().isInfinity()) {
                if (SHIFT == data) {
                    shift();
                } else {
                    if (OPERATIONS.indexOf(data) < 0) {
                        isNewValue = mainDisplay.processDisplayChar(isNewValue, data);
                        mainDisplay.writeDisplayValueToCurrency();
                    } else {
                        if (!isNewValue) {
                            switch (operation) {
                                case PLUS:
                                    currency.plus(yRegistry);
                                    break;
                                case MINUS:
                                    currency.minus(yRegistry);
                                    break;
                                case MUL:
                                    currency.mul(yRegistry);
                                    break;
                                case DIV:
                                    currency.div(yRegistry);
                            }
                            mainDisplay.writeCurrencyValueToDisplay();
                        }

                        if (currency.isInfinity() || data == EVAL) {
                            clearRegistries();
                        } else {
                            operation = data;
                            yRegistry = currency.value;
                        }
                        isNewValue = true;
                    }
                    mainDisplay.createExpression(operation, yRegistry);
                    calculateCurrencies(currency.value);
                }
            }
        }
    }

    private void calculateCurrencies(float value) {
        mainDisplay.getMainDisplayCurrency().value = value;
        float baseValue = mainDisplay.getMainDisplayCurrency().getBaseValue();

        for (int i = 1; i < displays.length; i++) {
            displays[i].calculateCurrencyValueToDisplay(baseValue);
        }
    }

    private void shift() {
        for (Display display : displays) {
            display.shift();
        }
        clearRegistries();
    }

    public void clear() {
        clearRegistries();

        for (Display display : displays) {
            display.zeroingDisplay();
        }

        mainDisplay.processDisplayChar(true, ZERO_CHAR);
        mainDisplay.createExpression(operation, yRegistry);
    }

    public void reset(Context context){
        loadCurrencies(context);
        displays[0].currencyIndex = 0;
        displays[0].currencies = currencies;
        displays[1].currencyIndex = 1;
        displays[1].currencies = currencies;
        displays[2].currencyIndex = 2;
        displays[2].currencies = currencies;
        clear();
    }

    private void clearRegistries() {
        yRegistry = 0;
        operation = EVAL;
        isNewValue = true;
    }

    public Display[] getDisplays() {
        return displays;
    }

    @SuppressWarnings("unchecked")
    private void loadCurrencies(Context context) {
        ArrayList<Currency> data = WidgetParameters.getWidgetParameters(context).currencies;
        if(data == null) {
            currencies = new ArrayList<>(3);
            currencies.add(new Currency(EMPTY));
        } else {
            currencies = (ArrayList<Currency>)data.clone();
        }
    }
}



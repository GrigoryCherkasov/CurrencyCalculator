package ws.grigory.currencycalculator.calculator;

import static ws.grigory.currencycalculator.CCWidgetLarge.BS;
import static ws.grigory.currencycalculator.CCWidgetLarge.DF;
import static ws.grigory.currencycalculator.CCWidgetLarge.DS;
import static ws.grigory.currencycalculator.CCWidgetLarge.EVAL;
import static ws.grigory.currencycalculator.CCWidgetLarge.ZERO_CHAR;
import static ws.grigory.currencycalculator.WidgetParameters.checkInfinity;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Objects;

import ws.grigory.currencycalculator.Currency;
public class MainDisplay extends Display {

    public final StringBuilder expression = new StringBuilder();

    public MainDisplay(int currencyIndex, ArrayList<Currency> currencies) {
        super(currencyIndex, currencies);
    }

    public Currency getMainDisplayCurrency() {
        return currencies.get(currencyIndex);
    }

    public boolean processDisplayChar(boolean isNewValue, char c) {
        if (isNewValue) {
            clearDisplay();
        }

        int displayLength = display.length();
        if (BS == c) {
            if (displayLength > 1) {
                display.delete(displayLength - 1, displayLength);
                isNewValue = false;
            } else {
                clearDisplay();
                display.append(ZERO_CHAR);
                isNewValue = true;
            }
        } else {

            int indexDS = display.indexOf(Character.toString(DS));

            if (indexDS < 0 || (c != DS && displayLength - indexDS < 3)) {
                if (displayLength == 1 && display.charAt(0) == ZERO_CHAR && c != DS) {
                    display.delete(0, 1);
                }
                if(displayLength == 0 && c == DS){
                    display.append(ZERO_CHAR);
                }
                display.append(c);
                isNewValue = false;
            }
        }
        return isNewValue;
    }

    @Override
    public void writeCurrencyValueToDisplay() {
        clearDisplay();
        display.append(floatToString(currencies.get(currencyIndex).value));
    }

    public void writeDisplayValueToCurrency() {
        try {
            getMainDisplayCurrency().value = checkInfinity(Objects.requireNonNull(
                    DF.parse(display.toString())).floatValue());

            if(getMainDisplayCurrency().isInfinity()){
                writeCurrencyValueToDisplay();
            }

        } catch (ParseException ignored) {
        }
    }

    public void createExpression(char operation, float yRegistry) {
        expression.setLength(0);
        if (!getMainDisplayCurrency().isInfinity()) {
            if (operation != EVAL) {
                expression.append(floatToString(yRegistry));
                expression.append(' ');
                expression.append(operation);
            }
        }
    }
}

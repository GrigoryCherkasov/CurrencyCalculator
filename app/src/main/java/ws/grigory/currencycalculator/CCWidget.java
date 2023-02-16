package ws.grigory.currencycalculator;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.IdRes;

import java.text.DecimalFormat;

import ws.grigory.currencycalculator.calculator.Calculator;
import ws.grigory.currencycalculator.calculator.Display;
import ws.grigory.currencycalculator.calculator.MainDisplay;
import ws.grigory.currencycalculator.settings.SettingsActivity;


public class CCWidget extends AppWidgetProvider {
    public static final char ZERO_CHAR = '0';
    public static final char PLUS = '+';
    public static final char MINUS = '\u2013';
    public static final char MUL = '\u00D7';
    public static final char DIV = '\u00F7';
    public static final char EVAL = '=';
    public static final char C = 'C';
    public static final char BS = '\u232B';
    public static final char SHIFT = '\u21F5';
    public static final char DOT = '.';
    public static final char COMMA = ',';
    private static final String BUTTON_CODE = "BUTTON_CODE";
    private static final String CLASS_CODE = "CLASS_CODE";
    public static final String CURRENCIES = "CURRENCIES";
    public static final String DIGITS = "0123456789,.";
    public static final DecimalFormat DF = new DecimalFormat("#.##");
    public static final char DS = DF.getDecimalFormatSymbols().getDecimalSeparator();
    private static final Intent CURRENCIES_INTENT = new Intent(CURRENCIES);

    static {
        CURRENCIES_INTENT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    private static Calculator CALCULATOR;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (CALCULATOR == null) {
            CALCULATOR = new Calculator(context);
        }

        boolean isButton = intent.hasExtra(BUTTON_CODE);
        boolean isNewCurrency = intent.hasExtra(CURRENCIES);

        if (isButton || isNewCurrency) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            ComponentName componentName = new ComponentName(context, this.getClass());

            if (isNewCurrency) {
                CALCULATOR.reset(context);
                repaintAllWidgets(context, appWidgetManager);
            } else if (appWidgetManager.getAppWidgetIds(componentName).length > 0 &&
                    this.getClass().getSimpleName().hashCode() ==
                            intent.getIntExtra(CLASS_CODE, 0)) {

                CALCULATOR.setData(intent.getCharExtra(BUTTON_CODE, EVAL));
                repaintAllWidgets(context, appWidgetManager);
            }
        }
    }

    private void showWidget(Context context, AppWidgetManager appWidgetManager,
                            Class<? extends CCWidget> widgetClass) {

        ComponentName componentName = new ComponentName(context, widgetClass);

        int[] idWidgets = appWidgetManager.getAppWidgetIds(componentName);
        if (idWidgets.length > 0) {

            int classCode = widgetClass.getSimpleName().hashCode();
            RemoteViews widget = new RemoteViews(componentName.getPackageName(), R.layout.ccwidget);

            setOnClick(context, widget);
            setOnClick(context, widget, R.id.shift, SHIFT, classCode);
            setOnClick(context, widget, R.id.bs, BS, classCode);
            setOnClick(context, widget, R.id.c, C, classCode);
            setOnClick(context, widget, R.id.div, DIV, classCode);
            setOnClick(context, widget, R.id.mul, MUL, classCode);
            setOnClick(context, widget, R.id.minus, MINUS, classCode);
            setOnClick(context, widget, R.id.plus, PLUS, classCode);
            setOnClick(context, widget, R.id.ds, DS, classCode);
            setOnClick(context, widget, R.id.eval, EVAL, classCode);
            setOnClick(context, widget, R.id.d0, '0', classCode);
            setOnClick(context, widget, R.id.d1, '1', classCode);
            setOnClick(context, widget, R.id.d2, '2', classCode);
            setOnClick(context, widget, R.id.d3, '3', classCode);
            setOnClick(context, widget, R.id.d4, '4', classCode);
            setOnClick(context, widget, R.id.d5, '5', classCode);
            setOnClick(context, widget, R.id.d6, '6', classCode);
            setOnClick(context, widget, R.id.d7, '7', classCode);
            setOnClick(context, widget, R.id.d8, '8', classCode);
            setOnClick(context, widget, R.id.d9, '9', classCode);

            Display[] displays = CALCULATOR.getDisplays();
            widget.setTextViewText(R.id.currency2, displays[2].getCurrencyName());
            widget.setTextViewText(R.id.value2, displays[2].getValue());

            widget.setTextViewText(R.id.currency1, displays[1].getCurrencyName());
            widget.setTextViewText(R.id.value1, displays[1].getValue());

            widget.setTextViewText(R.id.currency0, displays[0].getCurrencyName());
            widget.setTextViewText(R.id.value0, displays[0].getValue());
            widget.setTextViewText(R.id.expression, (
                    (MainDisplay) displays[0]).expression.toString());

            appWidgetManager.updateAppWidget(idWidgets, widget);
        }
    }

    private void setOnClick(Context context, RemoteViews widget, @IdRes int viewId, char code,
                            int classcode) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(BUTTON_CODE, code);
        intent.putExtra(CLASS_CODE, classcode);

        int flag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
        } else {
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, code, intent, flag);
        widget.setTextViewText(viewId, Character.toString(code));
        widget.setOnClickPendingIntent(viewId, pendingIntent);
    }

    private void setOnClick(Context context, RemoteViews widget) {
        CURRENCIES_INTENT.setClass(context, SettingsActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                CURRENCIES_INTENT,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        widget.setOnClickPendingIntent(R.id.setCurrency, pendingIntent);
    }

    private void repaintAllWidgets(Context context, AppWidgetManager appWidgetManager) {
        showWidget(context, appWidgetManager, CCWidgetSmall.class);
        showWidget(context, appWidgetManager, CCWidgetMedium.class);
        showWidget(context, appWidgetManager, CCWidgetLarge.class);
    }

    @Override
    public void onEnabled(Context context) {
        repaintAllWidgets(context, AppWidgetManager.getInstance(context));
    }

    @Override
    public void onDisabled(Context context) {
        repaintAllWidgets(context, AppWidgetManager.getInstance(context));
    }
}
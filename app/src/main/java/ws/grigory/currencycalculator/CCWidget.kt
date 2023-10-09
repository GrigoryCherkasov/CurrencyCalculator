package ws.grigory.currencycalculator

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.IdRes
import ws.grigory.currencycalculator.Constants.BS
import ws.grigory.currencycalculator.Constants.BUTTON_CODE
import ws.grigory.currencycalculator.Constants.C
import ws.grigory.currencycalculator.Constants.CLASS_CODE
import ws.grigory.currencycalculator.Constants.CURRENCIES
import ws.grigory.currencycalculator.Constants.DIV
import ws.grigory.currencycalculator.Constants.DS
import ws.grigory.currencycalculator.Constants.EVAL
import ws.grigory.currencycalculator.Constants.INVALIDATE
import ws.grigory.currencycalculator.Constants.MINUS
import ws.grigory.currencycalculator.Constants.MUL
import ws.grigory.currencycalculator.Constants.PLUS
import ws.grigory.currencycalculator.Constants.SHIFT
import ws.grigory.currencycalculator.calculator.Calculator
import ws.grigory.currencycalculator.calculator.Display
import ws.grigory.currencycalculator.calculator.MainDisplay
import ws.grigory.currencycalculator.settings.SettingsActivity

open class CCWidget : AppWidgetProvider() {

    companion object {
        val CURRENCIES_INTENT: Intent = Intent(CURRENCIES).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        var CALCULATOR: Calculator? = null
    }

    override fun onReceive(context: Context, intent: Intent) {

        super.onReceive(context, intent)

        if (CALCULATOR == null) {
            CALCULATOR = Calculator(context)
        }

        val isNewCurrency: Boolean = intent.hasExtra(CURRENCIES)
        val isInvalidate: Boolean = intent.hasExtra(INVALIDATE)

        if (intent.hasExtra(BUTTON_CODE) || isNewCurrency || isInvalidate) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, this::class.java)
            val classCode = intent.getIntExtra(CLASS_CODE, 0)
            when {
                intent.hasExtra(CURRENCIES) -> {
                    CALCULATOR!!.reset(context)

                }

                isInvalidate -> {
                    CALCULATOR!!.invalidate()
                }

                appWidgetManager.getAppWidgetIds(componentName).isNotEmpty()
                        && this::class.java.simpleName.hashCode() == classCode -> {
                    val button = intent.getCharExtra(BUTTON_CODE, EVAL)
                    CALCULATOR!!.setData(button, context)
                    if (!(button == EVAL || button == C)) {
                        (context.applicationContext as CalculatorApplication).launchEvalTimeout()
                    }
                }
            }
            showAllWidgetsDisplay(context, appWidgetManager, CALCULATOR!!.invalidated)
        } else if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)) {
            initAllWidgets(context, AppWidgetManager.getInstance(context))
        }
    }

    override fun onDisabled(context: Context){
        initAllWidgets(context, AppWidgetManager.getInstance(context))
    }

    private fun showAllWidgetsDisplay(
        context: Context,
        appWidgetManager: AppWidgetManager,
        invalidated: Boolean
    ) {
        showWidgetDisplay(
            context, appWidgetManager, CCWidgetSmall::class.java,
            R.layout.ccwidget_small, invalidated
        )
        showWidgetDisplay(
            context, appWidgetManager, CCWidgetMedium::class.java,
            R.layout.ccwidget_medium, invalidated
        )
        showWidgetDisplay(
            context, appWidgetManager, CCWidgetLarge::class.java,
            R.layout.ccwidget_large, invalidated
        )
    }

    private fun showWidgetDisplay(
        context: Context, appWidgetManager: AppWidgetManager,
        widgetClass: Class<out Any>, layoutId: Int, invalidated: Boolean
    ) {

        val componentName = ComponentName(context, widgetClass)

        val idWidgets: IntArray = appWidgetManager.getAppWidgetIds(componentName)
        if (idWidgets.isNotEmpty() && widgetClass == this.javaClass) {
            val widget = RemoteViews(componentName.packageName, layoutId)
            setTextValue(widget, invalidated)
            appWidgetManager.updateAppWidget(idWidgets, widget)
        }
    }

    private fun initAllWidgets(context: Context, appWidgetManager: AppWidgetManager) {

        initWidget(
            context,
            appWidgetManager,
            CCWidgetSmall::class.java,
            R.layout.ccwidget_small,
            CALCULATOR!!.invalidated
        )
        initWidget(
            context,
            appWidgetManager,
            CCWidgetMedium::class.java,
            R.layout.ccwidget_medium,
            CALCULATOR!!.invalidated
        )
        initWidget(
            context,
            appWidgetManager,
            CCWidgetLarge::class.java,
            R.layout.ccwidget_large,
            CALCULATOR!!.invalidated
        )
    }

    private fun initWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        widgetClass: Class<out Any>, layoutId: Int, invalidated: Boolean
    ) {
        val componentName = ComponentName(context, widgetClass)

        val idWidgets: IntArray = appWidgetManager.getAppWidgetIds(componentName)
        if (idWidgets.isNotEmpty()) {
            val classCode: Int = widgetClass.simpleName.hashCode()
            val widget = RemoteViews(componentName.packageName, layoutId)

            setOnClick(context, widget)
            setOnClick(context, widget, R.id.shift, SHIFT, classCode)
            setOnClick(context, widget, R.id.bs, BS, classCode)
            setOnClick(context, widget, R.id.c, C, classCode)
            setOnClick(context, widget, R.id.div, DIV, classCode)
            setOnClick(context, widget, R.id.mul, MUL, classCode)
            setOnClick(context, widget, R.id.minus, MINUS, classCode)
            setOnClick(context, widget, R.id.plus, PLUS, classCode)
            setOnClick(context, widget, R.id.ds, DS, classCode)
            setOnClick(context, widget, R.id.eval, EVAL, classCode)
            setOnClick(context, widget, R.id.d0, '0', classCode)
            setOnClick(context, widget, R.id.d1, '1', classCode)
            setOnClick(context, widget, R.id.d2, '2', classCode)
            setOnClick(context, widget, R.id.d3, '3', classCode)
            setOnClick(context, widget, R.id.d4, '4', classCode)
            setOnClick(context, widget, R.id.d5, '5', classCode)
            setOnClick(context, widget, R.id.d6, '6', classCode)
            setOnClick(context, widget, R.id.d7, '7', classCode)
            setOnClick(context, widget, R.id.d8, '8', classCode)
            setOnClick(context, widget, R.id.d9, '9', classCode)

            setTextValue(widget, invalidated)
            appWidgetManager.updateAppWidget(idWidgets, widget)
        }
    }

    private fun setOnClick(context: Context, widget: RemoteViews) {
        CURRENCIES_INTENT.setClass(context, SettingsActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            CURRENCIES_INTENT,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        widget.setOnClickPendingIntent(R.id.setCurrency, pendingIntent)
    }

    private fun setOnClick(
        context: Context, widget: RemoteViews, @IdRes viewId: Int, code: Char, classCode: Int
    ) {
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)

        intent.putExtra(BUTTON_CODE, code)
        intent.putExtra(CLASS_CODE, classCode)

        val flag: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        widget.setOnClickPendingIntent(
            viewId, PendingIntent.getBroadcast(
                context,
                code.code, intent, flag
            )
        )
    }

    private fun setTextValue(widget: RemoteViews, invalidated: Boolean) {
        val displays: Array<Display> = CALCULATOR!!.getDisplays()
        widget.setTextViewText(R.id.currency2, displays[2].currencyName)
        widget.setTextViewText(R.id.value2, displays[2].value)

        widget.setTextViewText(R.id.currency1, displays[1].currencyName)
        widget.setTextViewText(R.id.value1, displays[1].value)

        widget.setTextViewText(R.id.currency0, displays[0].currencyName)
        widget.setTextViewText(R.id.value0, displays[0].value)
        widget.setTextColor(R.id.value0, if (invalidated) Color.YELLOW else Color.WHITE)
        widget.setTextViewText(
            R.id.expression, (
                    displays[0] as MainDisplay).expression.toString()
        )
    }
}
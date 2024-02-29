package ws.grigory.currencycalculator

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
import ws.grigory.currencycalculator.Constants.EVAL_COLOR
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

            showWidgetDisplay(
                context, appWidgetManager, CALCULATOR!!.invalidated
            )

        } else if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)) {
            initWidget(
                context,
                AppWidgetManager.getInstance(context),
                CALCULATOR!!.invalidated
            )
        }
    }

    override fun onDisabled(context: Context) {
        initWidget(
            context,
            AppWidgetManager.getInstance(context),
            CALCULATOR!!.invalidated
        )
    }


    private fun showWidgetDisplay(
        context: Context, appWidgetManager: AppWidgetManager, invalidated: Boolean
    ) {
        val componentName = ComponentName(context, CCWidget::class.java)
        val idWidgets: IntArray = appWidgetManager.getAppWidgetIds(componentName)
        if (idWidgets.isNotEmpty()) {
            val widget = RemoteViews(componentName.packageName, R.layout.ccwidget)
            setTextValue(widget, invalidated)
            appWidgetManager.updateAppWidget(idWidgets, widget)
        }
    }

    private fun initWidget(
        context: Context, appWidgetManager: AppWidgetManager, invalidated: Boolean
    ) {
        val componentName = ComponentName(context, CCWidget::class.java)

        val idWidgets: IntArray = appWidgetManager.getAppWidgetIds(componentName)
        if (idWidgets.isNotEmpty()) {
            val classCode: Int = CCWidget::class.java.simpleName.hashCode()
            val widget = RemoteViews(componentName.packageName, R.layout.ccwidget)

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

        widget.setOnClickPendingIntent(
            R.id.setCurrency, PendingIntent.getActivity(
                context, 0,

                CURRENCIES_INTENT,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    private fun setOnClick(
        context: Context, widget: RemoteViews, @IdRes viewId: Int, code: Char, classCode: Int
    ) {
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)

        intent.putExtra(BUTTON_CODE, code)
        intent.putExtra(CLASS_CODE, classCode)

        val flag: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
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
        widget.setImageViewBitmap(
            R.id.currency2,
            getImageForString(displays[2].currencyName, Color.WHITE)
        )
        widget.setImageViewBitmap(R.id.value2, getImageForString(displays[2].value, Color.WHITE))
        widget.setImageViewBitmap(
            R.id.currency1,
            getImageForString(displays[1].currencyName, Color.WHITE)
        )
        widget.setImageViewBitmap(R.id.value1, getImageForString(displays[1].value, Color.WHITE))
        widget.setImageViewBitmap(
            R.id.currency0,
            getImageForString(displays[0].currencyName, Color.WHITE)
        )
        widget.setImageViewBitmap(
            R.id.value0,
            getImageForString(displays[0].value, if (invalidated) EVAL_COLOR else Color.WHITE)
        )
        widget.setImageViewBitmap(
            R.id.expression,
            getImageForString((displays[0] as MainDisplay).expression.toString(), Color.WHITE)
        )
    }

    private fun getImageForString(string: String, color: Int): Bitmap? {
        return if (string.isNotEmpty()) {
            val paint = Paint()
            paint.isAntiAlias = true
            paint.textSize = 140f
            paint.color = color
            val baseline = -paint.ascent()
            val image = Bitmap.createBitmap(
                paint.measureText(string).toInt(),
                (baseline * 1.2f).toInt(),
                Bitmap.Config.ARGB_8888
            )
            Canvas(image).drawText(string, 0f, baseline, paint)
            image
        } else {
            null
        }
    }
}
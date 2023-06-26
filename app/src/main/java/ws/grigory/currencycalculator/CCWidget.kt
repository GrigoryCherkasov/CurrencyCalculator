package ws.grigory.currencycalculator


import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.work.Configuration
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

private const val JOB_ID = 1
private const val LATENCY = 5000L

open class CCWidget : AppWidgetProvider() {

    companion object {
        val CURRENCIES_INTENT: Intent = Intent(CURRENCIES)
        val INVALIDATE_INTENT = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        var CALCULATOR: Calculator? = null
        var INVALIDATE_JOB_INFO: JobInfo? = null

        init {
            CURRENCIES_INTENT.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            INVALIDATE_INTENT.putExtra(INVALIDATE, true)
        }
    }

    class InvalidateJobService : JobService() {
        init {
            Configuration.Builder().setJobSchedulerJobIdRange(0, 1000).build()
        }

        override fun onStartJob(params: JobParameters): Boolean {
            sendBroadcast(INVALIDATE_INTENT)
            return true
        }

        override fun onStopJob(params: JobParameters): Boolean {
            return true
        }
    }

    override fun onReceive(context: Context, intent: Intent) {

        super.onReceive(context, intent)

        if (CALCULATOR == null) {
            CALCULATOR = Calculator(context)
        }

        if (INVALIDATE_JOB_INFO == null) {
            INVALIDATE_JOB_INFO = JobInfo.Builder(
                JOB_ID,
                ComponentName(context, InvalidateJobService::class.java)
            )
                .setOverrideDeadline(LATENCY)
                .setMinimumLatency(LATENCY)
                .build()
        }

        val isButton: Boolean = intent.hasExtra(BUTTON_CODE)
        val isNewCurrency: Boolean = intent.hasExtra(CURRENCIES)
        val isInvalidate: Boolean = intent.hasExtra(INVALIDATE)
        val isRepaint: Boolean = intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)

        if (isButton || isNewCurrency || isInvalidate) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, this::class.java)
            val classCode = intent.getIntExtra(CLASS_CODE, 0)
            if (isNewCurrency) {
                CALCULATOR!!.reset(context)
            } else if (isInvalidate) {
                CALCULATOR!!.invalidate()
            } else if (appWidgetManager.getAppWidgetIds(componentName).isNotEmpty()
                && this::class.java.simpleName.hashCode() == classCode
            ) {
                val button = intent.getCharExtra(BUTTON_CODE, EVAL)

                CALCULATOR!!.setData(button, context)

                if (button == EVAL || button == C) {
                    stopInvalidate(context)
                } else {
                    startInvalidate(context)
                }
            }
            showAllWidgetsDisplay(context, appWidgetManager, CALCULATOR!!.invalidated)
        } else if (isRepaint) {
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
        widgetClass: Class<out Any>, layout_id: Int, invalidated: Boolean
    ) {

        val componentName = ComponentName(context, widgetClass)

        val idWidgets: IntArray = appWidgetManager.getAppWidgetIds(componentName)
        if (idWidgets.isNotEmpty() && widgetClass == this.javaClass) {
            val widget = RemoteViews(componentName.packageName, layout_id)
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
        widgetClass: Class<out Any>, layout_id: Int, invalidated: Boolean
    ) {
        val componentName = ComponentName(context, widgetClass)

        val idWidgets: IntArray = appWidgetManager.getAppWidgetIds(componentName)
        if (idWidgets.isNotEmpty()) {
println("init $widgetClass")
            val classCode: Int = widgetClass.simpleName.hashCode()
            val widget = RemoteViews(componentName.packageName, layout_id)

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
        widget.setTextViewText(R.id.currency2, displays[2].getCurrencyName())
        widget.setTextViewText(R.id.value2, displays[2].getValue())

        widget.setTextViewText(R.id.currency1, displays[1].getCurrencyName())
        widget.setTextViewText(R.id.value1, displays[1].getValue())

        widget.setTextViewText(R.id.currency0, displays[0].getCurrencyName())
        widget.setTextViewText(R.id.value0, displays[0].getValue())
        widget.setTextColor(R.id.value0, if (invalidated) Color.YELLOW else Color.WHITE)
        widget.setTextViewText(
            R.id.expression, (
                    displays[0] as MainDisplay).expression.toString()
        )
    }

    private fun startInvalidate(context: Context) {
        stopInvalidate(context).schedule(INVALIDATE_JOB_INFO!!)
    }

    private fun stopInvalidate(context: Context): JobScheduler {
        val invalidateJobScheduler =
            context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        invalidateJobScheduler.cancel(JOB_ID)
        return invalidateJobScheduler
    }
}
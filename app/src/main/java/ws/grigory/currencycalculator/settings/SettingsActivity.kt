package ws.grigory.currencycalculator.settings

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ws.grigory.currencycalculator.Constants.ADD
import ws.grigory.currencycalculator.Constants.CURRENCIES
import ws.grigory.currencycalculator.Constants.EMPTY
import ws.grigory.currencycalculator.Constants.ONE
import ws.grigory.currencycalculator.Currency
import ws.grigory.currencycalculator.R
import ws.grigory.currencycalculator.RateLoader
import ws.grigory.currencycalculator.WidgetParameters

class SettingsActivity : AppCompatActivity() {
    private lateinit var widgetParameters: WidgetParameters
    private lateinit var currencyAdapter: CurrencyAdapter

    private var ratesList: MutableMap<String, Float> = HashMap()
    private var currenciesList: MutableList<RateLoader.CurrencyData> = ArrayList()
    private lateinit var currencies: ArrayList<Currency>


    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        setContentView(R.layout.activity_currency_list)

        val currencyListView: RecyclerView = findViewById(R.id.currencyList)
        val addButton: FloatingActionButton = findViewById(R.id.fab)

        widgetParameters = WidgetParameters.getWidgetParameters(this)
        val data = widgetParameters.currencies

        currencies =
            if (data[0].name.isEmpty()) ArrayList() else data.clone() as ArrayList<Currency>

        addButton.visibility = if (currencies.size >= 3) INVISIBLE else VISIBLE
        currencyListView.layoutManager = LinearLayoutManager(this)
        currencyAdapter = CurrencyAdapter(this, currencies)
        currencyListView.adapter = currencyAdapter
        addButton.setOnClickListener {
            if (currencies.isEmpty()) {
                changeBaseCurrency(EMPTY, ADD)
            } else if (currencies.size < 3) {
                changeCurrency(EMPTY, ONE, EMPTY, ADD)
            }
        }
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backPressed()
                }
            }
        )
    }

    fun changeBaseCurrency(currencyNameCS: CharSequence, mode: String?) {
        val searchProgressLayout = findViewById<LinearLayout>(R.id.searchProgressLayout)
        val searchProgressBar = findViewById<ProgressBar>(R.id.searchProgressBar)
        val handler = Handler(Looper.myLooper()!!)
        Thread {
            handler.post {
                searchProgressLayout.visibility = VISIBLE
                searchProgressBar.isEnabled = false
            }
            val rateLoader = RateLoader()
            currenciesList = rateLoader.getCurrenciesList(applicationContext)
            handler.post {
                searchProgressLayout.visibility = INVISIBLE
                searchProgressBar.isEnabled = true
                val newFragment = CurrencyNameDialog(
                    currencyNameCS,
                    currenciesList,
                    ratesList,
                    currencies,
                    currencyAdapter
                )
                newFragment.show(this.supportFragmentManager, mode)
            }
        }.start()
    }

    fun changeCurrency(
        currencyRateCS: CharSequence?, currencyCountCS: CharSequence?,
        currencyNameCS: CharSequence, mode: String?
    ) {
        val searchProgressLayout = findViewById<LinearLayout>(R.id.searchProgressLayout)
        val searchProgressBar = findViewById<ProgressBar>(R.id.searchProgressBar)
        val handler = Handler(Looper.myLooper()!!)
        Thread {
            handler.post {
                searchProgressLayout.visibility = VISIBLE
                searchProgressBar.isEnabled = false
            }

            var rateLoader: RateLoader? = null

            if (currenciesList.isEmpty()) {
                rateLoader = RateLoader()
                currenciesList = rateLoader.getCurrenciesList(applicationContext)
            }

            if (currencies.isNotEmpty() && ratesList.isEmpty()) {
                if (rateLoader == null) {
                    rateLoader = RateLoader()
                }
                ratesList = rateLoader.getCurrencyRate(currencies[0].name)
            }

            handler.post {
                searchProgressLayout.visibility = INVISIBLE
                searchProgressBar.isEnabled = true
                val newFragment = CurrencyDataDialog(
                    currencyRateCS,
                    currencyCountCS,
                    currencyNameCS,
                    currenciesList,
                    ratesList,
                    currencies,
                    currencyAdapter
                )
                newFragment.show(this.supportFragmentManager, mode)
            }
        }.start()
    }

    override fun onSupportNavigateUp(): Boolean {
        backPressed()
        return true
    }

    private fun backPressed() {
        widgetParameters.saveParameters(this, currencies, 0)
        intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        intent.putExtra(CURRENCIES, true)
        this.sendBroadcast(intent)
        finish()
    }
}

        /*val handler = Handler(Looper.myLooper()!!)
        Thread {*/
            /*handler.post {
                searchProgressLayout.visibility = VISIBLE
                searchProgressBar.isEnabled = false
            }*/
            /*val calendarStorage = CalendarStorage(this)
            calendarStorage.insert(2023, getNonWorkingDays(2023))
            println("years " + calendarStorage.getYears())*/
            /*println("2022 >" + calendarStorage.getData(LocalDate.now()))
            println("2023 >" + calendarStorage.getData(LocalDate.now().minusYears(1)))
            println("2024 >" + calendarStorage.getData(LocalDate.now().plusYears(1)))*/

            /*handler.post {
                searchProgressLayout.visibility = INVISIBLE
                searchProgressBar.isEnabled = true
                val newFragment = CurrencyNameDialog(
                    currencyNameCS,
                    currenciesList,
                    ratesList,
                    currencies,
                    currencyAdapter
                )
                newFragment.show(this.supportFragmentManager, mode)
            }*/
       //}.start()


        /*val cursor: Cursor? = applicationContext.contentResolver
            .query(
                Uri.parse("content://com.android.calendar/events"), arrayOf(
                    CalendarContract.Events.CALENDAR_ID,
                    CalendarContract.Events.TITLE,
                    CalendarContract.Events.DESCRIPTION
                ), null,
                null, null
            )

        if (cursor != null) {
            cursor.moveToFirst()
            do{
                println("calendar_id " + cursor.getString(0) + " " +
                        "title " + cursor.getString(1) + " " +
                        "description " + cursor.getString(2))
            } while(cursor.moveToNext())

            cursor.close()
        }*/
/*
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_settings)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/


/*    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_settings)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }*/

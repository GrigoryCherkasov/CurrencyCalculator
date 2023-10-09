package ws.grigory.currencycalculator.settings


import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.LinearLayoutManager
import ws.grigory.currencycalculator.Constants.ADD
import ws.grigory.currencycalculator.Constants.CURRENCIES
import ws.grigory.currencycalculator.Constants.EMPTY
import ws.grigory.currencycalculator.Constants.MAX_DISPLAY_CURRENCIES
import ws.grigory.currencycalculator.Constants.ONE
import ws.grigory.currencycalculator.Currency
import ws.grigory.currencycalculator.WidgetParameters
import ws.grigory.currencycalculator.databinding.SettingsActivityBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var widgetParameters: WidgetParameters
    private lateinit var currencies: ObservableArrayList<Currency>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.let {
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        val binding = SettingsActivityBinding.inflate(layoutInflater)
        val currencyListView = binding.currencyList
        val addButton = binding.fab

        setContentView(binding.root)

        widgetParameters = WidgetParameters.getWidgetParameters(this)
        val data = widgetParameters.currencies

        currencies = ObservableArrayList<Currency>()
        if (data.isNotEmpty()) currencies.addAll(data)

        val currencyEditor = CurrencyEditor(this, binding, currencies)
        val currencyAdapter = CurrencyAdapter(currencies, currencyEditor)

        currencies.addOnListChangedCallback(CurrenciesCallback(currencyAdapter, binding))

        currencyListView.layoutManager = LinearLayoutManager(this)
        currencyListView.adapter = currencyAdapter

        addButton.visibility = if (currencies.size >= MAX_DISPLAY_CURRENCIES) INVISIBLE else VISIBLE
        addButton.setOnClickListener {
            if (currencies.isEmpty()) {
                currencyEditor.addBaseCurrency()
            } else if (currencies.size < MAX_DISPLAY_CURRENCIES) {
                currencyEditor.changeCurrency(EMPTY, ONE, EMPTY, ADD)
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
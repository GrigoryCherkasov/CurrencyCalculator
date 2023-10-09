package ws.grigory.currencycalculator.settings

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ws.grigory.currencycalculator.Constants
import ws.grigory.currencycalculator.Currency
import ws.grigory.currencycalculator.RateLoader
import ws.grigory.currencycalculator.databinding.SettingsActivityBinding

class CurrencyEditor(
    private val activity: AppCompatActivity,
    private val binding: SettingsActivityBinding,
    private val currencies: ArrayList<Currency>,
) {
    private var currenciesList: List<RateLoader.CurrencyData> = ArrayList()
    private var ratesList: MutableMap<String, Float> = HashMap()

    fun addBaseCurrency() {

        activity.lifecycleScope.launch {
            showProgressBar(true)
            val rateLoader = RateLoader()
            currenciesList = withContext(Dispatchers.IO) {
                rateLoader.getCurrenciesList(activity)
            }
            showProgressBar(false)
            val newFragment = CurrencyNameDialog(
                Constants.EMPTY,
                currenciesList,
                currencies,
                ratesList
            )
            newFragment.show(activity.supportFragmentManager, Constants.ADD)
        }
    }
    fun changeCurrency(
        currencyRateCS: CharSequence?, currencyCountCS: CharSequence?,
        currencyNameCS: CharSequence, mode: String?
    ) {
        activity.lifecycleScope.launch {
            if (Constants.ADD == mode) {
                showProgressBar(true)
                withContext(Dispatchers.IO) {
                    var rateLoader: RateLoader? = null

                    if (currenciesList.isEmpty()) {
                        rateLoader = RateLoader()
                        currenciesList = rateLoader.getCurrenciesList(activity)
                    }

                    if (currencies.isNotEmpty() && ratesList.isEmpty()) {
                        if (rateLoader == null) {
                            rateLoader = RateLoader()
                        }
                        ratesList = rateLoader.getCurrencyRate(currencies[0].name)
                    }
                }
                showProgressBar(false)
            }

            val newFragment = CurrencyDataDialog(
                currencyRateCS,
                currencyCountCS,
                currencyNameCS,
                currenciesList,
                ratesList,
                currencies
            )
            newFragment.show(activity.supportFragmentManager, mode)
        }
    }

    private fun showProgressBar(show: Boolean) {
        with(binding) {
            searchProgressLayout.visibility =
                if (show) android.view.View.VISIBLE else android.view.View.INVISIBLE
            searchProgressBar.isEnabled = !show
        }
    }
}

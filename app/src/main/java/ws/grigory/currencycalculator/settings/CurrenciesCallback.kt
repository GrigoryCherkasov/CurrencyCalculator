package ws.grigory.currencycalculator.settings

import android.annotation.SuppressLint
import android.view.View
import androidx.databinding.ObservableList
import ws.grigory.currencycalculator.Constants
import ws.grigory.currencycalculator.Currency
import ws.grigory.currencycalculator.databinding.SettingsActivityBinding

class CurrenciesCallback(private val currencyAdapter: CurrencyAdapter,
                         private val binding: SettingsActivityBinding
) : ObservableList.OnListChangedCallback<ObservableList<Currency>>() {

    @SuppressLint("NotifyDataSetChanged")
    override fun onChanged(sender: ObservableList<Currency>?) {
        currencyAdapter.notifyDataSetChanged()
        checkFab(sender)
    }

    override fun onItemRangeChanged(
        sender: ObservableList<Currency>?,
        positionStart: Int,
        itemCount: Int
    ) {
        currencyAdapter.notifyItemRangeChanged(positionStart, itemCount)
        checkFab(sender)
    }

    override fun onItemRangeInserted(
        sender: ObservableList<Currency>?,
        positionStart: Int,
        itemCount: Int
    ) {
        currencyAdapter.notifyItemRangeInserted(positionStart, itemCount)
        checkFab(sender)
    }

    override fun onItemRangeMoved(
        sender: ObservableList<Currency>?,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
    }

    override fun onItemRangeRemoved(
        sender: ObservableList<Currency>?,
        positionStart: Int,
        itemCount: Int
    ) {
        currencyAdapter.notifyItemRangeRemoved(positionStart, itemCount)
        checkFab(sender)
    }

    private fun checkFab(sender: ObservableList<Currency>?) {
        binding.fab.visibility =
            if (sender != null && sender.size >= Constants.MAX_DISPLAY_CURRENCIES) View.INVISIBLE
            else View.VISIBLE
    }

}
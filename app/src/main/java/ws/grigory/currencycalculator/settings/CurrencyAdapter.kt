package ws.grigory.currencycalculator.settings

import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ws.grigory.currencycalculator.Constants.floatToCharSequence
import ws.grigory.currencycalculator.Currency

class CurrencyAdapter(
    private val currencies: ArrayList<Currency>,
    private val currencyEditor: CurrencyEditor
) :
    RecyclerView.Adapter<CurrencyViewHolder>() {

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency = currencies[position]
        with(holder) {
            currencyName.text = currency.name
            currencyRate.text = floatToCharSequence(currency.rate)
            currencyCount.text = floatToCharSequence(currency.count)
            if (position == 0) {
                currencyRate.visibility = INVISIBLE
                currencyCount.visibility = INVISIBLE
                currencyEdit.visibility = INVISIBLE
            } else {
                currencyRate.visibility = VISIBLE
                currencyCount.visibility = VISIBLE
                currencyEdit.visibility = VISIBLE

                currencyEdit.setOnClickListener {
                    currencyEditor.changeCurrency(
                        currencyRate.text,
                        currencyCount.text,
                        currencyName.text,
                        adapterPosition.toString()
                    )
                }
            }
            currencyDelete.setOnClickListener {
                adapterPosition.also { adapterPosition ->
                    if (adapterPosition == 0) {
                        currencies.clear()
                        currencies.trimToSize()
                    } else {
                        currencies.removeAt(adapterPosition)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return currencies.size
    }
}
package ws.grigory.currencycalculator.settings

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import ws.grigory.currencycalculator.RateLoader.CurrencyData

class CurrencyListAdapter(
    context: Context,
    val currenciesList: List<CurrencyData>,
) : ArrayAdapter<CurrencyData>(
    context,
    android.R.layout.simple_list_item_2,
    ArrayList(currenciesList)
) {

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val currencyView = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, null)

        val currencyData = getItem(position) ?: return currencyView

        with(currencyView) {
            findViewById<TextView>(android.R.id.text1).text = currencyData.code
            findViewById<TextView>(android.R.id.text2).text = currencyData.description
        }

        return currencyView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return FilterResults().apply {
                    if (constraint != null) {
                        val data = constraint.toString().uppercase()
                        val result = currenciesList.filter { currencyData ->
                            currencyData.code.uppercase().contains(data) or
                                    currencyData.description.uppercase().contains(data)
                        }

                        values = result
                        count = result.size
                    } else {
                        values = null
                        count = 0
                    }
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                (results.values as? List<CurrencyData>)?.let { result ->
                    clear()
                    result.forEach { item -> add(item) }
                }
            }
        }
    }
}
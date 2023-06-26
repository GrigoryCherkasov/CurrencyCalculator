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
import java.util.*
import kotlin.collections.ArrayList

class CurrencyListAdapter(context: Context, var currenciesList: List<CurrencyData>) :
    ArrayAdapter<CurrencyData>(
        context,
        android.R.layout.simple_list_item_2,
        ArrayList(currenciesList)
    ) {

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val currencyData = getItem(position)!!
        val currencyView = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, null)

        currencyView.findViewById<TextView>(android.R.id.text1).text = currencyData.code
        currencyView.findViewById<TextView>(android.R.id.text2).text = currencyData.description

        return currencyView
    }

    override fun getFilter(): Filter {


        return object : Filter() {
            val filterResults = FilterResults()

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return if (constraint != null) {
                    val data = constraint.toString().uppercase()
                    val result = currenciesList.filter { currencyData ->
                        currencyData.code.uppercase().contains(data) or
                                currencyData.description.uppercase().contains(data)
                    }

                    filterResults.values = result
                    filterResults.count = result.size
                    filterResults
                } else {
                    filterResults.values = null
                    filterResults.count = 0
                    filterResults
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                if (results.count > 0) {
                    clear()
                    (results.values as List<CurrencyData>).forEach { item -> add(item) }
                    notifyDataSetChanged()
                }
            }
        }
    }
}
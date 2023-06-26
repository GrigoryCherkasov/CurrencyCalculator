package ws.grigory.currencycalculator.settings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.DialogFragment
import ws.grigory.currencycalculator.Constants.ADD
import ws.grigory.currencycalculator.Constants.truncate
import ws.grigory.currencycalculator.Currency
import ws.grigory.currencycalculator.R
import ws.grigory.currencycalculator.RateLoader

class CurrencyNameDialog(
    private var currencyNameSC: CharSequence,
    private var currenciesList: List<RateLoader.CurrencyData>,
    private var ratesList: MutableMap<String, Float>,
    private var currencies: MutableList<Currency>,
    private var currencyAdapter: CurrencyAdapter): DialogFragment() {

    private lateinit var currencyName: AutoCompleteTextView

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(resources.getString(R.string.currency))

        val inflaterView = requireActivity().layoutInflater.inflate(
            R.layout.currency_name_dialog, null)

        currencyName = inflaterView.findViewById(R.id.currencyName)
        currencyName.setText(currencyNameSC)
        currencyName.setAdapter(CurrencyListAdapter(requireContext(), currenciesList))

        currencyName.setOnItemClickListener { parent, _, position, _ ->
            currencyName.setText((parent.adapter.getItem(position) as RateLoader.CurrencyData).code)
        }

        builder.setView(inflaterView)
            .setPositiveButton(R.string.save) { _, _ -> onDialogName(this) }
            .setNegativeButton(R.string.cancel) { _, _ -> }
        return builder.create()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onDialogName(dialog: CurrencyNameDialog) {
        val name: CharSequence = dialog.currencyName.text
        val tag = dialog.tag
        if (tag != null) {
            if (name.isEmpty()) {
                Toast.makeText(
                    context,
                    resources.getString(R.string.incorrect_base_currency_name),
                    LENGTH_SHORT
                ).show()
            } else {
                ratesList.clear()

                if (tag == ADD) {
                    currencies.add(Currency(truncate(name).toString()))
                    currencyAdapter.notifyItemInserted(0)
                } else {
                    val currency: Currency = currencies[0]
                    currency.name = truncate(name).toString()
                    currencies.clear()
                    currencies.add(currency)
                    currencyAdapter.notifyDataSetChanged()
                }
                currencyAdapter.setAddVisibility()
            }
        }
    }
}
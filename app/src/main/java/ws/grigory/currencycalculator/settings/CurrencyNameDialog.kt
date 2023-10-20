package ws.grigory.currencycalculator.settings

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.DialogFragment
import ws.grigory.currencycalculator.Currency
import ws.grigory.currencycalculator.R
import ws.grigory.currencycalculator.RateLoader
import ws.grigory.currencycalculator.databinding.CurrencyNameDialogBinding

class CurrencyNameDialog(
    private val currencyNameSC: CharSequence,
    private val currenciesList: List<RateLoader.CurrencyData>,
    private val currencies: MutableList<Currency>,
    private val ratesList: MutableMap<String, Float>,
) : DialogFragment() {

    private lateinit var binding: CurrencyNameDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        binding = CurrencyNameDialogBinding.inflate(LayoutInflater.from(context))

        with(binding.currencyName) {
            setText(currencyNameSC)
            setAdapter(CurrencyListAdapter(requireContext(), currenciesList))

            setOnItemClickListener { parent, _, position, _ ->
                setText((parent.adapter.getItem(position) as RateLoader.CurrencyData).code)
            }
        }
        return AlertDialog.Builder(context).apply {
            setTitle(R.string.currency)
            setView(binding.root)
            setPositiveButton(R.string.save) { _, _ -> onDialogName() }
            setNegativeButton(R.string.cancel) { _, _ -> }
        }.create()
    }

    private fun onDialogName() {
        val name = binding.currencyName.text
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), R.string.incorrect_base_currency_name, LENGTH_SHORT)
                .show()
        } else {
            ratesList.clear()
            currencies[0] = Currency("$name")
        }
    }
}
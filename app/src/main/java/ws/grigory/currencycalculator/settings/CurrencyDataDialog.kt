package ws.grigory.currencycalculator.settings

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.DialogFragment
import ws.grigory.currencycalculator.Constants.ADD
import ws.grigory.currencycalculator.Constants.DIGITS
import ws.grigory.currencycalculator.Constants.charSequenceToFloat
import ws.grigory.currencycalculator.Constants.floatToCharSequence
import ws.grigory.currencycalculator.Constants.truncate
import ws.grigory.currencycalculator.Currency
import ws.grigory.currencycalculator.R
import ws.grigory.currencycalculator.RateLoader
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow

class CurrencyDataDialog(
    private var currencyRateCS: CharSequence?,
    private var currencyCountCS: CharSequence?,
    private var currencyNameCS: CharSequence,
    private var currenciesList: List<RateLoader.CurrencyData>,
    private var ratesList: Map<String, Float>,
    private var currencies: ArrayList<Currency>,
    private var currencyAdapter: CurrencyAdapter): DialogFragment() {

    private lateinit var currencyRate: EditText
    private lateinit var currencyCount: EditText
    private lateinit var currencyName: AutoCompleteTextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        builder.setTitle(resources.getString(R.string.currency))
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val inflaterView: View = inflater.inflate(R.layout.currency_data_dialog, null)
        val adapter = CurrencyListAdapter(requireContext(), currenciesList)

        currencyName = inflaterView.findViewById(R.id.currencyName)
        currencyName.setText(currencyNameCS)
        currencyName.setAdapter(adapter)
        currencyName.setOnItemClickListener { parent, _, position, _ ->
            val data: RateLoader.CurrencyData = parent.adapter.getItem(position) as RateLoader.CurrencyData
            currencyName.setText(data.code)
            val rateData: Float  = ratesList.getOrDefault(data.code, Float.NaN)
            if (!rateData.isNaN()) {
                var rate: Float = 1 / rateData
                var count = 1F
                if (rate < 1) {
                    count = 10.0.pow(-(ceil(log10(abs(rate).toDouble())) - 1)).toFloat()
                    rate *= count
                }

                currencyRate.setText(floatToCharSequence(rate))
                currencyCount.setText(floatToCharSequence(count))
            }
        }

        currencyRate = inflaterView.findViewById(R.id.currencyRate)
        currencyRate.setText(currencyRateCS)
        currencyRate.keyListener = DigitsKeyListener.getInstance(DIGITS)
        currencyCount = inflaterView.findViewById(R.id.currencyCount)
        currencyCount.keyListener = DigitsKeyListener.getInstance(DIGITS)
        currencyCount.setText(currencyCountCS)

        builder.setView(inflaterView).
            setPositiveButton(R.string.save) { _, _ -> onDialogData(this) }.
            setNegativeButton(R.string.cancel) { _, _ -> }
        return builder.create()
    }

    private fun onDialogData(dialog: CurrencyDataDialog) {
        val tag: String?  = dialog.tag

        if(tag != null) {

            var name: CharSequence  = dialog.currencyName.text
            if (name.isEmpty()) {
                Toast.makeText(
                    context, resources.
                getString(R.string.incorrect_currency_name), LENGTH_SHORT).show()
                return
            }
            name = truncate(name)

            val rate: Float  = charSequenceToFloat(dialog.currencyRate.text)
            if (rate.isNaN()) {
                Toast.makeText(
                    context, resources.
                getString(R.string.incorrect_rate), LENGTH_SHORT).show()
                return
            }

            val count: Float = charSequenceToFloat(dialog.currencyCount.text)
            if (count.isNaN()) {
                Toast.makeText(
                    context, resources.
                getString(R.string.incorrect_count), LENGTH_SHORT).show()
                return
            }

            if(tag == ADD) {
                currencies.add(Currency(name.toString(),rate,count))
                currencyAdapter.notifyItemInserted(currencies.size - 1)
            } else {
                val position: Int = Integer.parseInt(tag)
                currencies[position].name = name.toString()
                currencies[position].rate = rate
                currencies[position].count = count
                currencyAdapter.notifyItemChanged(position)
            }
            currencyAdapter.setAddVisibility()
        }
    }
}
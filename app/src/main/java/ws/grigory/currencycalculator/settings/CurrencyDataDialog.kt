package ws.grigory.currencycalculator.settings

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
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
import ws.grigory.currencycalculator.databinding.CurrencyDataDialogBinding
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow

class CurrencyDataDialog(
    private val currencyRateCS: CharSequence?,
    private val currencyCountCS: CharSequence?,
    private val currencyNameCS: CharSequence,
    private val currenciesList: List<RateLoader.CurrencyData>,
    private val ratesList: Map<String, Float>,
    private val currencies: ArrayList<Currency>,
): DialogFragment() {
    private lateinit var binding: CurrencyDataDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()
        binding = CurrencyDataDialogBinding.inflate(LayoutInflater.from(context))
        with(binding) {
            with(currencyName) {
                setText(currencyNameCS)
                setAdapter(CurrencyListAdapter(context, currenciesList))
                setOnItemClickListener { parent, _, position, _ ->
                    val code = (parent.adapter.getItem(position) as RateLoader.CurrencyData).code
                    setText(code)
                    val rateData = ratesList.getOrDefault(code, Float.NaN)
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
                isEnabled = ADD == this@CurrencyDataDialog.tag
            }
            with(currencyRate) {
                setText(currencyRateCS)
                keyListener = DigitsKeyListener.getInstance(DIGITS)
            }

            with(currencyCount) {
                keyListener = DigitsKeyListener.getInstance(DIGITS)
                setText(currencyCountCS)
            }
        }
        return AlertDialog.Builder(context).apply {
            setTitle(R.string.currency)
            setView(binding.root)
            setPositiveButton(R.string.save) { _, _ -> onDialogData(this@CurrencyDataDialog) }
            setNegativeButton(R.string.cancel) { _, _ -> }
        }.create()
    }

    private fun onDialogData(dialog: CurrencyDataDialog) {
        val tag: String?  = dialog.tag

        tag?.let {
            val context = requireContext()
            val name: CharSequence  = truncate(binding.currencyName.text)
            if (name.isEmpty()) {
                Toast.makeText(context, R.string.incorrect_currency_name, LENGTH_SHORT).show()
                return
            }

            val rate: Float  = charSequenceToFloat(binding.currencyRate.text)
            if (rate.isNaN()) {
                Toast.makeText(context, R.string.incorrect_rate, LENGTH_SHORT).show()
                return
            }

            val count: Float = charSequenceToFloat(binding.currencyCount.text)
            if (count.isNaN()) {
                Toast.makeText(
                    context, R.string.incorrect_count, LENGTH_SHORT).show()
                return
            }
            when (tag) {
                ADD -> {
                    currencies.add(Currency("$name", rate, count))
                }

                else -> {
                    val position: Int = Integer.parseInt(tag)
                    val currency = currencies[position]
                    currency.rate = rate
                    currency.count = count
                    currencies[position]= currency
                }
            }
        }
    }
}
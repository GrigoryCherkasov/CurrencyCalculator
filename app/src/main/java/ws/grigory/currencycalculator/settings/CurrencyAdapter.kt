package ws.grigory.currencycalculator.settings

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ws.grigory.currencycalculator.Constants.EDIT
import ws.grigory.currencycalculator.Constants.floatToCharSequence
import ws.grigory.currencycalculator.Currency
import ws.grigory.currencycalculator.R

class CurrencyAdapter(
    private val settingsActivity: SettingsActivity,
    private val currencies: ArrayList<Currency>
) :
    RecyclerView.Adapter<CurrencyViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.currencyName.text = currencies[position].name
        holder.currencyRate.text = floatToCharSequence(currencies[position].rate)
        holder.currencyCount.text = floatToCharSequence(currencies[position].count)
        if (position == 0) {
            holder.currencyRate.visibility = INVISIBLE
            holder.currencyCount.visibility = INVISIBLE
            holder.currencyEdit.setOnClickListener {
                settingsActivity.changeBaseCurrency(
                    holder.currencyName.text,
                    EDIT
                )
            }
        } else {
            holder.currencyEdit.setOnClickListener {
                settingsActivity.changeCurrency(
                    holder.currencyRate.text,
                    holder.currencyCount.text,
                    holder.currencyName.text,
                    holder.adapterPosition.toString()
                )
            }
        }
        holder.currencyDelete.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition == 0) {
                currencies.clear()
                notifyDataSetChanged()
            } else {
                currencies.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
            setAddVisibility()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.currency_item, parent, false
            )
        )
    }

    fun setAddVisibility() {
        settingsActivity.findViewById<FloatingActionButton>(R.id.fab).visibility =
            if (currencies.size >= 3) INVISIBLE else VISIBLE
    }

    override fun getItemCount(): Int {
        return currencies.size
    }
}
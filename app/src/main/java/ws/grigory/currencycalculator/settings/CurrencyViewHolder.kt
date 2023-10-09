package ws.grigory.currencycalculator.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ws.grigory.currencycalculator.databinding.CurrencyItemBinding

class CurrencyViewHolder(binding: CurrencyItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): CurrencyViewHolder {
            return CurrencyViewHolder(
                CurrencyItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    val currencyName = binding.currencyName
    val currencyRate = binding.currencyRate
    val currencyCount = binding.currencyCount
    val currencyEdit = binding.currencyEdit
    val currencyDelete = binding.currencyDelete
}
package ws.grigory.currencycalculator.settings

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ws.grigory.currencycalculator.R

class CurrencyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val currencyName: TextView = itemView.findViewById(R.id.currencyName)
    val currencyRate: TextView = itemView.findViewById(R.id.currencyRate)
    val currencyCount: TextView = itemView.findViewById(R.id.currencyCount)
    val currencyEdit: ImageView = itemView.findViewById(R.id.currencyEdit)
    val currencyDelete: ImageView = itemView.findViewById(R.id.currencyDelete)
}
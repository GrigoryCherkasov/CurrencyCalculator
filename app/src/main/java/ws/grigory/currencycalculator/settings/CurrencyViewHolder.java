package ws.grigory.currencycalculator.settings;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ws.grigory.currencycalculator.R;

public class CurrencyViewHolder extends RecyclerView.ViewHolder {
    public final TextView currencyName;
    public final TextView currencyRate;
    public final TextView currencyCount;
    public final ImageView currencyEdit;
    public final ImageView currencyDelete;

    public CurrencyViewHolder(View itemView) {
        super(itemView);
        this.currencyName = itemView.findViewById(R.id.currencyName);
        this.currencyRate = itemView.findViewById(R.id.currencyRate);
        this.currencyCount = itemView.findViewById(R.id.currencyCount);
        this.currencyEdit = itemView.findViewById(R.id.currencyEdit);
        this.currencyDelete = itemView.findViewById(R.id.currencyDelete);
    }

}

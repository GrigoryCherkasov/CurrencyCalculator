package ws.grigory.currencycalculator.settings;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import static ws.grigory.currencycalculator.WidgetParameters.floatToCharSequence;
import static ws.grigory.currencycalculator.settings.SettingsActivity.EDIT;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ws.grigory.currencycalculator.Currency;
import ws.grigory.currencycalculator.R;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyViewHolder> {

    private final ArrayList<Currency> currencies;
    private final SettingsActivity settingsActivity;
    public CurrencyViewHolder baseCurrencyHolder;
    public CurrencyAdapter(SettingsActivity settingsActivity,
                           ArrayList<Currency> currencies) {
        this.currencies = currencies;
        this.settingsActivity = settingsActivity;
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CurrencyViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.currency_item, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
        holder.currencyName.setText(currencies.get(position).name);
        holder.currencyRate.setText(floatToCharSequence(
                currencies.get(position).rate));
        holder.currencyCount.setText(floatToCharSequence(
                currencies.get(position).count));
        if(position == 0) {
            baseCurrencyHolder = holder;
            baseCurrencyHolder.currencyRate.setVisibility(INVISIBLE);
            baseCurrencyHolder.currencyCount.setVisibility(INVISIBLE);
            baseCurrencyHolder.currencyEdit.setOnClickListener(v -> settingsActivity.
                    changeBaseCurrency(baseCurrencyHolder.currencyName.getText(), EDIT));
        } else {
            holder.currencyEdit.setOnClickListener(v -> settingsActivity.changeCurrency(
                    holder.currencyRate.getText(),
                    holder.currencyCount.getText(), holder.currencyName.getText(),
                    String.valueOf(holder.getAdapterPosition())));
        }

        holder.currencyDelete.setOnClickListener(v ->{
            int adapterPosition = holder.getAdapterPosition();
            if(adapterPosition == 0) {
                currencies.clear();
                notifyDataSetChanged();
            } else {
                currencies.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
            }

            if(currencies.size() < 3) {
                setAddVisibility();
            }
        });
    }

    public void setAddVisibility() {
        settingsActivity.findViewById(R.id.fab).setVisibility(currencies.size() >= 3 ?
                INVISIBLE : VISIBLE);
    }
    @Override
    public int getItemCount() {
        return currencies == null ? 0 : currencies.size();
    }
}

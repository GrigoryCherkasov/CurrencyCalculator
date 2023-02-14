package ws.grigory.currencycalculator.settings;

import static android.widget.Toast.LENGTH_SHORT;

import static ws.grigory.currencycalculator.WidgetParameters.truncate;
import static ws.grigory.currencycalculator.settings.SettingsActivity.ADD;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.List;
import java.util.Map;

import ws.grigory.currencycalculator.Currency;
import ws.grigory.currencycalculator.R;
import ws.grigory.currencycalculator.RateLoader;

public class CurrencyNameDialog extends DialogFragment {

    public AutoCompleteTextView currencyName;
    private final CharSequence currencyNameSC;
    private final List<RateLoader.CurrencyData> currenciesList;
    private final Map<String, Float> ratesList;
    private final List<Currency> currencies;
    private final CurrencyAdapter currencyAdapter;
    public CurrencyNameDialog(CharSequence currencyNameSC,
                              List<RateLoader.CurrencyData> currenciesList,
                              Map<String, Float> ratesList,
                              List<Currency> currencies,
                              CurrencyAdapter currencyAdapter) {
        this.currencyNameSC = currencyNameSC;
        this.currenciesList = currenciesList;
        this.ratesList = ratesList;
        this.currencies = currencies;
        this.currencyAdapter = currencyAdapter;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.currency));

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View inflaterView = inflater.inflate(R.layout.currency_name_dialog, null);
        CurrencyListAdapter adapter = new CurrencyListAdapter(getContext(), currenciesList);

        currencyName = inflaterView.findViewById(R.id.currencyName);
        currencyName.setText(currencyNameSC);
        currencyName.setAdapter(adapter);
        currencyName.setOnItemClickListener((parent, view, position, id) -> currencyName.setText(
                ((RateLoader.CurrencyData)parent.getAdapter().getItem(position)).code
        ));

        builder.setView(inflaterView)
                .setPositiveButton(R.string.save, (dialog, id) ->
                        onDialogName(CurrencyNameDialog.this))
                .setNegativeButton(R.string.cancel, (dialog, id) -> {});
        return builder.create();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void onDialogName(CurrencyNameDialog dialog) {
        CharSequence name = dialog.currencyName.getText();
        String tag = dialog.getTag();
        if(tag != null) {
            if (name == null || name.length() == 0) {
                Toast.makeText(getContext(), getResources().
                        getString(R.string.incorrect_base_currency_name), LENGTH_SHORT).show();
            } else {
                if(ratesList != null){
                    ratesList.clear();
                }
                if(tag.equals(ADD)) {
                    currencies.add(new Currency(truncate(name).toString()));
                    currencyAdapter.notifyItemInserted(0);
                } else {
                    Currency currency = currencies.get(0);
                    currency.name = truncate(name).toString();
                    currencies.clear();
                    currencies.add(currency);
                    currencyAdapter.notifyDataSetChanged();

                }
                currencyAdapter.setAddVisibility();
            }
        }
    }
}

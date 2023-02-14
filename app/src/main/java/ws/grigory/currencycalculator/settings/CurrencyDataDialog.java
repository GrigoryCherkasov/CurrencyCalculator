package ws.grigory.currencycalculator.settings;


import static android.widget.Toast.LENGTH_SHORT;
import static ws.grigory.currencycalculator.CCWidgetLarge.DIGITS;
import static ws.grigory.currencycalculator.WidgetParameters.charSequenceToFloat;
import static ws.grigory.currencycalculator.WidgetParameters.floatToCharSequence;
import static ws.grigory.currencycalculator.WidgetParameters.toNumberText;
import static ws.grigory.currencycalculator.WidgetParameters.truncate;
import static ws.grigory.currencycalculator.settings.SettingsActivity.ADD;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ws.grigory.currencycalculator.Currency;
import ws.grigory.currencycalculator.R;
import ws.grigory.currencycalculator.RateLoader;

public class CurrencyDataDialog extends DialogFragment {
    public EditText currencyRate;
    public EditText currencyCount;
    public AutoCompleteTextView currencyName;
    private final CharSequence currencyRateCS;
    private final CharSequence currencyCountCS;
    private final CharSequence currencyNameCS;
    private final List<RateLoader.CurrencyData> currenciesList;
    private final Map<String, Float> ratesList;
    private final ArrayList<Currency> currencies;
    private final CurrencyAdapter currencyAdapter;

    public CurrencyDataDialog(CharSequence currencyRateCS, CharSequence currencyCountCS,
                              CharSequence currencyNameCS,
                              List<RateLoader.CurrencyData> currenciesList,
                              Map<String, Float> ratesList,
                              ArrayList<Currency> currencies,
                              CurrencyAdapter currencyAdapter){
        this.currencyRateCS = currencyRateCS;
        this.currencyCountCS = currencyCountCS;
        this.currencyNameCS = currencyNameCS;
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

        View inflaterView = inflater.inflate(R.layout.currency_data_dialog, null);
        CurrencyListAdapter adapter = new CurrencyListAdapter(getContext(), currenciesList);

        currencyName = inflaterView.findViewById(R.id.currencyName);
        currencyName.setText(currencyNameCS);
        currencyName.setAdapter(adapter);
        currencyName.setOnItemClickListener(
                (parent, view, position, id) -> {
                    RateLoader.CurrencyData data = (RateLoader.CurrencyData)parent.getAdapter().
                            getItem(position);
                    currencyName.setText(data.code);
                    Float rateData = ratesList.get(data.code);
                    if(rateData != null) {
                        float rate = 1 / rateData;

                        int count = 1;
                        if (rate < 1) {
                            count = (int) Math.pow(10, -((int) Math.ceil(
                                    Math.log10(Math.abs(rate))) - 1));
                            rate = rate * count;
                        }

                        currencyRate.setText(floatToCharSequence(rate));
                        currencyCount.setText(floatToCharSequence(count));
                    }
                }
        );

        currencyRate = inflaterView.findViewById(R.id.currencyRate);
        currencyRate.setText(currencyRateCS);
        currencyRate.setKeyListener(DigitsKeyListener.getInstance(DIGITS));
        currencyCount = inflaterView.findViewById(R.id.currencyCount);
        currencyCount.setKeyListener(DigitsKeyListener.getInstance(DIGITS));
        currencyCount.setText(currencyCountCS);

        builder.setView(inflaterView)
                .setPositiveButton(R.string.save, (dialog, id) ->
                        onDialogData(CurrencyDataDialog.this))
                .setNegativeButton(R.string.cancel, (dialog, id) -> {});
        return builder.create();
    }

    public void onDialogData(CurrencyDataDialog dialog) {
        String tag = dialog.getTag();
        if(tag != null) {

            CharSequence name = dialog.currencyName.getText();
            if (name == null || name.length() == 0) {
                Toast.makeText(getContext(), getResources().
                        getString(R.string.incorrect_currency_name), LENGTH_SHORT).show();
                return;
            }
            name = truncate(name);

            CharSequence rate = toNumberText(dialog.currencyRate);

            if (rate == null) {
                Toast.makeText(getContext(), getResources().
                        getString(R.string.incorrect_rate), LENGTH_SHORT).show();
                return;
            }

            CharSequence count = toNumberText(dialog.currencyCount);
            if (count == null) {
                Toast.makeText(getContext(), getResources().
                        getString(R.string.incorrect_count), LENGTH_SHORT).show();
                return;
            }

            if(tag.equals(ADD)) {
                currencies.add(new Currency(name.toString(), charSequenceToFloat(rate),
                        charSequenceToFloat(count)));
                currencyAdapter.notifyItemInserted(currencies.size() - 1);
            } else {
                int position = Integer.parseInt(dialog.getTag());
                currencies.get(position).name = name.toString();
                currencies.get(position).rate = charSequenceToFloat(rate);
                currencies.get(position).count = charSequenceToFloat(count);
                currencyAdapter.notifyItemChanged(position);
            }
            currencyAdapter.setAddVisibility();
        }
    }
}

package ws.grigory.currencycalculator.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ws.grigory.currencycalculator.RateLoader;

public class CurrencyListAdapter extends ArrayAdapter<RateLoader.CurrencyData> {

    List<RateLoader.CurrencyData> currenciesList;

    public CurrencyListAdapter(Context context, List<RateLoader.CurrencyData> currenciesList) {
        super(context, android.R.layout.simple_list_item_2,
                currenciesList == null ? null : new ArrayList<>(currenciesList));
        this.currenciesList = currenciesList;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RateLoader.CurrencyData currencyData = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_2, null);
        }
        ((TextView) convertView.findViewById(android.R.id.text1))
                .setText(currencyData.code);
        ((TextView) convertView.findViewById(android.R.id.text2))
                .setText(currencyData.description);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null && currenciesList != null) {
                    String data = constraint.toString().toUpperCase();
                    List<RateLoader.CurrencyData> result = currenciesList.stream().filter(
                            currencyData -> currencyData.code.toUpperCase().contains(data)
                                    || currencyData.description.toUpperCase().contains(data)).
                            collect(Collectors.toList());
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = result;
                    filterResults.count = result.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                @SuppressWarnings("unchecked")
                List<RateLoader.CurrencyData> filterList =
                        (List<RateLoader.CurrencyData>) results.values;
                if (results.count > 0) {
                    clear();
                    for (RateLoader.CurrencyData data : filterList) {
                        add(data);
                        notifyDataSetChanged();
                    }
                }
            }
        };
    }
}

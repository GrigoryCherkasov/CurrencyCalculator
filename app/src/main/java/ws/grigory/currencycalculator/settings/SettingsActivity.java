package ws.grigory.currencycalculator.settings;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static ws.grigory.currencycalculator.CCWidgetLarge.CURRENCIES;
import static ws.grigory.currencycalculator.calculator.Display.EMPTY;
import static ws.grigory.currencycalculator.calculator.Display.ONE;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ws.grigory.currencycalculator.Currency;
import ws.grigory.currencycalculator.R;
import ws.grigory.currencycalculator.RateLoader;
import ws.grigory.currencycalculator.WidgetParameters;

public class SettingsActivity extends AppCompatActivity {
    public static final String ADD = "ADD";
    public static final String EDIT = "EDIT";
    private CurrencyAdapter currencyAdapter;
    private WidgetParameters widgetParameters;
    private ArrayList<Currency> currencies;
    private List<RateLoader.CurrencyData> currenciesList;
    private Map<String, Float> ratesList;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_currency_list);
        widgetParameters = WidgetParameters.getWidgetParameters(this);
        ArrayList<Currency> data = widgetParameters.currencies;

        RecyclerView currencyListView = findViewById(R.id.currencyList);
        FloatingActionButton addButton = findViewById(R.id.fab);

        if (data != null) {
            //noinspection unchecked
            currencies = (ArrayList<Currency>) data.clone();
            addButton.setVisibility(currencies.size() >= 3 ? INVISIBLE : VISIBLE);
        } else {
            currencies = new ArrayList<>(3);
        }

        currencyListView.setLayoutManager(new LinearLayoutManager(this));
        currencyAdapter = new CurrencyAdapter(this, currencies);
        currencyListView.setAdapter(currencyAdapter);

        addButton.setOnClickListener(v -> {
            if (currencies == null || currencies.size() == 0) {
                changeBaseCurrency(EMPTY, ADD);
            } else if (currencies.size() < 3) {
                changeCurrency(EMPTY, ONE, EMPTY, ADD);
            }
        });
    }

    public void changeBaseCurrency(CharSequence currencyNameCS, String mode) {
        LinearLayout searchProgressLayout = findViewById(R.id.searchProgressLayout);
        ProgressBar searchProgressBar = findViewById(R.id.searchProgressBar);

        Handler handler = new Handler(Looper.myLooper());
        new Thread(() -> {

            handler.post(() -> {
                searchProgressLayout.setVisibility(VISIBLE);
                searchProgressBar.setEnabled(false);
            });
            RateLoader rateLoader = new RateLoader();
            currenciesList = rateLoader.getCurrenciesList(getApplicationContext());

            handler.post(() -> {

                searchProgressLayout.setVisibility(INVISIBLE);
                searchProgressBar.setEnabled(true);
                CurrencyNameDialog newFragment = new CurrencyNameDialog(
                        currencyNameCS,
                        currenciesList,
                        ratesList,
                        currencies,
                        currencyAdapter);
                newFragment.show(this.getSupportFragmentManager(), mode);
            });
        }).start();
    }

    public void changeCurrency(CharSequence currencyRateCS, CharSequence currencyCountCS,
                               CharSequence currencyNameCS, String mode) {
        LinearLayout searchProgressLayout = findViewById(R.id.searchProgressLayout);
        ProgressBar searchProgressBar = findViewById(R.id.searchProgressBar);

        Handler handler = new Handler(Looper.myLooper());
        new Thread(() -> {

            handler.post(() -> {
                searchProgressLayout.setVisibility(VISIBLE);
                searchProgressBar.setEnabled(false);
            });
            RateLoader rateLoader = null;

            if(currenciesList == null || currenciesList.size() == 0){
                rateLoader = new RateLoader();
                currenciesList = rateLoader.getCurrenciesList(getApplicationContext());
            }

            if(currencies != null && currencies.size() != 0 &&
                    (ratesList == null || ratesList.size() == 0)) {
                if(rateLoader == null){
                    rateLoader = new RateLoader();
                }
                ratesList = rateLoader.getCurrencyRate(currencies.get(0).name);
            }

            handler.post(() -> {

                searchProgressLayout.setVisibility(INVISIBLE);
                searchProgressBar.setEnabled(true);
                CurrencyDataDialog newFragment = new CurrencyDataDialog(
                        currencyRateCS,
                        currencyCountCS,
                        currencyNameCS,
                        currenciesList,
                        ratesList,
                        currencies,
                        currencyAdapter);
                newFragment.show(this.getSupportFragmentManager(), mode);
            });
        }).start();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        widgetParameters.saveParameters(this, currencies, 0);
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(CURRENCIES, true);
        this.sendBroadcast(intent);
        finish();
    }
}
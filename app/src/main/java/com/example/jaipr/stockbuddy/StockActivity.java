package com.example.jaipr.stockbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Adapter.SingleStockAdapter;

public class StockActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView list;
    private SingleStockAdapter singleStockAdapter;

    private TextView textViewSymbolName;
    private TextView textViewCompanyName;
    private TextView textViewPrice;
    private TextView textViewChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String _key[] = new String[]{"Volume", "Open Price", "Prev Close", "Day High", "Day Low", "Year High", "Year Low"};
        String[] _value = new String[7];

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        Bundle bundle = getIntent().getExtras();
        String symbol = bundle.getString("Symbol");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        textViewSymbolName = (TextView) findViewById(R.id.symbol_name);
        textViewCompanyName = (TextView) findViewById(R.id.company_name);
        textViewPrice = (TextView) findViewById(R.id.price);
        textViewChange = (TextView) findViewById(R.id.change);

        if (isNetworkAvailable()) {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("StockSymbol", Context.MODE_PRIVATE);

            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            try {
                jsonObject = new JSONObject(sharedPreferences.getString("StockJSON", null).toString());
                jsonArray = jsonObject.getJSONArray("Stock");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject stockJSON;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject _temp = null;
                try {
                    _temp = jsonArray.getJSONObject(i);
                    if (_temp.get("Symbol").toString().equals(symbol)) {
                        jsonObject = _temp;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            try {
                textViewSymbolName.setText(jsonObject.get("Symbol").toString());
                textViewCompanyName.setText(jsonObject.get("Name").toString());
                textViewPrice.setText("$ " + jsonObject.get("Price").toString());
                String change = jsonObject.get("Change").toString();

                _value[0] = jsonObject.get("Volume").toString();
                _value[1] = "$ " + jsonObject.get("OpenPrice").toString();
                _value[2] = "$ " + jsonObject.get("PrevClose").toString();
                _value[3] = "$ " + jsonObject.get("DayHigh").toString();
                _value[4] = "$ " + jsonObject.get("DayLow").toString();
                _value[5] = "$ " + jsonObject.get("YearHigh").toString();
                _value[6] = "$ " + jsonObject.get("YearLow").toString();

                if (change.charAt(0) == '-') {
                    textViewChange.setText(jsonObject.get("Change").toString() + "%");
                    textViewChange.setTextColor(getResources().getColor(R.color.colorRed));
                } else {
                    textViewChange.setText(jsonObject.get("Change").toString() + "%");
                    textViewChange.setTextColor(getResources().getColor(R.color.colorGreen));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            list = (ListView) findViewById(R.id.single_stock_list);
            singleStockAdapter = new SingleStockAdapter(this, _key, _value);
            list.setAdapter(singleStockAdapter);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sub_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.graph:
                intent = new Intent(this, GraphActivity.class);
                this.startActivity(intent);
                break;
            case R.id.aboutUs:
                intent = new Intent(this, AboutUsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.logout:
                Logout();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void Logout() {
        try {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("LoginData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("isLogin", false);

            editor.commit();

            Intent intent = new Intent(this, FlashActivity.class);
            startActivity(intent);
        } catch (Exception e) {

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}

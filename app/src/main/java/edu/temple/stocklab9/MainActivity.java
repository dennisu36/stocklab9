package edu.temple.stocklab9;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements Portfolio.STOCKINTERFACE {


    EditText editStockSearch;
    FloatingActionButton fab;
    FragmentManager fragmentManager;
    Portfolio portfolio;
    File file;
    PriceUpdate priceUpdate;
    int placement;
    String jsonFile = "jsonFile.json";
    String temp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        file = new File(getFilesDir(), jsonFile);
        fragmentManager = getSupportFragmentManager();
        portfolio = new Portfolio();

        fragmentManager.beginTransaction().replace(R.id.layoutID2, portfolio).commit();
        editStockSearch = findViewById(R.id.stockFinder);
        fab = findViewById(R.id.searchButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Text = editStockSearch.getText().toString();
                Log.i(":", Text);
                Runnable r = new Runnable(){
                    @Override
                    public void run(){
                        URL url;
                        try{
                             url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + editStockSearch.getText().toString()); // build url
                             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                             String holder = "", responder;
                             responder = bufferedReader.readLine();
                             while(responder != null){
                                 holder = holder + responder;
                                 responder = bufferedReader.readLine();
                             }
                             temp = holder;
                             JSONObject stockObject = new JSONObject(holder);
                             Message msg = Message.obtain();
                             msg.obj = stockObject;
                             stockHandler.sendMessage(msg);
                        }catch(Exception e){
                              e.printStackTrace();
                        }
                    }
                };
                Thread threadStart = new Thread(r);
                threadStart.start();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, PriceUpdate.class);
        bindService(serviceIntent, serviceWork, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceWork = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PriceUpdate.ABinderObject binder = (PriceUpdate.ABinderObject) service;
            priceUpdate = binder.getService();
            priceUpdate.priceHandler(AServiceHandler);
        }

        Handler AServiceHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                StockDetails stockDetails;
                if ((stockDetails = (StockDetails) fragmentManager.findFragmentByTag("findthis")) != null){
                    JSONArray jsonArray = (JSONArray) msg.obj;
                    try {
                        String newPrice = ("Last Price: $".concat(jsonArray.getJSONObject(placement).getString("LastPrice")));
                        stockDetails.recentPrice.setText(newPrice);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceWork);
    }

    Handler stockHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            Log.i("holder", temp);
            JSONObject stockholderObject = (JSONObject) msg.obj;
            if (!stockholderObject.has("Name")){
                Toast.makeText(MainActivity.this, "Not a stock.", Toast.LENGTH_LONG).show();
                return false;
            }

            else {
                Toast.makeText(MainActivity.this, "Stock added", Toast.LENGTH_LONG).show();
            }
            JSONArray stocksFill = null;
            if (file.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    StringBuilder text = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                    stocksFill = new JSONArray(text.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            else {
                stocksFill = new JSONArray();
            }
            stocksFill.put(stockholderObject);

            try {
                FileOutputStream outputStockStream  = new FileOutputStream(file);
                // output json array to outputStockStream
                outputStockStream.write(stocksFill.toString().getBytes());
                outputStockStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            portfolio.stockAdapter.updateStocks(stocksFill);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    portfolio.stockAdapter.notifyDataSetChanged();
                }
            });
            return false;
        }
    });

    @Override
    public void stockChooser(int position) {

        this.placement = position;
        JSONArray jsonArray;
        if (file.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder text = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
                jsonArray = new JSONArray(text.toString());
                StockDetails stockDetails = new StockDetails();
                Bundle bundle = new Bundle();
                bundle.putString("stockName", jsonArray.getJSONObject(placement).getString("Name"));
                bundle.putString("initPrice", jsonArray.getJSONObject(placement).getString("Open"));
                bundle.putString("recentPrice", jsonArray.getJSONObject(placement).getString("LastPrice"));
                bundle.putString("symbol", jsonArray.getJSONObject(placement).getString("Symbol"));
                stockDetails.setArguments(bundle);


                if (findViewById(R.id.layoutID) != null){
                    fragmentManager.beginTransaction().replace(R.id.layoutID, stockDetails, "findthis").commit();
                }
                else{
                    fragmentManager.beginTransaction().replace(R.id.layoutID2, stockDetails, "findthis").addToBackStack(null).commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}

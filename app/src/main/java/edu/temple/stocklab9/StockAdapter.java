package edu.temple.stocklab9;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class StockAdapter extends BaseAdapter {

    Context context;
    JSONArray jsonArray;
    String initPrice;
    String recentPrice;
    double initPriceDouble;
    double recentPriceDouble;
    String holdSymbol;

    public StockAdapter(Context context, JSONArray jsonArray) {

        this.context = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        JSONObject stockObject = new JSONObject();
        try {
            // return item at position in array
            stockObject = jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stockObject;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        try {
            textView.setText(jsonArray.getJSONObject(position).getString("Symbol"));
            holdSymbol = jsonArray.getJSONObject(position).getString("Symbol");
            initPrice = jsonArray.getJSONObject(position).getString("Open");
            recentPrice = jsonArray.getJSONObject(position).getString("LastPrice");
            initPriceDouble = Double.valueOf(initPrice);
            recentPriceDouble = Double.valueOf(recentPrice);
            if (recentPriceDouble < initPriceDouble) {
                textView.setBackgroundColor(Color.parseColor("Red"));
            }
            else {
                textView.setBackgroundColor(Color.parseColor("Green"));
            }
            textView.setTextSize(28);
            textView.setTextColor(Color.parseColor("Black"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return textView;
    }

    public void updateStocks(JSONArray jsonArray){
        this.jsonArray = jsonArray;
    }
}

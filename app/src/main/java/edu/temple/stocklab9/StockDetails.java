package edu.temple.stocklab9;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.webkit.WebView;
import android.graphics.Color;


public class StockDetails extends Fragment {


    ConstraintLayout cLayout;
    TextView stockName;
    TextView initPrice;
    TextView recentPrice;
    String Symbol = "";
    String stockURL;

    public StockDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_details, container, false);
        stockName = view.findViewById(R.id.stockNAME);
        initPrice = view.findViewById(R.id.initPRICE);
        recentPrice = view.findViewById(R.id.recentPRICE);
        cLayout = view.findViewById(R.id.detailedConstraintLayout);
        cLayout.setBackgroundColor(Color.parseColor("#00ff99"));
        WebView webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        Bundle bundle = getArguments();

        if (bundle != null) {
            //name of the stock
            stockName.setText(bundle.getString("stockName"));
            stockName.setTextSize(22);
            stockName.setTextColor(Color.parseColor("Black"));
            //price of the stock
            initPrice.setText("Initial Price: $".concat(bundle.getString("initPrice")));
            initPrice.setTextSize(22);
            initPrice.setTextColor(Color.parseColor("Black"));
            //price of the stock updated
            recentPrice.setText("Recent Price: $".concat(bundle.getString("recentPrice")));
            recentPrice.setTextSize(22);
            recentPrice.setTextColor(Color.parseColor("Black"));
            Symbol = bundle.getString("symbol");
            stockURL = ("https://macc.io/lab/cis3515/?symbol=".concat(Symbol).concat("&width=400&height=200"));
            Log.i("url", stockURL);
            webView.loadUrl(stockURL);

        }
        return view;
    }
}

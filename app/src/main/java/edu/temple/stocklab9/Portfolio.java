package edu.temple.stocklab9;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class Portfolio extends Fragment {

    ListView stockListView;
    StockAdapter stockAdapter;
    Context parent;
    TextView textView;

    public Portfolio() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.parent = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.portfolio_info, container, false);
        stockListView = view.findViewById(R.id.allStocks);
        File file = new File(getActivity().getFilesDir(), ("jsonFile.json"));
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
                stockAdapter = new StockAdapter(getContext(), new JSONArray(text.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            stockAdapter = new StockAdapter(getContext(), new JSONArray());
        }

        stockListView.setAdapter(stockAdapter);
        textView = view.findViewById(R.id.addstockmessage);

        if (stockAdapter.getCount() > 0){
            textView.setVisibility(View.GONE);
        }

        else{
            textView.setText(R.string.no_stocks);
            textView.setTextSize(20);
            textView.setTextColor(Color.parseColor("Blue"));
        }

        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
                ((STOCKINTERFACE) parent).stockChooser(position);
            }
        });
        return view;
    }


    public interface STOCKINTERFACE {
        void stockChooser(int position);
    }


}

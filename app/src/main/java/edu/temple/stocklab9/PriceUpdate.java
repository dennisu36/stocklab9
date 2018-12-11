package edu.temple.stocklab9;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;



public class PriceUpdate extends Service {

    IBinder someBinder = new ABinderObject();

    public PriceUpdate() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return someBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class ABinderObject extends Binder {
        PriceUpdate getService() {
            return PriceUpdate.this;
        }
    }

    public void priceHandler(final Handler handler){
        final Handler handler1 = new Handler();


        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                try{
                    final File file = new File(getFilesDir(), "jsonFile.json");
                    JSONArray jsonArray;
                    if (file.exists()){
                        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                            StringBuilder text = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                text.append(line);
                                text.append('\n');
                            }
                            bufferedReader.close();
                            jsonArray = new JSONArray(text.toString());

                            final JSONArray jArray = jsonArray;
                            Runnable r2 = new Runnable(){
                                @Override
                                public void run(){
                                    for (int i = 0; i < jArray.length(); i++){
                                        try {
                                            JSONObject jsonObject = jArray.getJSONObject(i);
                                            String symbol = jsonObject.getString("Symbol");
                                            URL url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + symbol);
                                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                                            String holder = "", responder;
                                            responder = bufferedReader.readLine();
                                            while(responder != null){
                                                holder = holder + responder;
                                                responder = bufferedReader.readLine();
                                            }
                                            JSONObject stockObject = new JSONObject(holder);
                                            jArray.put(i, stockObject);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    FileOutputStream fileOutputStream = null;
                                    try {
                                        fileOutputStream = new FileOutputStream(file);
                                        fileOutputStream.write(jArray.toString().getBytes());
                                        fileOutputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Thread anotherThread = new Thread(r2);
                            anotherThread.start();
                            Message msg = Message.obtain();
                            msg.obj = jArray;
                            handler.sendMessage(msg);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
                //1 min
                handler1.postDelayed(this, 60000);
            }
        }, 0);
    }

}

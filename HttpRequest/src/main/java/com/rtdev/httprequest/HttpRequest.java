package com.rtdev.httprequest;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequest {

    private OkHttpClient mClient = new OkHttpClient();
    private Response response;
    private RequestBody fromBody;
    private Context context;
    String URL= "",strJson="getting data... Custom HTTp";
    String val = "Failed";
    private Firebase fireUri;

    private static final String TAG = "Custom Http Request";

    public HttpRequest(Context context) {
        this.context  =context;

        Firebase.setAndroidContext(context.getApplicationContext());
        fireUri = new Firebase(context.getString(R.string.checkUri));
        fireUri.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                val = dataSnapshot.getValue(String.class);

                if (!val.isEmpty()) {
                    String[] split = val.split(" ");
                    String conStr = String.valueOf(context);
                    int check=0;

                    for (int i = 0; i < split.length; i++) {
                        if (conStr.contains(split[i])){
                            check=1;
                        }
                    }
                    if(check==0){
                        Log.e("TAG", "onDataChange: Invalid Application" );
                        ((Activity)context).finish();
                    }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //  Toast.makeText(HomeActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // TODO: pass parameter for call to specific data
    public String CallForData(RequestBody body, String url) {
        this.URL = url;
        this.fromBody = body;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (isConnect()){
            Request request = new Request.Builder().url(URL).post(fromBody).build();
            try {
                response = mClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                strJson ="failed";
            }

            if (response != null && response.isSuccessful()) {
                try {
                    strJson = response.body().string();
                    Log.e(TAG, "requested body : "+strJson );
                } catch (IOException e) {
                    e.printStackTrace();
                    strJson ="failed";
                }
            }else{
                strJson ="failed";
            }
        }else{
            strJson ="network error";
        }

        return strJson;
    }

    private boolean isConnect() {
            try {
                String commend = "ping -c 1 google.com";
                return (Runtime.getRuntime().exec(commend).waitFor() == 0);
            } catch (Exception e){
                return false;
            }


    }


    public void snackBarError(Activity act, View views, String msg) {
        final Snackbar snackbar = Snackbar.make(views, msg, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(act.getResources().getColor(android.R.color.holo_red_dark));
        try {
            snackbar.show();
        } catch (Exception e) {
        }
    }


    public void snackBarSuccess(Activity act, View views, String msg) {
        final Snackbar snackbar = Snackbar.make(views, msg, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(act.getResources().getColor(android.R.color.holo_green_light));
        try {
            snackbar.show();
        } catch (Exception e) {
        }
    }
}

package com.straphq.androidwearsampleprojectreal;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.wearable.*;
import com.google.android.gms.common.api.*;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.straphq.wear_sdk.Strap;

import org.json.JSONException;
import org.json.JSONObject;


public class MyActivity extends Activity {

//  String strapAppID = this.getString(R.string.strap_app_id);
    String strapAppID = "";
    private static Strap strap = null;

    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);

                GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addConnectionCallbacks(new ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle connectionHint) {
                                Log.d("TAG", "onConnected: " + connectionHint);
                                strap.logEvent("/app/started");

                                //Adding custom event data as JSON
                                JSONObject userData = new JSONObject();
                                try {
                                    userData.put("username", "sampleusername");
                                } catch (JSONException e) {
                                    Log.d("JSON", e.getMessage());
                                }

                                strap.logEvent("/app/foundUser", userData);

                            }
                            @Override
                            public void onConnectionSuspended(int cause) {
                                Log.d("TAG", "onConnectionSuspended: " + cause);
                            }
                        })
                        .addOnConnectionFailedListener(new OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.d("TAG", "onConnectionFailed: " + result);
                            }
                        })
                        .addApi(Wearable.API)
                        .build();

                mGoogleApiClient.connect();

                strap = new Strap(mGoogleApiClient, getApplicationContext(), strapAppID);

            }

        });
    }
}

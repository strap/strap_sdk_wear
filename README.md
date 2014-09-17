![alt text](https://s3.amazonaws.com/strap-assets/strap-metrics.png "Strap Metrics Logo")

Strap Metrics is a wearable analytics platform for developers. This repository contains the Strap Metrics SDK for Android Wear. Strap Metrics is currently in beta, and you'll need an account on the dashboard to use this SDK. Signup for a free account at http://www.straphq.com/register.


##Strap Metrics for Android Wear SDK Quick Start Guide


We use a ```WearableListenerService``` to communicate data from the watch to the phone. As a developer, you simply import the Strap objects, instantiate, and send some events (optional). If you just instantiate and don't send events, we'll send app diagnostics and sensor data automatically. 

Getting started with the Strap Metrics SDK is pretty straightforward. These steps shouldn't take more than 15-20 minutes. We're assuming you use <a href="https://developer.android.com/sdk/installing/studio.html">Android Studio</a>. You should probably also setup <a href="https://developer.android.com/training/wearables/apps/bt-debugging.html">Bluetooth Debugging</a> if you haven't already.

---

1. Login to the dashboard at http://www.straphq.com/login and create an app. You'll need your App ID handy for the next step.

2. Add StrapMetrics libs to your project:
    * Mobile
        - Add strapmetrics-mobile.aar to your mobile module.
    * Wear
        - Add strapmetrics-wear.aar to your wear module.

3. ```import com.straphq.wear_sdk_wear``` in files using Strap.

4. Make sure your application can access the Internet and use Google Play Services. 
           
        <uses-permission android:name="android.permission.INTERNET"></uses-permission>
        <meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version" />

5. Instantiate StrapMetrics
     * _Mobile_ - You'll need a WearableListenerService on the mobile side, which you probably already have if you've built a Wear app. If you don't, here's one you can borrow:

            public class MyService extends WearableListenerService {

                private static final String TAG = "DataLayerSample";
                private static final String START_ACTIVITY_PATH = "/start-activity";
                private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";

                // instantiate StrapMetrics
                private final StrapMetrics sm = new StrapMetrics();


                public void onPeerConnected(com.google.android.gms.wearable.Node peer) {
                    super.onPeerConnected(peer);
                    String id = peer.getId();
                    String name = peer.getDisplayName();

                    Log.d("Wear_connect", "Connected peer name & ID: " + name + "|" + id);
                }


                @Override
                public void onDataChanged(DataEventBuffer dataEvents) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        Log.d(TAG, "onDataChanged: " + dataEvents);
                    }


                    // Loop through the events and send a message
                    // to the node that created the data item.
                    for (DataEvent event : dataEvents) {
                        Uri uri = event.getDataItem().getUri();
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                        DataMap map = dataMapItem.getDataMap();

                        //can strap can handle the data map? If so, strap should handle the event
                        if (sm.canHandleMsg(event)) {
                            // Log.d("DataEvent","Received new strapmetrics event!! " + map.toString());
                            try {
                                sm.processReceiveData(map);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // must call release() here to prevent dataEvents from growing too large.
                    dataEvents.release();
                }
            }

        


    * _Wear_ - To instantiate Strap you'll need to create a GoogleApiClient, have access to the Context, and have a valid appID to pass. Once you've created the Strap metrics, you can use the ```logEvent('/event-name/foo')``` function to log events. 


            public class MyActivity extends Activity {

                // CHANGE THE APP ID //
                // GET YOURS AT straphq.com //
                String strapAppID = "abc123abc123foo";
                Strap strap = null;

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
                                            // Now you can use the logEvent function (not until watch is connected)
                                            // log an event
                                            strap.logEvent("/app-load");
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

                            SensorManager sensor = (SensorManager)getSystemService(SENSOR_SERVICE);

                            // instantiate strap
                            strap = new Strap(mGoogleApiClient, getApplicationContext(), strapAppID);




                        }

                    });
                }
                public void handleButtonClick(View view) {
                    strap.logEvent("button-click");
                }
            }

![alt text](http://images.memegenerator.net/images/200x/1031.jpg "Success Kid")

That wasn't bad, right? You've successfully integrated Strap into your Android Wear application. We'll start crunching the numbers as data starts to flow into Strap, and you'll be seeing <a href="https://www.straphq.com/login">reports on the dashboard</a> in a few minutes. We have tested Strap in a variety of app configurations, but your feedback is extremely important to us in this beta period! If you have any questions, concerns, or problems with Strap Metrics, please let us know. You can open an issue on GitHub, visit our community support portal at http://strap.uservoice.com, email us at support@straphq.com, or tweet us @getstrap. 


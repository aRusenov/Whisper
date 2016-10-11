package com.example.nasko.whisper.data.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public abstract class NetworkStateReceiver {

    private static final String TAG = NetworkStateReceiver.class.getName();

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                NetworkInfo networkInfo = (NetworkInfo) intent.getExtras()
                        .get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    Log.i(TAG, "Network " + networkInfo.getTypeName() + " connected");
                    isConnected = true;
                    onNetworkConnected();
                } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                    Log.d(TAG, "There's no network connectivity");
                    isConnected = false;
                    onNoNetworkConnectivity();
                }
            }
        }
    };

    private Context context;
    private boolean isConnected;

    public NetworkStateReceiver(Context context) {
        this.context = context;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void start() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(networkStateReceiver, intentFilter);
    }

    public void stop() {
        context.unregisterReceiver(networkStateReceiver);
    }

    public abstract void onNetworkConnected();

    public abstract void onNoNetworkConnectivity();
}

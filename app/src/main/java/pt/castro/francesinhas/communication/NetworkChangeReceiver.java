package pt.castro.francesinhas.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pt.castro.francesinhas.tools.ConnectionUtils;

/**
 * Created by lourenco on 18/01/16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private final IConnectionObserver observer;

    public NetworkChangeReceiver(IConnectionObserver observer) {
        this.observer = observer;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        observer.onConnectionChange(ConnectionUtils.checkConnection(context));
    }
}
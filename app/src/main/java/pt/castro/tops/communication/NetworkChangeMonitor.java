package pt.castro.tops.communication;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import pt.castro.tops.tools.ConnectionUtils;

/**
 * Created by lourenco on 09/06/16.
 */
public class NetworkChangeMonitor implements INetworkChangeObserver {
    private NetworkChangeReceiver mConnectionMonitor;
    private IConnectionObserver mObserver;
    private boolean mConnected;

    public NetworkChangeMonitor(final IConnectionObserver observer) {
        mObserver = observer;
    }

    public void registerConnectionMonitor(final Context context) {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mConnectionMonitor = new NetworkChangeReceiver(this);
        context.registerReceiver(mConnectionMonitor, filter);
    }

    public void unregisterConnectionMonitor(final Context context) {
        if (mConnectionMonitor != null) {
            try {
                context.unregisterReceiver(mConnectionMonitor);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @Override
    public void onConnectionChange(boolean connected) {
        if (connected) {
            if (!mConnected) {
                mObserver.onConnect();
            }
        } else {
            mObserver.onDisconnect();
        }
        mConnected = connected;
    }

    public boolean isConnected() {
        return mConnected;
    }

    public boolean checkConnection(final Context context) {
        mConnected = ConnectionUtils.checkConnection(context);
        return mConnected;
    }
}
package pt.castro.tops.communication;

/**
 * Created by lourenco on 18/01/16.
 */
public interface INetworkChangeObserver {
    void onConnectionChange(final boolean connected);
}

package pt.castro.tops.communication;

/**
 * Created by lourenco on 09/06/16.
 */
public interface IConnectionObserver {
    void onConnect();

    void onDisconnect();
}
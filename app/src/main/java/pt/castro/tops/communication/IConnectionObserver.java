package pt.castro.tops.communication;

/**
 * Created by lourenco on 18/01/16.
 */
public interface IConnectionObserver {
    void onConnectionChange(final boolean connected);
}

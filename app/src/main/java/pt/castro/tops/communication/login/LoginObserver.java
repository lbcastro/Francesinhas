package pt.castro.tops.communication.login;

import pt.castro.francesinhas.backend.myApi.model.UserHolder;

/**
 * Created by lourenco on 26/03/16.
 */
public interface LoginObserver {
    void onLoginSuccess(final int sourceIndex, final UserHolder userHolder);

    void onLoginFail();
}

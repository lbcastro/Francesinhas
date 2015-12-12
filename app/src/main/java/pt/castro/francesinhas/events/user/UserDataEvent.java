package pt.castro.francesinhas.events.user;

import pt.castro.francesinhas.backend.myApi.model.UserHolder;

/**
 * Created by lourenco on 08/06/15.
 */
public class UserDataEvent {
    private UserHolder userHolder;

    public UserDataEvent(UserHolder userHolder) {
        this.userHolder = userHolder;
    }

    public UserHolder getUserHolder() {
        return userHolder;
    }
}

package pt.castro.francesinhas.events;

import pt.castro.francesinhas.backend.myApi.model.UserHolder;

/**
 * Created by lourenco.castro on 23/05/15.
 */
public class ScoreChangeEvent {
    private UserHolder userHolder;

    public ScoreChangeEvent(UserHolder userHolder) {
        this.userHolder = userHolder;
    }

    public UserHolder getUserHolder() {
        return userHolder;
    }
}
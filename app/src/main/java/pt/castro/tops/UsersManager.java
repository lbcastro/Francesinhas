package pt.castro.tops;

import pt.castro.francesinhas.backend.myApi.model.UserHolder;

/**
 * Created by lourenco on 27/03/16.
 */
public class UsersManager {

    private UserHolder mCurrentUser;

    public UserHolder getUser() {
        return mCurrentUser;
    }

    public void setUser(UserHolder currentUser) {
        this.mCurrentUser = currentUser;
    }
}

package pt.castro.tops.communication;

import android.os.AsyncTask;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.tops.events.connection.ConnectionFailedEvent;
import pt.castro.tops.events.user.NoUserEvent;
import pt.castro.tops.events.user.UserDataEvent;


/**
 * Created by Lourenço on 08/06/2015.
 */
public class UserEndpointActions extends AsyncTask<String, Void, UserHolder> {

    public final static int GET_USER = 1;
    public final static int ADD_USER = 2;

    private int mode;

    public UserEndpointActions(final int mode) {
        this.mode = mode;
    }

    @Override
    protected UserHolder doInBackground(String... params) {
        switch (mode) {
            case GET_USER:
                try {
                    return EndpointApiHolder.getInstance().getUser(params[0]).execute();
                } catch (IOException e) {
                    EventBus.getDefault().post(new ConnectionFailedEvent());
                }
            case ADD_USER:
                try {
                    return EndpointApiHolder.getInstance().addUser(params[0]).execute();
                } catch (IOException e) {
                    EventBus.getDefault().post(new ConnectionFailedEvent());
                }
        }
        return null;
    }

    @Override
    protected void onPostExecute(UserHolder userHolder) {
        super.onPostExecute(userHolder);
        if (userHolder == null) {
            if (mode == GET_USER) {
                EventBus.getDefault().post(new NoUserEvent());
            }
        } else {
            EventBus.getDefault().post(new UserDataEvent(userHolder));
        }
    }
}
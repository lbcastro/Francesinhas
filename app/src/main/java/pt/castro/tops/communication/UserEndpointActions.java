package pt.castro.tops.communication;

import android.os.AsyncTask;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

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
        String userId = params[0];
        switch (mode) {
            case GET_USER:
                try {
                    return EndpointApiHolder.getInstance().getUser(userId).execute();
                } catch (GoogleJsonResponseException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new NoUserEvent());
                } catch (IOException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new ConnectionFailedEvent());
                }
                break;
            case ADD_USER:
                String userEmail = params[1];
                try {
                    return EndpointApiHolder.getInstance().addUser(userId, userEmail).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new ConnectionFailedEvent());
                }
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(UserHolder userHolder) {
        super.onPostExecute(userHolder);
        if (userHolder != null) {
            EventBus.getDefault().post(new UserDataEvent(userHolder));
        }
    }
}
package pt.castro.francesinhas.communication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.francesinhas.events.UserDataEvent;


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
                    e.printStackTrace();
                }
            case ADD_USER:
                try {
                    return EndpointApiHolder.getInstance().addUser(params[0]).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    @Override
    protected void onPostExecute(UserHolder userHolder) {
        super.onPostExecute(userHolder);
        if (userHolder != null) {
            Log.d("UserHolder", userHolder.getId());
        } else {
            Log.d("UserHolder", "NULL");
        }
        EventBus.getDefault().post(new UserDataEvent(userHolder));
    }
}
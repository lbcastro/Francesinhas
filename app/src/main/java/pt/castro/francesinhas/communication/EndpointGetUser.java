package pt.castro.francesinhas.communication;

import android.os.AsyncTask;

import java.io.IOException;

import pt.castro.francesinhas.backend.myApi.model.UserHolder;

/**
 * Created by Lourenço on 08/06/2015.
 */
public class EndpointGetUser extends AsyncTask<String, Void, UserHolder> {

    @Override
    protected UserHolder doInBackground(String... params) {
        try {
            return EndpointApiHolder.getInstance().getUser(params[0]).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(UserHolder userHolder) {
        super.onPostExecute(userHolder);

    }
}
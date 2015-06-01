package pt.castro.francesinhas.communication;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.MyApi;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.events.ScoreChangeEvent;

public class EndpointsAsyncTask extends AsyncTask<ItemHolder, Void, ItemHolder> {

    public final static int ADD = 1;
    public final static int INCREASE = 2;
    public final static int DECREASE = 3;
    private static MyApi myApiService = null;
    private int activeMode;

    public EndpointsAsyncTask(int mode) {
        activeMode = mode;
    }

    @Override
    protected ItemHolder doInBackground(ItemHolder... params) {
        Log.d("EndpointsAsyncTask", "Starting");
        if (myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://castro-francesinhas.appspot.com/_ah/api/");
            // end options for devappserver

            myApiService = builder.build();
        }

        ItemHolder id = params[0];

        try {
            switch (activeMode) {
                case ADD:
                    return myApiService.insertQuote(id).execute();
                case INCREASE:
                    return myApiService.increaseScore(id).execute();
                case DECREASE:
                    return myApiService.decreaseScore(id).execute();
                default:
                    return id;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ItemHolder result) {
        if (activeMode == INCREASE) {
            ScoreChangeEvent scoreChangeEvent = new ScoreChangeEvent();
            scoreChangeEvent.itemHolder = result;
            EventBus.getDefault().post(scoreChangeEvent);
        }
    }
}
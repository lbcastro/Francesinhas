package pt.castro.francesinhas.communication;

import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.MyApi;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.events.ListRetrievedEvent;

/**
 * Created by lourenco.castro on 23/05/15.
 */
public class EndpointGetItems extends AsyncTask<Void, Void, List<ItemHolder>> {
    private static MyApi myApiService = null;

    @Override
    protected List<ItemHolder> doInBackground(Void... params) {
        if (myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new
                    AndroidJsonFactory(), null)
                    .setRootUrl("https://castro-francesinhas.appspot.com/_ah/api/");
            // end options for devappserver
            myApiService = builder.build();
        }
        try {
            return myApiService.listItems().execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onPostExecute(List<ItemHolder> list) {
        if (list == null) {
            list = Collections.emptyList();
        }
        ListRetrievedEvent listRetrievedEvent = new ListRetrievedEvent();
        listRetrievedEvent.list = list;
        EventBus.getDefault().post(listRetrievedEvent);
    }
}

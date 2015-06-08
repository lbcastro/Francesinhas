package pt.castro.francesinhas.communication;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.events.ListRetrievedEvent;

/**
 * Created by lourenco.castro on 23/05/15.
 */
public class EndpointGetItems extends AsyncTask<Void, Void, List<ItemHolder>> {
    @Override
    protected List<ItemHolder> doInBackground(Void... params) {

        try {
            return EndpointApiHolder.getInstance().listItems().execute().getItems();
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

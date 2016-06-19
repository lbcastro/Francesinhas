package pt.castro.tops.communication;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.MyApi;
import pt.castro.francesinhas.backend.myApi.model.CollectionResponseItemHolder;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.tops.events.connection.ConnectionFailedEvent;
import pt.castro.tops.events.list.ListRetrievedEvent;

/**
 * Created by lourenco on 29/03/16.
 */
public class EndpointSearch extends AsyncTask<String, Void, Void> {

    private String cursor = null;
    private int count = 0;

    public void setCursor(final String cursor) {
        this.cursor = cursor;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            MyApi.QueryItems items = EndpointApiHolder.getInstance().queryItems(params[0]);
            if (cursor != null) {
                items.setCursor(cursor);
            }
            if (count != 0) {
                items.setCount(count);
            } else {
                items.setCount(10);
            }
            CollectionResponseItemHolder response = items.execute();
            List<ItemHolder> list = response.getItems();
            String nextToken = response.getNextPageToken();
            if (list == null) {
                EventBus.getDefault().post(new ENoPlacesFound());
                return null;
            }
            ListRetrievedEvent listRetrievedEvent = new ListRetrievedEvent();
            listRetrievedEvent.setList(list);
            listRetrievedEvent.setToken(nextToken);
            EventBus.getDefault().post(listRetrievedEvent);
        } catch (IOException e) {
            EventBus.getDefault().post(new ConnectionFailedEvent());
        }
        return null;
    }

    public class ENoPlacesFound {

    }
}

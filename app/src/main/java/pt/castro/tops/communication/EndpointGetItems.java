package pt.castro.tops.communication;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.MyApi;
import pt.castro.francesinhas.backend.myApi.model.CollectionResponseItemHolder;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.tops.events.connection.ConnectionFailedEvent;
import pt.castro.tops.events.list.ListRetrievedEvent;

/**
 * Created by lourenco.castro on 23/05/15.
 */
public class EndpointGetItems extends AsyncTask<Void, Void, Void> {

    private static final int ITEMS_PER_PAGE = 20;

    private String cursor;
    private int count;

    public void setCursor(final String cursor) {
        this.cursor = cursor;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            MyApi.ListItems listItems = EndpointApiHolder.getInstance().listItems();
            if (cursor != null) {
                listItems.setCursor(cursor);
            }
            if (count != 0) {
                listItems.setCount(count);
            } else {
                listItems.setCount(ITEMS_PER_PAGE);
            }
            CollectionResponseItemHolder response = listItems.execute();
            List<ItemHolder> list = response.getItems();
            String nextToken = response.getNextPageToken();
            if (list == null) {
                list = Collections.emptyList();
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
}
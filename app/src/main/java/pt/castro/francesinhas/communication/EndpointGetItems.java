package pt.castro.francesinhas.communication;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.events.connection.ConnectionFailedEvent;
import pt.castro.francesinhas.events.list.ListRetrievedEvent;

/**
 * Created by lourenco.castro on 23/05/15.
 */
public class EndpointGetItems extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            List<ItemHolder> list = EndpointApiHolder.getInstance().listItems().execute
                    ().getItems();
            if (list == null) {
                list = Collections.emptyList();
            }
            ListRetrievedEvent listRetrievedEvent = new ListRetrievedEvent();
            listRetrievedEvent.list = list;
            EventBus.getDefault().post(listRetrievedEvent);
        } catch (IOException e) {
            EventBus.getDefault().post(new ConnectionFailedEvent());
        }
        return null;
    }
}
package pt.castro.francesinhas.communication;

import android.os.AsyncTask;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.events.ListRefreshEvent;
import pt.castro.francesinhas.events.PlaceAlreadyExistsEvent;

public class EndpointsAsyncTask extends AsyncTask<ItemHolder, Void, ItemHolder> {

    public final static int ADD = 1;
    public final static int UPDATE = 2;

    private int activeMode;

    public EndpointsAsyncTask(int mode) {
        activeMode = mode;
    }

    @Override
    protected ItemHolder doInBackground(ItemHolder... params) {
        ItemHolder itemHolder = params[0];
        switch (activeMode) {
            case ADD:
                try {
                    return EndpointApiHolder.getInstance().addItem(itemHolder).execute();
                } catch (IOException e) {
                    EventBus.getDefault().post(new PlaceAlreadyExistsEvent());
                }
            case UPDATE:
                try {
                    return EndpointApiHolder.getInstance().updateItem(itemHolder).execute();
                } catch (IOException e) {
                    // TODO: Figure out when this happens
                    e.printStackTrace();
                }
            default:
                return itemHolder;
        }
    }

    @Override
    protected void onPostExecute(ItemHolder result) {
        if (activeMode == ADD) {
            EventBus.getDefault().post(new ListRefreshEvent(false));
        }
    }
}
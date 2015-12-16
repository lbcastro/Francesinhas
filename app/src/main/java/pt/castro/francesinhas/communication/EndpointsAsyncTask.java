package pt.castro.francesinhas.communication;

import android.os.AsyncTask;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.events.list.ListRefreshEvent;
import pt.castro.francesinhas.events.place.PlaceAlreadyExistsEvent;

public class EndpointsAsyncTask extends AsyncTask<ItemHolder, Void, Void> {

    public final static int ADD = 1;
    public final static int UPDATE = 2;

    private int activeMode;

    public EndpointsAsyncTask(int mode) {
        activeMode = mode;
    }

    @Override
    protected Void doInBackground(ItemHolder... params) {
        ItemHolder itemHolder = params[0];
        switch (activeMode) {
            case ADD:
                try {
                    EndpointApiHolder.getInstance().addItem(itemHolder).execute();
                } catch (IOException e) {
                    EventBus.getDefault().post(new PlaceAlreadyExistsEvent());
                }
                break;
            case UPDATE:
                try {
                    EndpointApiHolder.getInstance().updateItem(itemHolder).execute();
                } catch (IOException e) {
                    // TODO: Figure out when this happens
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void arguments) {
        if (activeMode == ADD) {
            EventBus.getDefault().post(new ListRefreshEvent(false));
        }
    }
}
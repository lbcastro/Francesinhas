package pt.castro.francesinhas.communication;

import android.os.AsyncTask;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.events.ListRefreshEvent;
import pt.castro.francesinhas.events.PlaceAlreadyExistsEvent;
import pt.castro.francesinhas.events.ScoreChangeEvent;

public class EndpointsAsyncTask extends AsyncTask<ItemHolder, Void, ItemHolder> {

    public final static int ADD = 1;
    public final static int INCREASE = 2;
    public final static int DECREASE = 3;
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
            case INCREASE:
                try {
                    return EndpointApiHolder.getInstance().increaseScore(itemHolder).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            case DECREASE:
                try {
                    return EndpointApiHolder.getInstance().decreaseScore(itemHolder).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            default:
                return itemHolder;
        }
    }

    @Override
    protected void onPostExecute(ItemHolder result) {
        if (activeMode == INCREASE) {
            ScoreChangeEvent scoreChangeEvent = new ScoreChangeEvent();
            scoreChangeEvent.itemHolder = result;
            EventBus.getDefault().post(scoreChangeEvent);
        }
        if (activeMode == ADD) {
            EventBus.getDefault().post(new ListRefreshEvent(false));
        }
    }
}
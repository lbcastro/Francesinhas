package pt.castro.francesinhas.communication;

import android.os.AsyncTask;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import pt.castro.francesinhas.backend.myApi.model.UserHolder;
import pt.castro.francesinhas.events.ScoreChangeEvent;

/**
 * Created by lourenco on 08/06/15.
 */
public class EndpointUserVote extends AsyncTask<Integer, Void, UserHolder> {

    private final String userId;
    private final String itemId;

    public EndpointUserVote(final String userId, final String itemId) {
        this.userId = userId;
        this.itemId = itemId;
    }

    @Override
    protected UserHolder doInBackground(Integer... params) {
        try {
            return EndpointApiHolder.getInstance().addUserVote(userId, itemId, params[0]).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(UserHolder userHolder) {
        super.onPostExecute(userHolder);
        EventBus.getDefault().post(new ScoreChangeEvent(userHolder));
    }
}
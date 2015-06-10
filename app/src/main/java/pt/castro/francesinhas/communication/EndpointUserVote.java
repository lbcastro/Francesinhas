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

    private final UserHolder userHolder;
    private final String itemId;

    public EndpointUserVote(final UserHolder userHolder, final String itemId) {
        this.userHolder = userHolder;
        this.itemId = itemId;
    }

    @Override
    protected UserHolder doInBackground(Integer... params) {
        try {
            EndpointApiHolder.getInstance().addUserVote(itemId, params[0], userHolder).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(UserHolder userHolder) {
        super.onPostExecute(userHolder);
        EventBus.getDefault().post(new ScoreChangeEvent());
    }
}

package pt.castro.francesinhas.events;

/**
 * Created by lourenco.castro on 02-06-2015.
 */
public class ListRefreshEvent {

    private boolean refreshed;

    public ListRefreshEvent(final boolean refreshed) {
        this.refreshed = refreshed;
    }

    public boolean isRefreshed() {
        return refreshed;
    }
}

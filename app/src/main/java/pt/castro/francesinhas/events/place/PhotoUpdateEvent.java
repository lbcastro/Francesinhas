package pt.castro.francesinhas.events.place;

import pt.castro.francesinhas.list.LocalItemHolder;

/**
 * Created by lourenco on 22/06/15.
 */
public class PhotoUpdateEvent {
    private final LocalItemHolder localItemHolder;

    public PhotoUpdateEvent(LocalItemHolder localItemHolder) {
        this.localItemHolder = localItemHolder;
    }

    public LocalItemHolder getLocalItemHolder() {
        return localItemHolder;
    }
}

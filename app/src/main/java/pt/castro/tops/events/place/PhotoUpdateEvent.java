package pt.castro.tops.events.place;

import pt.castro.tops.list.LocalItemHolder;

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

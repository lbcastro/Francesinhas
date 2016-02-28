package pt.castro.francesinhas.list;

import pt.castro.francesinhas.backend.myApi.model.ItemHolder;

/**
 * Created by lourenco on 08/06/15.
 */
public class LocalItemHolder {
    private ItemHolder itemHolder;
    private int userVote;
    private float distance = -1;

    public LocalItemHolder(ItemHolder itemHolder) {
        this.itemHolder = itemHolder;
    }

    public ItemHolder getItemHolder() {
        return itemHolder;
    }

    public int getUserVote() {
        return userVote;
    }

    public void setUserVote(final int userVote) {
        this.userVote = userVote;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
package pt.castro.francesinhas.events;

import pt.castro.francesinhas.backend.myApi.model.ItemHolder;

/**
 * Created by lourenco on 08/06/15.
 */
public class UserClickEvent {
    private ItemHolder itemHolder;
    private int userVote;

    public UserClickEvent(ItemHolder itemHolder) {
        this.itemHolder = itemHolder;
    }


    public ItemHolder getItemHolder() {
        return itemHolder;
    }

    public int getUserVote() {
        return userVote;
    }

    public void setUserVote(int userVote) {
        this.userVote = userVote;
    }
}

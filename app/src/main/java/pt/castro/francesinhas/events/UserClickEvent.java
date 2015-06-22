package pt.castro.francesinhas.events;

import android.view.View;

import pt.castro.francesinhas.list.LocalItemHolder;

/**
 * Created by lourenco on 08/06/15.
 */
public class UserClickEvent {
    private View view;
    private LocalItemHolder itemHolder;
    private int userVote;

    public UserClickEvent(LocalItemHolder itemHolder) {
        this.itemHolder = itemHolder;
    }


    public LocalItemHolder getLocalItemHolder() {
        return itemHolder;
    }

    public int getUserVote() {
        return userVote;
    }

    public void setUserVote(int userVote) {
        this.userVote = userVote;
    }
}

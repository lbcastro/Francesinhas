package pt.castro.francesinhas.events.user;

import android.view.View;

import pt.castro.francesinhas.list.LocalItemHolder;

/**
 * Created by lourenco on 08/06/15.
 */
public class UserClickEvent {
    private LocalItemHolder itemHolder;
    private int userVote;
    private View view;

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

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}

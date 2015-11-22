package pt.castro.francesinhas.list;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

import pt.castro.francesinhas.backend.myApi.model.ItemHolder;

/**
 * Created by lourenco on 08/06/15.
 */
public class LocalItemHolder implements ParentListItem {
    private ItemHolder itemHolder;

    private int userVote;
    private List<CustomChild> list = new ArrayList<>();

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

    public void update(final ItemHolder itemHolder) {
        this.itemHolder = itemHolder;
    }

    public void setChilds() {
        CustomChild customChild = new CustomChild();
        list.add(customChild);
    }

    @Override
    public List<CustomChild> getChildItemList() {
        return list;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
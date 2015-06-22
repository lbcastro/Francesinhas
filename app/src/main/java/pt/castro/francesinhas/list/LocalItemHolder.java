package pt.castro.francesinhas.list;

import android.graphics.Bitmap;

import java.util.List;

import pt.castro.francesinhas.backend.myApi.model.ItemHolder;

/**
 * Created by lourenco on 08/06/15.
 */
public class LocalItemHolder {
    private ItemHolder itemHolder;
    private int userVote;
    private List<PhotoReference> photoReferences;
    private String photoUrl;
    private Bitmap photo;

    public LocalItemHolder(ItemHolder itemHolder) {
        this.itemHolder = itemHolder;
    }

    public ItemHolder getItemHolder() {
        return itemHolder;
    }

    public void setItemHolder(final ItemHolder itemHolder) {
        this.itemHolder = itemHolder;
    }

    public int getUserVote() {
        return userVote;
    }

    public void setUserVote(final int userVote) {
        this.userVote = userVote;
    }

    public List<PhotoReference> getPhotoReferences() {
        return photoReferences;
    }

    public void setPhotoReferences(List<PhotoReference> photoReferences) {
        this.photoReferences = photoReferences;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}

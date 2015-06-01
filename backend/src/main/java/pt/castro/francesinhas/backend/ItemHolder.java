package pt.castro.francesinhas.backend;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by lourenco.castro on 07/05/15.
 */
@Entity
public class ItemHolder {

    @Id
    private long id;
    private String name;
    private int ranking;
    private int votesUp;
    private int votesDown;
    private String location;
    private int imageResource;
    private BlobKey key;
    private Blob image;

    public void increaseRanking() {
        this.votesUp++;
    }

    public void decreaseRanking() {
        this.votesDown++;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getVotesUp() {
        return votesUp;
    }

    public int getVotesDown() {
        return votesDown;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
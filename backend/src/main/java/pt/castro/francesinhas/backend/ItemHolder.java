package pt.castro.francesinhas.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Object used to hold all data associated with a single item.
 * Created by lourenco.castro on 07/05/15.
 */
@Entity
public class ItemHolder {

    @Id
    private String id;
    private String name;
    private String userId;

    private int votesUp;
    private int votesDown;
    private int priceRange;

    private String location;
    private String address;
    private String phone;
    private String url;

    private String photoUrl;

    public ItemHolder increaseRanking() {
        this.votesUp++;
        return this;
    }

    public String getId() {
        return id;
    }

    public void decreaseRanking() {
        this.votesDown++;
    }

    public int getVotesUp() {
        return votesUp;
    }

    public int getVotesDown() {
        return votesDown;
    }

    public String getName() {
        return name;
    }

    public ItemHolder setName(String name) {
        this.name = name;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public ItemHolder setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public ItemHolder setAddress(final String address) {
        this.address = address;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public ItemHolder setPhone(final String phone) {
        this.phone = phone;
        return this;
    }

    public int getPriceRange() {
        return priceRange;
    }

    public ItemHolder setPriceRange(int priceRange) {
        this.priceRange = priceRange;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ItemHolder setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
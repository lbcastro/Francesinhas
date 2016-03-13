package pt.castro.tops.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.HashMap;

/**
 * Created by lourenco.castro on 07/06/15.
 */
@Entity
public class UserHolder {

    @Id
    private String id;
    private HashMap<String, Integer> votesMap;

    public UserHolder() {
        this.votesMap = new HashMap<>();
    }

    public void setVotesMap(HashMap<String, Integer> votesMap) {
        this.votesMap = votesMap;
    }

    public UserHolder addVote(final String itemId, final int vote) {
        votesMap.put(itemId, vote);
        return this;
    }

    public int getVote(final String itemId) {
        return votesMap.get(itemId);
    }

    public HashMap<String, Integer> getVotes() {
        return votesMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
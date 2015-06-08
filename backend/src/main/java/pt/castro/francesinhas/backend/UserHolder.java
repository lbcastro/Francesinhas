package pt.castro.francesinhas.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.HashMap;

/**
 * Created by lourenco.castro on 07/06/15.
 */
@Entity
public class UserHolder {

    @Id
    private String token;
    private String name;

    private HashMap<String, Integer> votesMap;

    public UserHolder(String token, String name) {
        this.token = token;
        this.name = name;
        this.votesMap = new HashMap<>();
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

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }
}

package pt.castro.tops.backend;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by lourenco on 26/03/16.
 */
@Entity
public class IdHolder {

    @Id
    private String id;

    private Ref<UserHolder> user;

    public IdHolder() {
    }

    public Ref<UserHolder> getUser() {
        return user;
    }

    public void setUser(Ref<UserHolder> user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
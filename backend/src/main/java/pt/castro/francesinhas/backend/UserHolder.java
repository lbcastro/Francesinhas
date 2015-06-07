package pt.castro.francesinhas.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by lourenco.castro on 07/06/15.
 */
@Entity
public class UserHolder {

    @Id
    private String token;
    private String name;
}

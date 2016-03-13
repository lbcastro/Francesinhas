package pt.castro.tops.events.list;

import java.util.List;

import pt.castro.francesinhas.backend.myApi.model.ItemHolder;

/**
 * Created by lourenco.castro on 23/05/15.
 */
public class ListRetrievedEvent {
    private String token;
    private List<ItemHolder> list;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<ItemHolder> getList() {
        return list;
    }

    public void setList(List<ItemHolder> list) {
        this.list = list;
    }
}

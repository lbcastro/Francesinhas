/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package pt.castro.francesinhas.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Named;

import static pt.castro.francesinhas.backend.OfyService.ofy;

@Api(name = "myApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend" +
        ".francesinhas.castro.pt", ownerName = "backend.francesinhas.castro.pt", packagePath = ""))
public class Endpoint {

    private List<ItemHolder> itemList = Collections.emptyList();

    @ApiMethod(name = "listItems")
    public CollectionResponse<ItemHolder> listItems(@Nullable @Named("cursor") String cursorString,
                                                    @Nullable @Named("count") Integer count) {
        Query<ItemHolder> query = ofy().load().type(ItemHolder.class);
        if (count != null) {
            query.limit(count);
        }
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }
        itemList = new ArrayList<>();
        QueryResultIterator<ItemHolder> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            itemList.add(iterator.next());
            if (count != null) {
                num++;
                if (num == count) break;
            }
        }

        //Find the next cursor
        if (cursorString != null && cursorString != "") {
            Cursor cursor = iterator.getCursor();
            if (cursor != null) {
                cursorString = cursor.toWebSafeString();
            }
        }
        updateRanking();
        ofy().clear();
        return CollectionResponse.<ItemHolder>builder().setItems(itemList).setNextPageToken
                (cursorString).build();
    }


    @ApiMethod(name = "updateItem")
    public ItemHolder updateItem(ItemHolder itemHolder) throws NotFoundException,
            ConflictException {
        if (findItem(itemHolder.getId()) == null) {
            return addItem(itemHolder);
        }
        ofy().save().entity(itemHolder).now();
        return itemHolder;
    }

    @ApiMethod(name = "addPhoto")
    public ItemHolder addPhoto(@Named("itemId") String itemId, @Named("photUrl") String photoUrl) {
        ItemHolder itemHolder;
        if ((itemHolder = findItem(itemId)) == null) {
            throw new NullPointerException("Item not found");
        }
        itemHolder.setPhotoUrl(photoUrl);
        ofy().save().entity(itemHolder).now();
        return itemHolder;
    }

    @ApiMethod(name = "increaseScore")
    public ItemHolder increaseScore(ItemHolder itemHolder) {
        ItemHolder previousItemHolder;
        if ((previousItemHolder = findItem(itemHolder.getId())) == null) {
            ofy().save().entity(itemHolder).now();
            previousItemHolder = itemHolder;
        }
        previousItemHolder.increaseRanking();
        ofy().save().entity(previousItemHolder).now();
        return previousItemHolder;
    }

    @ApiMethod(name = "decreaseScore")
    public ItemHolder decreaseScore(ItemHolder itemHolder) {
        ItemHolder previousItemHolder;
        if ((previousItemHolder = findItem(itemHolder.getId())) == null) {
            ofy().save().entity(itemHolder).now();
            previousItemHolder = itemHolder;
        }
        previousItemHolder.decreaseRanking();
        ofy().save().entity(previousItemHolder).now();
        return previousItemHolder;
    }

    private ItemHolder findItem(String id) {
        return ofy().load().type(ItemHolder.class).id(id).now();
    }

    private UserHolder findUser(String userId) {
        return ofy().load().type(UserHolder.class).id(userId).now();
    }

    @ApiMethod(name = "addItem")
    public ItemHolder addItem(ItemHolder itemHolder) throws ConflictException {
        if (findItem(itemHolder.getId()) != null) {
            throw new ConflictException("Item already exists");
        }
        ofy().save().entity(itemHolder).now();
        return itemHolder;
    }

    private void updateRanking() {
        Collections.sort(itemList, new RankingComparator());
        Collections.reverse(itemList);
    }

    @ApiMethod(name = "addUser")
    public UserHolder addUser(@Named("userId") String userId) throws ConflictException {
        if (findUser(userId) != null) {
            throw new ConflictException("User already exists");
        }
        final UserHolder userHolder = new UserHolder();
        userHolder.setId(userId);
        ofy().save().entity(userHolder).now();
        return userHolder;
    }

    @ApiMethod(name = "getUser")
    public UserHolder getUser(@Named("userId") String userId) throws ConflictException {
        UserHolder previousUserHolder;
        if ((previousUserHolder = findUser(userId)) == null) {
            throw new ConflictException("User not found");
        }
        return previousUserHolder;
    }

    @ApiMethod(name = "addUserVote")
    public UserHolder addUserVote(@Named("userId") String userId, @Named("itemId") String itemId,
                                  @Named("vote") int vote) {
        ItemHolder itemHolder;
        UserHolder userHolder;
        if ((userHolder = findUser(userId)) == null) {
            throw new NullPointerException("User not found");
        } else if ((itemHolder = findItem(itemId)) == null) {
            throw new NullPointerException("Item not found");
        } else {
            ofy().save().entity(userHolder.addVote(itemId, vote)).now();
            if (vote == -1) {
                decreaseScore(itemHolder);
            } else if (vote == 1) {
                increaseScore(itemHolder);
            }
            return userHolder;
        }
    }

    private class RankingComparator implements Comparator<ItemHolder> {
        @Override
        public int compare(ItemHolder o1, ItemHolder o2) {
            int delta1 = o1.getVotesUp() - o1.getVotesDown();
            int delta2 = o2.getVotesUp() - o2.getVotesDown();
            return delta1 < delta2 ? -1 : delta1 == delta2 ? 0 : 1;
        }
    }
}
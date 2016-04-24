/*
   For step-by-step instructions on connecting your Android application to this backend
    module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master
   /HelloEndpoints
*/

package pt.castro.tops.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Named;

import static pt.castro.tops.backend.OfyService.ofy;

@Api(name = "myApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend" + "" +
        ".francesinhas.castro.pt", ownerName = "backend.francesinhas.castro.pt",
        packagePath = ""))
public class Endpoint {

    @ApiMethod(name = "listItems")
    public CollectionResponse<ItemHolder> listItems(@Nullable @Named("cursor") String
                                                                cursorString, @Nullable @Named
            ("count") Integer count) {
        Query<ItemHolder> query = ofy().load().type(ItemHolder.class).order("-votesUp").order
                ("votesDown");
        if (count != null) {
            query.limit(count);
        }
        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }
        final List<ItemHolder> itemList = new ArrayList<>();
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
        Cursor cursor = iterator.getCursor();
        if (cursor != null) {
            cursorString = cursor.toWebSafeString();
        }
        updateRanking(itemList);
        ofy().clear();
        return CollectionResponse.<ItemHolder>builder().setItems(itemList).setNextPageToken
                (cursorString).build();
    }

    @ApiMethod(name = "queryItems")
    public CollectionResponse<ItemHolder> queryItems(@Nullable @Named("cursor") String
                                                                 cursorString, @Nullable @Named
            ("count") Integer count, @Named("query") String search) {
        Query<ItemHolder> query = ofy().load().type(ItemHolder.class).order("-votesUp").order
                ("votesDown");

        if (cursorString != null && cursorString != "") {
            query = query.startAt(Cursor.fromWebSafeString(cursorString));
        }

        search = Normalizer.normalize(search, Normalizer.Form.NFD);
        search = search.replaceAll("[^\\p{ASCII}]", "");

        final List<ItemHolder> itemList = new ArrayList<>();
        QueryResultIterator<ItemHolder> iterator = query.iterator();
        int num = 0;
        while (iterator.hasNext()) {
            final ItemHolder itemHolder = iterator.next();
            String name = Normalizer.normalize(itemHolder.getName().toLowerCase(), Normalizer
                    .Form.NFD);
            name = name.replaceAll("[^\\p{ASCII}]", "");
            if (name.contains(search)) {
                itemList.add(itemHolder);
                if (count != null) {
                    num++;
                    if (num == count) {
                        break;
                    }
                }
            }
        }

        //Find the next cursor
        Cursor cursor = iterator.getCursor();
        if (cursor != null) {
            cursorString = cursor.toWebSafeString();
        }
        updateRanking(itemList);
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
        final IdHolder holder = ofy().load().type(IdHolder.class).id(userId).now();
        if (holder != null) {
            return holder.getUser().get();
        }
        return null;
    }

    private UserHolder findUserByEmail(final String userEmail) {
        return ofy().load().type(UserHolder.class).id(userEmail).now();
    }

    private UserHolder findUserByOldId(final String userId) {
        return ofy().load().type(UserHolder.class).id(userId).now();
    }

    private UserHolder findUser(String userId, String userEmail) {
        IdHolder holder = ofy().load().type(IdHolder.class).id(userId).now();
        UserHolder userHolder;
        if (holder == null) {
            userHolder = ofy().load().type(UserHolder.class).id(userEmail).now();
            if (userHolder == null) {
                return null;
            }
            final IdHolder idHolder = new IdHolder();
            idHolder.setId(userId);
            idHolder.setUser(Ref.create(userHolder));
            ofy().save().entity(idHolder).now();
        } else {
            userHolder = holder.getUser().get();
        }
        return userHolder;
    }

    @ApiMethod(name = "addItem")
    public ItemHolder addItem(ItemHolder itemHolder) throws ConflictException {
        if (findItem(itemHolder.getId()) != null) {
            throw new ConflictException("Item already exists");
        }
        ofy().save().entity(itemHolder).now();
        return itemHolder;
    }

    private void updateRanking(final List<ItemHolder> itemList) {
        Collections.sort(itemList, new RankingComparator());
        Collections.reverse(itemList);
    }

    @ApiMethod(name = "addUser")
    public UserHolder addUser(@Named("userId") String userId, @Named("userEmail") String
            userEmail) throws ConflictException {
        UserHolder userHolder;
        if ((userHolder = findUser(userId, userEmail)) != null) {
            return userHolder;
        }
        final IdHolder idHolder = new IdHolder();
        idHolder.setId(userId);
        userHolder = findUserByEmail(userEmail);
        if (userHolder == null) {
            userHolder = findUserByOldId(userId);
            if (userHolder != null) {
                userHolder.setId(userEmail);
            } else {
                userHolder = new UserHolder();
                userHolder.setId(userEmail);
            }
        }
        idHolder.setUser(Ref.create(userHolder));
        ofy().save().entities(userHolder, idHolder).now();
        return userHolder;
    }

    @ApiMethod(name = "getUser")
    public UserHolder getUser(@Named("userId") String userId) throws NoSuchFieldError {
        UserHolder previousUserHolder;
        if ((previousUserHolder = findUser(userId)) == null) {
            throw new NoSuchFieldError("User not found");
        }
        return previousUserHolder;
    }

    @ApiMethod(name = "addUserVote")
    public UserHolder addUserVote(@Named("userId") String userId, @Named("itemId") String itemId,
                                  @Named("vote") int vote) {
        ItemHolder itemHolder;
        UserHolder userHolder;
        if ((userHolder = findUserByEmail(userId)) == null) {
            throw new NullPointerException("User not found");
        } else if ((itemHolder = findItem(itemId)) == null) {
            throw new NullPointerException("Item not found");
        } else {

            int previousVote = userHolder.getVotes().get(itemId) == null ? 0 : userHolder.getVote
                    (itemId);
            switch (vote) {
                case -1:
                    if (previousVote == 1) {
                        itemHolder.setVotesUp(itemHolder.getVotesUp() - 1);
                    } else if (previousVote == -1) {
                        itemHolder.setVotesDown(itemHolder.getVotesDown() - 1);
                        break;
                    }
                    itemHolder.setVotesDown(itemHolder.getVotesDown() + 1);
                    break;
                case 1:
                    if (previousVote == -1) {
                        itemHolder.setVotesDown(itemHolder.getVotesDown() - 1);
                    } else if (previousVote == 1) {
                        itemHolder.setVotesUp(itemHolder.getVotesUp() - 1);
                        break;
                    }
                    itemHolder.setVotesUp(itemHolder.getVotesUp() + 1);
                    break;
            }
            if (vote == previousVote) {
                vote = 0;
            }

            ofy().save().entity(itemHolder).now();
            ofy().save().entity(userHolder.addVote(itemId, vote)).now();
            return userHolder;
        }
    }

//    private class RankingComparator implements Comparator<ItemHolder> {
//        @Override
//        public int compare(ItemHolder o1, ItemHolder o2) {
//            int delta1 = o1.getVotesUp() - o1.getVotesDown();
//            int delta2 = o2.getVotesUp() - o2.getVotesDown();
//            return delta1 < delta2 ? -1 : delta1 == delta2 ? 0 : 1;
//        }
//    }

    private class RankingComparator implements Comparator<ItemHolder> {
        @Override
        public int compare(ItemHolder o1, ItemHolder o2) {
            int delta1 = (o1.getVotesUp() - o1.getVotesDown()) * o1.getVotesUp();
            int delta2 = (o2.getVotesUp() - o2.getVotesDown()) * o2.getVotesUp();
            return delta1 < delta2 ? -1 : delta1 == delta2 ? 0 : 1;
        }
    }
}
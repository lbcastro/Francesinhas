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
    private boolean dirtyList = true;

    @ApiMethod(name = "listItems")
    public CollectionResponse<ItemHolder> listItems(@Nullable @Named("cursor") String cursorString,
                                                    @Nullable @Named("count") Integer count) {
//        if (dirtyList) {
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
        dirtyList = false;
        updateRanking();
//        }
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
        dirtyList = true;
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
        dirtyList = true;
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
        dirtyList = true;
        return previousItemHolder;
    }

    private ItemHolder findItem(String id) {
        return ofy().load().type(ItemHolder.class).id(id).now();
    }

    @ApiMethod(name = "addItem")
    public ItemHolder addItem(ItemHolder quote) throws ConflictException {
        if (findItem(quote.getId()) != null) {
            if (findItem(quote.getId()) != null) {
                throw new ConflictException("Item already exists");
            }
        }
        ofy().save().entity(quote).now();
        dirtyList = true;
        return quote;
    }

    private void updateRanking() {
        Collections.sort(itemList, new RankingComparator());
        Collections.reverse(itemList);
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

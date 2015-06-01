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

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myApi", version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.francesinhas.castro.pt",
                ownerName = "backend.francesinhas.castro.pt",
                packagePath = ""))
public class MyEndpoint {

    private List<ItemHolder> itemList = Collections.emptyList();
    private boolean dirtyList = true;

    /**
     * Return a collection of quotes
     *
     * @param count The number of quotes
     * @return a list of Quotes
     */
    @ApiMethod(name = "listQuote")
    public CollectionResponse<ItemHolder> listQuote(@Nullable @Named("cursor") String cursorString,
                                                    @Nullable @Named("count") Integer count) {
        if (dirtyList) {
            Query<ItemHolder> query = ofy().load().type(ItemHolder.class);
            if (count != null) query.limit(count);
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
        }
        return CollectionResponse.<ItemHolder>builder().setItems(itemList).setNextPageToken(cursorString).build();
    }

    /**
     * This updates an existing <code>Quote</code> object.
     *
     * @param quote The object to be added.
     * @return The object to be updated.
     */
    @ApiMethod(name = "updateQuote")
    public ItemHolder updateQuote(ItemHolder quote) throws NotFoundException {
        if (findRecord(quote.getId()) == null) {
            throw new NotFoundException("Quote Record does not exist");
        }
        ofy().save().entity(quote).now();
        return quote;
    }

    @ApiMethod(name = "increaseScore")
    public ItemHolder increaseScore(ItemHolder itemHolder) throws NotFoundException {
        ItemHolder previousItemHolder;
        if ((previousItemHolder = findRecord(itemHolder.getId())) == null) {
            throw new NotFoundException("Quote Record does not exist");
        }
        previousItemHolder.increaseRanking();
        ofy().save().entity(previousItemHolder).now();
        dirtyList = true;
        return previousItemHolder;
    }

    @ApiMethod(name = "decreaseScore")
    public ItemHolder decreaseScore(ItemHolder itemHolder) throws NotFoundException {
        ItemHolder previousItemHolder;
        if ((previousItemHolder = findRecord(itemHolder.getId())) == null) {
            throw new NotFoundException("Quote Record does not exist");
        }
        previousItemHolder.decreaseRanking();
        ofy().save().entity(previousItemHolder).now();
        dirtyList = true;
        return previousItemHolder;
    }

    //Private method to retrieve a <code>Quote</code> record
    private ItemHolder findRecord(Long id) {
        return ofy().load().type(ItemHolder.class).id(id).now();
//or return ofy().load().type(Quote.class).filter("id",id).first.now();
    }


    /**
     * This inserts a new <code>Quote</code> object.
     *
     * @param quote The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertQuote")
    public ItemHolder insertQuote(ItemHolder quote) throws ConflictException {
//If if is not null, then check if it exists. If yes, throw an Exception
//that it is already present
        if (findRecord(quote.getId()) != null) {
            if (findRecord(quote.getId()) != null) {
                throw new ConflictException("Object already exists");
            }
        }
//Since our @Id field is a Long, Objectify will generate a unique value for us
//when we use put
        ofy().save().entity(quote).now();
        dirtyList = true;
        return quote;
    }

    private void updateRanking() {
        Collections.sort(itemList, new RankingComparator());
        Collections.reverse(itemList);
        for (ItemHolder itemHolder : itemList) {
            itemHolder.setRanking(itemList.indexOf(itemHolder) + 1);
        }
    }

    class RankingComparator implements Comparator<ItemHolder> {

        @Override
        public int compare(ItemHolder o1, ItemHolder o2) {
            int delta1 = o1.getVotesUp() - o1.getVotesDown();
            int delta2 = o2.getVotesUp() - o2.getVotesDown();
            return delta1 < delta2 ? -1 : delta1 == delta2 ? 0 : 1;
        }
    }
}

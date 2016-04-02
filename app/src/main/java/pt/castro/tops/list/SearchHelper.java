package pt.castro.tops.list;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.SearchView;

import de.greenrobot.event.EventBus;
import pt.castro.tops.CustomApplication;
import pt.castro.tops.communication.EndpointSearch;
import pt.castro.tops.events.EventBusHook;
import pt.castro.tops.events.list.ListRefreshEvent;

/**
 * Created by lourenco on 02/04/16.
 */
public class SearchHelper {

    private ISearchObserver mObserver;

    //    private String mNextToken;
    private Handler searchThread;
    private SearchView mSearchView;
    private String mLastQuery;

    public SearchHelper(final ISearchObserver observer) {
        mObserver = observer;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

//    @EventBusHook
//    public void onEventMainThread(final ListRetrievedEvent listRetrievedEvent) {
//        mNextToken = listRetrievedEvent.getToken();
//    }

    @EventBusHook
    public void onEventMainThread(final ListRefreshEvent listRefreshEvent) {
        if (!mSearchView.isIconified()) {
            resetSearchView();
        }
    }

    public boolean isIconified() {
        return mSearchView.isIconified();
    }

    public void setSearchView(final Activity context, final SearchView searchView) {
        SearchManager searchManager = (SearchManager) context.getSystemService(Context
                .SEARCH_SERVICE);
        mSearchView = searchView;
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(context.getComponentName()));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    search(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    search(newText);
                }
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                resetItems();
                return false;
            }
        });
    }

    private void search(final String query) {
        if (mLastQuery != null && query.equals(mLastQuery)) {
            return;
        }
        mLastQuery = query;
        if (searchThread != null) {
            searchThread.removeCallbacksAndMessages(null);
        }
        mObserver.onSearchStart();
        searchThread = new Handler();
        searchThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                new EndpointSearch().execute(query);
                CustomApplication.getPlacesManager().clear();
                searchThread = null;
            }
        }, 1000);
    }

    private void resetItems() {
        mLastQuery = null;
        mObserver.onSearchReset();
    }

    public void resetSearchView() {
        mSearchView.setQuery("", true);
        mSearchView.setIconified(true);
        mSearchView.clearFocus();
    }

    public String getQuery() {
        return mSearchView.getQuery().toString();
    }

    public interface ISearchObserver {
        void onSearchStart();

        void onSearchReset();
    }
}

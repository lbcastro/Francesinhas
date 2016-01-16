package pt.castro.francesinhas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pt.castro.francesinhas.list.LocalItemHolder;

/**
 * Created by lourenco on 10/01/16.
 */
public class PlacesManager {

    private Map<String, LocalItemHolder> items;
    private List<LocalItemHolder> sortedList;

    public PlacesManager() {
        this.items = new TreeMap<>();
    }

    public void add(final String placeId, final LocalItemHolder localItemHolder) {
        items.put(placeId, localItemHolder);
        sortedList = null;
    }

    public void remove(final String placeId) {
        items.remove(placeId);
        sortedList = null;
    }

    public Map<String, LocalItemHolder> getPlaces() {
        return items;
    }

    public void clear() {
        items.clear();
        sortedList = null;
    }

    public List<LocalItemHolder> getList() {
        if (sortedList == null) {
            sortedList = new ArrayList<>();
            sortedList.addAll(items.values());
            Collections.sort(sortedList, new RankingComparator());
            Collections.reverse(sortedList);
        }
        return sortedList;
    }

    private class RankingComparator implements Comparator<LocalItemHolder> {
        @Override
        public int compare(LocalItemHolder o1, LocalItemHolder o2) {
            int delta1 = o1.getItemHolder().getVotesUp() - o1.getItemHolder()
                    .getVotesDown();
            int delta2 = o2.getItemHolder().getVotesUp() - o2.getItemHolder()
                    .getVotesDown();
            return delta1 < delta2 ? -1 : delta1 == delta2 ? 0 : 1;
        }
    }
}

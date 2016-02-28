package pt.castro.francesinhas;

import android.location.Location;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.list.LocalItemHolder;

/**
 * Created by lourenco on 10/01/16.
 */
public class PlacesManager {

    private Location location;
    private Map<String, LocalItemHolder> items;
    private List<LocalItemHolder> sortedList;

    public PlacesManager() {
        this.items = new TreeMap<>();
    }

    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public void add(final String placeId, final LocalItemHolder localItemHolder) {
        if (location != null) {
            setLocation(localItemHolder, location);
        }
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
        }
        return sortedList;
    }

    public void setLocation(final Location location) {
        this.location = location;
        for (LocalItemHolder localItemHolder : items.values()) {
            setLocation(localItemHolder, location);
        }
        sortedList = null;
    }

    private void setLocation(final LocalItemHolder localItemHolder, final Location location) {
        ItemHolder itemHolder = localItemHolder.getItemHolder();
        Location itemLocation = new Location("");
        itemLocation.setLatitude(itemHolder.getLatitude());
        itemLocation.setLongitude(itemHolder.getLongitude());
        localItemHolder.setDistance(round(location.distanceTo(itemLocation) / 1000, 1));
    }

    private class RankingComparator implements Comparator<LocalItemHolder> {
        @Override
        public int compare(LocalItemHolder o1, LocalItemHolder o2) {
            int delta1 = o1.getItemHolder().getVotesUp() - o1.getItemHolder().getVotesDown();
            int delta2 = o2.getItemHolder().getVotesUp() - o2.getItemHolder().getVotesDown();
            return delta1 < delta2 ? -1 : delta1 == delta2 ? 0 : 1;
        }
    }

    private class DistanceComparator implements Comparator<LocalItemHolder> {
        @Override
        public int compare(LocalItemHolder o1, LocalItemHolder o2) {
            return o1.getDistance() > o2.getDistance() ? 1 : 0;
        }
    }
}

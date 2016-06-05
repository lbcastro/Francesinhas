package pt.castro.tops;

import android.location.Location;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.tops.list.LocalItemHolder;

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

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }

    public void add(final String placeId, final LocalItemHolder localItemHolder) throws Exception {
        if (location != null) {
            setLocation(localItemHolder, location);
        }
        localItemHolder.getItemHolder().getName().replaceAll("Restaurante", "");
        final LocalItemHolder existing = items.get(placeId);
        if (existing != null) {
            update(existing, localItemHolder);
            items.put(placeId, localItemHolder);
            sortedList = null;
            throw new Exception();
        } else {
            items.put(placeId, localItemHolder);
            sortedList = null;
        }
    }

    public void remove(final String placeId) {
        items.remove(placeId);
        sortedList = null;
    }

    private void update(LocalItemHolder existing, LocalItemHolder updated) {
        existing.updateItemHolder(updated.getItemHolder());
    }

    public LocalItemHolder get(final String placeId) {
        return items.get(placeId);
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

    public boolean hasLocation() {
        return location != null;
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

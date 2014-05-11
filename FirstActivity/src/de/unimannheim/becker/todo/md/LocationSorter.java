package de.unimannheim.becker.todo.md;

import android.location.Location;

import com.google.android.gms.location.LocationClient;

import de.unimannheim.becker.todo.md.model.Item;
import de.unimannheim.becker.todo.md.model.LocationDAO;

public class LocationSorter {

    public static void sort(LocationDAO locationDAO, LocationClient locationClient, Item[] items) {
        float[] distances = new float[items.length];
        Location lastLocation = locationClient.getLastLocation();
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            de.unimannheim.becker.todo.md.model.Location[] locations = locationDAO.getLocationsForItem(item.getId());
            Float min = Float.MAX_VALUE;
            for (de.unimannheim.becker.todo.md.model.Location l : locations) {
                float[] results = new float[1];
                Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(), l.getLatitude(),
                        l.getLongtitude(), results);
                if (results[0] < min) {
                    min = results[0];
                }
            }
            distances[i] = min;
        }
        sort(items, distances);
    }

    private static void sort(Item[] items, float[] distances) {
        for (int i = 0; i < distances.length; i++) {
            for (int j = i + 1; j < distances.length; j++) {
                if (distances[i] > distances[j]) {
                    float tmp = distances[i];
                    distances[i] = distances[j];
                    distances[j] = tmp;
                    Item temp = items [i];
                    items[i] = items[j];
                    items[j] = temp;
                }
            }
        }
    }
}

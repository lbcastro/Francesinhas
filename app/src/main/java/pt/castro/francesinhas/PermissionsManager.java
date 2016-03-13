package pt.castro.francesinhas;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lourenco on 28/02/16.
 */
public class PermissionsManager {

    private static final List<Pair<String, Integer>> permissionsList = new ArrayList<Pair<String,
            Integer>>() {{
        add(new Pair<>(android.Manifest.permission.ACCESS_FINE_LOCATION, 0));
        add(new Pair<>(android.Manifest.permission.ACCESS_COARSE_LOCATION, 1));
        add(new Pair<>(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, 3));
    }};


    public PermissionsManager() {
    }

    public boolean verifyPermissions(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            for (Pair<String, Integer> pair : permissionsList) {
                if (ActivityCompat.checkSelfPermission(activity, pair.first) != PackageManager
                        .PERMISSION_GRANTED) {
                    permissions.add(pair.first);
                }
            }
            if (permissions.size() > 0) {
                ActivityCompat.requestPermissions(activity, permissions.toArray(new
                        String[permissions.size()]), 0);
            } else {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    public boolean hasLocationPermission(final Activity activity) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ActivityCompat
                .checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}

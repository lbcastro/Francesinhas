package pt.castro.francesinhas.details;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dmax.staticmap.Callback;
import dmax.staticmap.Config;
import dmax.staticmap.Marker;
import dmax.staticmap.StaticMap;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.tools.PhotoUtils;

/**
 * Created by lourenco on 26/06/15.
 */
public class DetailsActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();
    @InjectView(R.id.name)
    TextView nameTextView;
    @InjectView(R.id.address)
    TextView addressTextView;
    @InjectView(R.id.phone)
    TextView phoneTextView;
    @InjectView(R.id.url)
    TextView urlTextView;
    //    @InjectView(R.id.votes_up)
//    TextView votesUpTextView;
//    @InjectView(R.id.votes_down)
//    TextView votesDownTextView;
    @InjectView(R.id.backdrop)
    ImageView imageView;
    @InjectView(R.id.map_view)
    ImageView mapView;
    private DetailsFragment mDetailsFragment;
    private String backgroundUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_details2);

        Bundle data = getIntent().getExtras();
        if (data == null) {
            Log.e(TAG, "onCreate: No details found");
            finish();
            return;
        }
        ButterKnife.inject(this);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d(TAG, data.getString(DetailsFragment.ITEM_NAME));

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(data.getString(DetailsFragment.ITEM_NAME));

        nameTextView.setText(data.getString(DetailsFragment.ITEM_NAME));
        nameTextView.setVisibility(View.GONE);
        addressTextView.setText(data.getString(DetailsFragment.ITEM_ADDRESS));
        phoneTextView.setText(data.getString(DetailsFragment.ITEM_PHONE));
        urlTextView.setText(data.getString(DetailsFragment.ITEM_URL));

        backgroundUrl = data.getString(DetailsFragment.ITEM_BACKGROUND_URL);
        if (backgroundUrl != null) {
            ImageLoader.getInstance().displayImage(backgroundUrl, imageView, PhotoUtils.getDisplayImageOptions());
        }

        final Double latitude = data.getDouble(DetailsFragment.ITEM_LATITUDE);
        final Double longitude = data.getDouble(DetailsFragment.ITEM_LONGITUDE);

        Config config = new Config();
        config.setImageSize(300, 500)
                .setZoom(16)
                .setScale(2)
                .setCenter(latitude.floatValue(), longitude.floatValue());

        final Marker marker = config.addMarker();
        marker.setLocation(latitude.floatValue(), longitude.floatValue());
        marker.setSize(Marker.Size.mid);

        Callback callback = new Callback() {
            public void onFailed(int errorCode, String errorMessage) {
                Log.e(TAG, "onFailed: loading image - " + errorMessage + " - " + errorCode);
            }

            public void onMapGenerated(Bitmap bitmap) {
                mapView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                Log.d(TAG, "onMapGenerated: finished loading");
            }
        };
        StaticMap.requestMapImage(this, config, callback);
    }
}

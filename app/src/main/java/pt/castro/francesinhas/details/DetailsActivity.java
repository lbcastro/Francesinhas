package pt.castro.francesinhas.details;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
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
import pt.castro.francesinhas.communication.EndpointGetItems;
import pt.castro.francesinhas.communication.EndpointUserVote;
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
    @InjectView(R.id.vote_up)
    TextView votesUpTextView;
    @InjectView(R.id.vote_down)
    TextView votesDownTextView;
    @InjectView(R.id.backdrop)
    ImageView imageView;
    @InjectView(R.id.map_view)
    ImageView mapView;
    int votesUp;
    int votesDown;
    private String backgroundUrl;
    private String userId;
    private String itemId;

    private void setActionBar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

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
        setActionBar();

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(data.getString(DetailsFragment.ITEM_NAME));

        setTitle(data.getString(DetailsFragment.ITEM_NAME));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTaskDescription(new ActivityManager.TaskDescription(data.getString(DetailsFragment.ITEM_NAME)));
        }

        userId = data.getString(DetailsFragment.USER_ID);
        itemId = data.getString(DetailsFragment.ITEM_ID);

        nameTextView.setText(data.getString(DetailsFragment.ITEM_NAME));
        nameTextView.setVisibility(View.GONE);
        addressTextView.setText(data.getString(DetailsFragment.ITEM_ADDRESS));
        phoneTextView.setText(data.getString(DetailsFragment.ITEM_PHONE));

        votesUp = data.getInt(DetailsFragment.ITEM_VOTES_UP);
        votesDown = data.getInt(DetailsFragment.ITEM_VOTES_DOWN);
        votesUpTextView.setText(Integer.toString(votesUp));
        votesDownTextView.setText(Integer.toString(votesDown));

        if (!userId.isEmpty()) {
            votesUpTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voteUp();
                }
            });
            votesDownTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voteDown();
                }
            });

            int userVote = data.getInt(DetailsFragment.USER_VOTE);
            if (userVote == 1) {
                votesUpTextView.setSelected(true);
            } else if (userVote == -1) {
                votesDownTextView.setSelected(true);
            }
        }

        if (data.getString(DetailsFragment.ITEM_URL) == null || data.getString(DetailsFragment.ITEM_URL).equals("n/a")) {
            urlTextView.setVisibility(View.GONE);
        } else {
            urlTextView.setText(data.getString(DetailsFragment.ITEM_URL));
            urlTextView.setVisibility(View.VISIBLE);
        }

        backgroundUrl = data.getString(DetailsFragment.ITEM_BACKGROUND_URL);
        if (backgroundUrl != null) {
            ImageLoader.getInstance().displayImage(backgroundUrl, imageView, PhotoUtils.getDisplayImageOptions());
        }

        final float latitude = (float) data.getDouble(DetailsFragment.ITEM_LATITUDE);
        final float longitude = (float) data.getDouble(DetailsFragment.ITEM_LONGITUDE);
        setMapView(latitude, longitude);
    }

    private void voteUp() {
        if (votesUpTextView.isSelected()) {
            vote(0);
            votesUp--;
            votesUpTextView.setSelected(false);
        } else {
            vote(1);
            votesUp++;
            votesUpTextView.setSelected(true);
            if (votesDownTextView.isSelected()) {
                votesDown--;
                votesDownTextView.setText(Integer.toString(votesDown));
                votesDownTextView.setSelected(false);
            }
        }
        votesUpTextView.setText(Integer.toString(votesUp));
    }

    private void voteDown() {
        if (votesDownTextView.isSelected()) {
            vote(0);
            votesDown--;
            votesDownTextView.setSelected(false);
        } else {
            vote(-1);
            votesDown++;
            votesDownTextView.setSelected(true);
            if (votesUpTextView.isSelected()) {
                votesUp--;
                votesUpTextView.setText(Integer.toString(votesUp));
                votesUpTextView.setSelected(false);
            }
        }
        votesDownTextView.setText(Integer.toString(votesDown));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new EndpointGetItems().execute();
    }

    private void vote(int vote) {
        new EndpointUserVote(userId, itemId).execute(vote);
    }

    private void setMapView(final float latitude, final float longitude) {
        Config config = new Config();
        config.setImageSize(300, 500)
                .setZoom(16)
                .setScale(2)
                .setCenter(latitude, longitude);

        final Marker marker = config.addMarker();
        marker.setLocation(latitude, longitude);
        marker.setSize(Marker.Size.mid);

        final Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        final Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mapIntent);
            }
        });

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

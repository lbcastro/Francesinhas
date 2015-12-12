package pt.castro.francesinhas.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.staticmap.Callback;
import dmax.staticmap.Config;
import dmax.staticmap.Marker;
import dmax.staticmap.StaticMap;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.list.decoration.EnterSharedElementCallback;
import pt.castro.francesinhas.list.decoration.TransitionUtils;

/**
 * Created by lourenco on 03/12/15.
 */
public class DetailsActivityOld extends AppCompatActivity {

    @Bind(R.id.details_address_label)
    TextView addressLabel;
    @Bind(R.id.details_address_content)
    TextView addressTextView;
    @Bind(R.id.details_phone_label)
    TextView phoneLabel;
    @Bind(R.id.details_phone_content)
    TextView phoneTextView;
    @Bind(R.id.details_url_label)
    TextView urlLabel;
    @Bind(R.id.details_url_content)
    TextView urlTextView;
    @Bind(R.id.map_view)
    ImageView mapView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_details);
        ButterKnife.bind(this);

        postponeEnterTransition();

        final View decor = getWindow().getDecorView();
        decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
                .OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                decor.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });

        getWindow().setEnterTransition(TransitionUtils.makeSlideTransition());
        getWindow().setExitTransition(TransitionUtils.makeSlideTransition());
        getWindow().setSharedElementEnterTransition(TransitionUtils
                .makeSharedElementEnterTransition(this));
        setEnterSharedElementCallback(new EnterSharedElementCallback(this));

        final Bundle data = getIntent().getExtras();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }

        setAddress(data);
        setPhone(data);
        setUrl(data);

        final String backgroundUrl = data.getString(DetailsKeys.ITEM_BACKGROUND_URL);
        title.setText(data.getString(DetailsKeys.ITEM_NAME));
//        ImageLoader.getInstance().displayImage(backgroundUrl, backdrop, PhotoUtils
//                .getDisplayImageOptions());
    }

    @OnClick(R.id.details_phone_content)
    public void onClickPhoneText() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneTextView.getText()));
        startActivity(intent);
    }

    @OnClick(R.id.details_address_content)
    public void onClickAddressText() {
    }


    private void setAddress(final Bundle data) {
        final String address = data.getString(DetailsKeys.ITEM_ADDRESS);
        if (address == null || address.isEmpty()) {
            addressTextView.setVisibility(View.GONE);
            addressLabel.setVisibility(View.GONE);
            mapView.setVisibility(View.GONE);
        } else {
            addressTextView.setText(address);
            final float latitude = (float) data.getDouble(DetailsKeys.ITEM_LATITUDE);
            final float longitude = (float) data.getDouble(DetailsKeys.ITEM_LONGITUDE);
            setMapView(latitude, longitude);
        }
    }

    private void setPhone(final Bundle data) {
        final String phone = data.getString(DetailsKeys.ITEM_PHONE);
        if (phone == null || phone.isEmpty()) {
            phoneLabel.setVisibility(View.GONE);
            phoneTextView.setVisibility(View.GONE);
        } else {
            phoneTextView.setText(phone);
        }
    }

    private void setUrl(final Bundle data) {
        final String url = data.getString(DetailsKeys.ITEM_URL);
        if (url == null || (url.isEmpty() || url.equals("n/a"))) {
            urlLabel.setVisibility(View.GONE);
            urlTextView.setVisibility(View.GONE);
        } else {
            urlTextView.setText(url);
            urlTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setMapView(final float latitude, final float longitude) {
        Config config = new Config();
        config.setImageSize(300, 500).setZoom(16).setScale(2).setCenter(latitude,
                longitude);

        final Marker marker = config.addMarker();
        marker.setLocation(latitude, longitude);
        marker.setSize(Marker.Size.mid);

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        final Intent mapsAppIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapsAppIntent.setPackage("com.google.android.apps.maps");

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(mapsAppIntent);
                } catch (ActivityNotFoundException e) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse
                            ("http://maps.google" +
                            ".com/maps?daddr=" + latitude + "," + longitude));
                    startActivity(intent);
                }
            }
        });

        Callback callback = new Callback() {
            public void onFailed(int errorCode, String errorMessage) {
            }

            public void onMapGenerated(Bitmap bitmap) {
                mapView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
            }
        };
        StaticMap.requestMapImage(this, config, callback);
    }
}

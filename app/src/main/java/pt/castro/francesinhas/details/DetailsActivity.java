package pt.castro.francesinhas.details;

import android.content.ActivityNotFoundException;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.staticmap.Callback;
import dmax.staticmap.Config;
import dmax.staticmap.Marker;
import dmax.staticmap.StaticMap;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.tools.PhotoUtils;

/**
 * Created by lourenco on 03/12/15.
 */
public class DetailsActivity extends AppCompatActivity {

    @Bind(R.id.details_address_content)
    TextView addressTextView;
    @Bind(R.id.details_phone_content)
    TextView phoneTextView;
    @Bind(R.id.details_url_content)
    TextView urlTextView;
    @Bind(R.id.map_view)
    ImageView mapView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.backdrop_image)
    ImageView backdrop;
    @Bind(R.id.details_phone_parent)
    LinearLayout phoneParent;
    @Bind(R.id.details_url_parent)
    LinearLayout urlParent;
    @Bind(R.id.details_address_parent)
    LinearLayout addressParent;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    private String location;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_details);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setActionBar();

        final Bundle data = getIntent().getExtras();
        collapsingToolbarLayout.setTitle(data.getString(DetailsKeys.ITEM_NAME));
        setBackdrop(data);
        setAddress(data);
        setPhone(data);
        setUrl(data);
    }

    private void setBackdrop(final Bundle data) {
        final String backgroundUrl = data.getString(DetailsKeys.ITEM_BACKGROUND_URL);
        if (backgroundUrl == null || backgroundUrl.equals("n/a")) {
            backdrop.setImageResource(R.drawable.francesinha_blur);
        } else {
            ImageLoader.getInstance().displayImage(backgroundUrl, backdrop, PhotoUtils
                    .getDisplayImageOptions(false));
        }
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            ImageLoader.getInstance().getDiskCache().save(location, bitmap);
        } catch (Exception ignored) {
            // This happens when the bitmap was null, which means it was already cached.
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.details_phone_parent)
    public void onClickPhoneParent() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneTextView.getText()));
        startActivity(intent);
    }

    @OnClick(R.id.details_url_parent)
    public void onUrlClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlTextView
                .getText().toString()));
        startActivity(browserIntent);
    }

    private void setAddress(final Bundle data) {
        final String address = data.getString(DetailsKeys.ITEM_ADDRESS);
        if (address == null || address.isEmpty()) {
            ((ViewGroup) addressParent.getParent()).setVisibility(View.GONE);
        } else {
            ((ViewGroup) addressParent.getParent()).setVisibility(View.VISIBLE);
            addressTextView.setText(address);
            final float latitude = (float) data.getDouble(DetailsKeys.ITEM_LATITUDE);
            final float longitude = (float) data.getDouble(DetailsKeys.ITEM_LONGITUDE);
            setMapView(address, latitude, longitude);
        }
    }

    private void setPhone(final Bundle data) {
        final String phone = data.getString(DetailsKeys.ITEM_PHONE);
        if (phone == null || phone.isEmpty()) {
            ((ViewGroup) phoneParent.getParent()).setVisibility(View.GONE);
        } else {
            ((ViewGroup) phoneParent.getParent()).setVisibility(View.VISIBLE);
            phoneTextView.setText(phone);
        }
    }

    private void setUrl(final Bundle data) {
        final String url = data.getString(DetailsKeys.ITEM_URL);
        if (url == null || (url.isEmpty() || url.equals("n/a"))) {
            ((ViewGroup) urlParent.getParent()).setVisibility(View.GONE);
        } else {
            ((ViewGroup) urlParent.getParent()).setVisibility(View.VISIBLE);
            urlTextView.setText(url);
            urlTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setMapView(final String address, final float latitude, final float
            longitude) {
        Config config = new Config();
        config.setImageSize(300, 500).setZoom(16).setScale(2).setCenter(latitude,
                longitude);

        final Marker marker = config.addMarker();
        marker.setLocation(latitude, longitude);
        marker.setSize(Marker.Size.mid);

        Uri gmmIntentUri;
        if (latitude + longitude == 0.0f) {
            ((ViewGroup) mapView.getParent()).setVisibility(View.GONE);
            gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
        } else {
            location = "" + latitude + "," + longitude;
            gmmIntentUri = Uri.parse("google.navigation:q=" + location);

            ((ViewGroup) mapView.getParent()).setVisibility(View.VISIBLE);

            final File mapImage = ImageLoader.getInstance().getDiskCache().get(location);
            if (mapImage != null && mapImage.exists()) {
                bitmap = PhotoUtils.bitmapFromFile(mapImage);
                mapView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
            } else {
                Callback callback = new Callback() {
                    public void onFailed(int errorCode, String errorMessage) {
                    }

                    public void onMapGenerated(Bitmap bitmap) {
                        mapView.setImageDrawable(new BitmapDrawable(getResources(),
                                bitmap));
                    }
                };
                StaticMap.requestMapImage(this, config, callback);
            }
        }

        final Intent mapsAppIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapsAppIntent.setPackage("com.google.android.apps.maps");

        addressParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(mapsAppIntent);
                } catch (ActivityNotFoundException e) {
                    final Intent intent = new Intent(android.content.Intent
                            .ACTION_VIEW, Uri.parse("http://maps.google" +
                            ".com/maps?daddr=" + latitude + "," + longitude));
                    startActivity(intent);
                }
            }
        });
    }
}

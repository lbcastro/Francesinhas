package pt.castro.francesinhas.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.ppamorim.dragger.DraggerPosition;
import com.github.ppamorim.dragger.DraggerView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.staticmap.Callback;
import dmax.staticmap.Config;
import dmax.staticmap.Marker;
import dmax.staticmap.StaticMap;
import pt.castro.francesinhas.CustomApplication;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.list.LocalItemHolder;
import pt.castro.francesinhas.tools.PhotoUtils;

/**
 * Created by lourenco on 03/12/15.
 */
public class DetailsActivity extends AppCompatActivity {

    @Bind(R.id.details_address_content)
    TextView addressTextView;
    @Bind(R.id.details_address_clickable)
    View addressClickable;
    @Bind(R.id.details_phone_content)
    TextView phoneTextView;
    @Bind(R.id.details_url_content)
    TextView urlTextView;
    @Bind(R.id.map_view)
    ImageView mapView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.backdrop_image)
    KenBurnsView backdrop;
    @Bind(R.id.details_phone_parent)
    LinearLayout phoneParent;
    @Bind(R.id.details_url_parent)
    LinearLayout urlParent;
    @Bind(R.id.details_address_parent)
    LinearLayout addressParent;
    @Bind(R.id.details_price_content)
    TextView priceContent;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.details_parent)
    LinearLayout detailsParent;
    @Bind(R.id.details_rating_parent)
    LinearLayout ratingParent;

    @Bind(R.id.nested_scroll)
    NestedScrollView nestedScrollView;
    @Bind(R.id.dragger_view)
    DraggerView draggerView;
    @Bind(R.id.appbar)
    AppBarLayout appBarLayout;

    private String location;
    private Bitmap bitmap;

    private LocalItemHolder item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setActionBar();

        final Bundle data = getIntent().getExtras();
        item = CustomApplication.getPlacesManager().getPlaces().get(data.getString("id"));
        collapsingToolbarLayout.setTitle(item.getItemHolder().getName());

        setScroll();
        setBackdrop();
        setAddress();
        setPhone();
        setRatings();
        setPriceRange();
        setUrl();
    }

    @Override
    public void onBackPressed() {
        draggerView.closeActivity();
    }

    private void setScroll() {
        draggerView.setAnimationDuration(200, 300);
        draggerView.setSlideEnabled(false);
        draggerView.setDraggerLimit(0.7f);
        draggerView.setFriction(6);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    draggerView.setDraggerPosition(DraggerPosition.TOP);
                    draggerView.setSlideEnabled(true);
                } else if (draggerView.getDragPosition() == DraggerPosition.TOP) {
                    draggerView.setDraggerPosition(DraggerPosition.BOTTOM);
                    draggerView.setSlideEnabled(false);
                }
            }
        });
        final View view = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1);
        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver
                .OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                draggerView.setSlideEnabled(view.getBottom() - (nestedScrollView.getHeight() +
                        nestedScrollView.getScrollY()) == 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
        AppEventsLogger.activateApp(this);
    }

    private void setBackdrop() {
        final String backgroundUrl = item.getItemHolder().getPhotoUrl();
        if (backgroundUrl == null || backgroundUrl.equals("n/a")) {
            backdrop.setImageResource(R.drawable.francesinha_blur);
        } else {
            ImageLoader.getInstance().displayImage(backgroundUrl, backdrop, PhotoUtils
                    .getDisplayImageOptions(false));
        }
    }

    private void setPriceRange() {
        final int price = item.getItemHolder().getPriceRange();
        if (price <= 0) {
            ((ViewGroup) priceContent.getParent()).setVisibility(View.GONE);
        } else {
            final String currency = "€";
            final String text = "<font color='#335EAE'>" + new String(new char[price]).replace
                    ("\0", currency) + "</font><font color='#ccbfbf'>" + new String(new char[5 -
                    price]).replace("\0", currency) + "</font>";
            priceContent.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlTextView.getText()
                .toString()));
        startActivity(browserIntent);
    }

    private LinearLayout addRatingBar(final ViewGroup parent, final int drawableResource, final
    float rating, final String url) {
        final LinearLayout ratingBar = (LinearLayout) LayoutInflater.from(this).inflate(R.layout
                .rating_bar, parent, false);
        final ImageView logo = (ImageView) ratingBar.findViewById(R.id.rating_logo);
        logo.setBackgroundResource(drawableResource);
        ratingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
        int total = 0;
        final int roundedRating = (int) rating;
        for (int x = 0; x < roundedRating; x++) {
            addStarImage(ratingBar, R.drawable.ic_star_white_18dp);
            total++;
        }
        if (rating - roundedRating >= 0.5) {
            addStarImage(ratingBar, R.drawable.ic_star_half_white_18dp);
            total++;
        }
        int rest = Math.round(5 - total);
        for (int x = 0; x < rest; x++) {
            addStarImage(ratingBar, R.drawable.ic_star_border_white_18dp);
        }
        return ratingBar;
    }

    private void addStarImage(final ViewGroup parent, final int drawableResource) {
        final ImageView star = (ImageView) LayoutInflater.from(this).inflate(R.layout
                .rating_star, parent, false);
        star.setBackgroundResource(drawableResource);
        parent.addView(star);
    }

    private void setRatings() {

        final String googleUrl = item.getItemHolder().getGoogleUrl();
        final String zomatoUrl = item.getItemHolder().getZomatoUrl();

        if (googleUrl != null) {
            final String[] googleData = googleUrl.split(";");
            final LinearLayout bar = addRatingBar(ratingParent, R.drawable.google, Float
                    .parseFloat(googleData[0]), googleData[1]);
            ratingParent.addView(bar);
        }

        if (googleUrl != null && zomatoUrl != null) {
            final View separator = LayoutInflater.from(this).inflate(R.layout.white_separator,
                    ratingParent, false);
            ratingParent.addView(separator);
        } else {
            ((ViewGroup) ratingParent.getParent()).setVisibility(View.GONE);
        }

        if (zomatoUrl != null) {
            final String[] zomatoData = zomatoUrl.split(";");
            final LinearLayout bar = addRatingBar(ratingParent, R.drawable.zomato, Float
                    .parseFloat(zomatoData[0]), zomatoData[1]);
            ratingParent.addView(bar);
        }
    }

    private void setAddress() {
        final String address = item.getItemHolder().getAddress();
        if (address == null || address.isEmpty()) {
            ((ViewGroup) addressParent.getParent()).setVisibility(View.GONE);
        } else {
            ((ViewGroup) addressParent.getParent()).setVisibility(View.VISIBLE);
            addressTextView.setText(address);
            final float latitude = (float) item.getItemHolder().getLatitude().doubleValue();
            final float longitude = (float) item.getItemHolder().getLongitude().doubleValue();
            setMapView(address, latitude, longitude);
        }
    }

//    private void setAddress(final Bundle data) {
//        final String address = data.getString(DetailsKeys.ITEM_ADDRESS);
//        if (address != null && !address.isEmpty()) {
//            final CardView addressCard = (CardView) LayoutInflater.from(this).inflate(R
//                    .layout.details_card, detailsParent, false);
//            final LinearLayout addressParent = (LinearLayout) addressCard.findViewById
//                    (R.id.details_parent);
//            final TextView addressLabel = (TextView) addressParent.findViewById(R.id
//                    .details_label);
//            addressLabel.setText(getString(R.string.details_address));
//
//            final Drawable drawable = ContextCompat.getDrawable(this, R.drawable
//                    .ic_map_black_24dp);
//            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor
//                    (this, R.color.blue), PorterDuff.Mode.SRC_IN));
//            addressLabel.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null,
//                    null);
//            final TextView addressContent = (TextView) LayoutInflater.from(this)
//                    .inflate(R.layout.details_text, addressParent, false);
//            addressContent.setText(address);
//            addressContent.setAutoLinkMask(Linkify.MAP_ADDRESSES);
//            addressContent.setMaxLines(5);
//            addressParent.addView(addressContent);
//            detailsParent.addView(addressCard);
//
//            final float latitude = (float) data.getDouble(DetailsKeys.ITEM_LATITUDE);
//            final float longitude = (float) data.getDouble(DetailsKeys.ITEM_LONGITUDE);
//            setMapView(address, latitude, longitude);
//        }
//    }

    private void setPhone() {
        final String phone = item.getItemHolder().getPhone();
        if (phone == null || phone.isEmpty()) {
            ((ViewGroup) phoneParent.getParent()).setVisibility(View.GONE);
        } else {
            ((ViewGroup) phoneParent.getParent()).setVisibility(View.VISIBLE);
            phoneTextView.setText(phone);
        }
    }

    private void setUrl() {
        final String url = item.getItemHolder().getUrl();
        if (url == null || (url.isEmpty() || url.equals("n/a"))) {
            ((ViewGroup) urlParent.getParent()).setVisibility(View.GONE);
        } else {
            ((ViewGroup) urlParent.getParent()).setVisibility(View.VISIBLE);
            urlTextView.setText(url);
            urlTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setMapView(final String address, final float latitude, final float longitude) {

        Config config = new Config();
        config.setImageSize((int) getResources().getDimension(R.dimen.map_view_height), (int)
                (getResources().getDimension(R.dimen.map_view_height) * 1.5)).setZoom(16)
                .setScale(2).setCenter(latitude, longitude);

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

            final File mapImage = ImageLoader.getInstance().getDiskCache().get(location);
            if (mapImage != null && mapImage.exists()) {
                bitmap = PhotoUtils.bitmapFromFile(mapImage);
                mapView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
            } else {
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

        final Intent mapsAppIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapsAppIntent.setPackage("com.google.android.apps.maps");

        addressClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(mapsAppIntent);
                } catch (ActivityNotFoundException e) {
                    final Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri
                            .parse("http://maps.google" +
                            ".com/maps?daddr=" + latitude + "," + longitude));
                    startActivity(intent);
                }
            }
        });
    }
}

package pt.castro.tops.details;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.ppamorim.dragger.DraggerCallback;
import com.github.ppamorim.dragger.DraggerPosition;
import com.github.ppamorim.dragger.DraggerView;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dmax.staticmap.Config;
import dmax.staticmap.Marker;
import dmax.staticmap.builder.HeadSegment;
import dmax.staticmap.builder.MapTypeSegment;
import dmax.staticmap.builder.MarkerSegment;
import dmax.staticmap.builder.PositionSegment;
import dmax.staticmap.builder.ScaleSegment;
import dmax.staticmap.builder.Segment;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.tops.CustomApplication;
import pt.castro.tops.R;
import pt.castro.tops.tools.LayoutUtils;
import pt.castro.tops.tools.PhotoUtils;

/**
 * Created by lourenco on 03/12/15.
 */
public class DetailsActivity extends AppCompatActivity implements DraggerCallback {

    private static List<Segment> segments = new LinkedList<>();

    static {
        segments.add(new HeadSegment());
        segments.add(new MapTypeSegment());
        segments.add(new ScaleSegment());
        segments.add(new MarkerSegment());
        segments.add(new PositionSegment());
    }

    @Bind(R.id.details_address_content)
    TextView addressTextView;
    @Bind(R.id.details_address_clickable)
    View addressClickable;
    @Bind(R.id.map_view)
    ImageView mapView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.backdrop_image)
    KenBurnsView backdrop;
    @Bind(R.id.details_address_parent)
    LinearLayout addressParent;
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

    private static String buildUrl(Config config, Context context) {
        final StringBuilder urlBuilder = new StringBuilder();
        for (Segment segment : segments) {
            segment.append(config, urlBuilder, context);
        }
        return urlBuilder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        if (LayoutUtils.isLollipopOrAbove()) {
            LayoutUtils.setTransparentStatusBar(this);
        } else if (LayoutUtils.isKitkatOrAbove()) {
            LayoutUtils.setImmersiveMode(this);
        }

        if (!LayoutUtils.hasSoftKeys(this) && LayoutUtils.isKitkatOrAbove()) {
            final View nestedLinear = findViewById(R.id.nested_linear);
            if (nestedLinear != null) {
                nestedLinear.setPadding(0, 0, 0, 0);
            }
        }

        setActionBar();
        setDraggerView();

        final Bundle data = getIntent().getExtras();
        final ItemHolder item = CustomApplication.getPlacesManager().getPlaces().get(data
                .getString("id")).getItemHolder();
        collapsingToolbarLayout.setTitle(item.getName());
        setTitle(item.getName());

        setBackdrop(item);
        setAddress(item);
        setRatings(item);
        setDetails(item);

        draggerView.show();
    }

    @Override
    public void onBackPressed() {
        draggerView.closeActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getApplication());
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

    private void setDraggerView() {
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

    private void setBackdrop(final ItemHolder item) {
        final String backgroundUrl = item.getPhotoUrl();
        if (!hasContent(backgroundUrl)) {
            backdrop.setImageResource(R.drawable.francesinha_blur);
        } else {
            Uri uri = Uri.parse(backgroundUrl);
            Picasso.with(this).load(uri).into(backdrop);
        }
    }

    private void setActionBar() {
        setSupportActionBar(toolbar);
        if (LayoutUtils.isKitkatOrAbove() && !LayoutUtils.isLollipopOrAbove()) {
            toolbar.setPadding(0, LayoutUtils.getStatusBarHeight(getResources()), 0, 0);
            toolbar.getLayoutParams().height = toolbar.getLayoutParams().height + LayoutUtils
                    .getStatusBarHeight(getResources());
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }
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
        final LinearLayout ratingParent = (LinearLayout) ratingBar.findViewById(R.id.rating_parent);
        for (int x = 0; x < roundedRating; x++) {
            LayoutUtils.addStarImage(this, ratingParent, R.drawable.ic_star_white_18dp);
            total++;
        }
        if (rating - roundedRating >= 0.5) {
            LayoutUtils.addStarImage(this, ratingParent, R.drawable.ic_star_half_white_18dp);
            total++;
        }
        int rest = Math.round(5 - total);
        for (int x = 0; x < rest; x++) {
            LayoutUtils.addStarImage(this, ratingParent, R.drawable.ic_star_border_white_18dp);
        }
        return ratingBar;
    }

    private void setRatings(final ItemHolder item) {

        final String googleUrl = item.getGoogleUrl();
        final String zomatoUrl = item.getZomatoUrl();

        if (hasContent(googleUrl)) {
            final String[] googleData = googleUrl.split(";");
            final LinearLayout bar = addRatingBar(ratingParent, R.drawable.google, Float
                    .parseFloat(googleData[0]), googleData[1]);
            ratingParent.addView(bar);
            ((ViewGroup) ratingParent.getParent()).setVisibility(View.VISIBLE);
        }

        if (hasContent(googleUrl) && hasContent(zomatoUrl)) {
            final View separator = LayoutInflater.from(this).inflate(R.layout.white_separator,
                    ratingParent, false);
            ratingParent.addView(separator);
        }

        if (hasContent(zomatoUrl)) {
            final String[] zomatoData = zomatoUrl.split(";");
            final LinearLayout bar = addRatingBar(ratingParent, R.drawable.zomato, Float
                    .parseFloat(zomatoData[0]), zomatoData[1]);
            ratingParent.addView(bar);
            ((ViewGroup) ratingParent.getParent()).setVisibility(View.VISIBLE);
        }
    }

    private void setAddress(final ItemHolder item) {
        final String address = item.getAddress();
        if (address == null || address.isEmpty()) {
            addressParent.setVisibility(View.GONE);
        } else {
            addressParent.setVisibility(View.VISIBLE);
            final TextView text = (TextView) addressParent.findViewById(R.id.details_address_label);
            text.setCompoundDrawablesWithIntrinsicBounds(PhotoUtils.tintedDrawable(this, R
                    .drawable.ic_map_black_24dp), null, null, null);
            addressTextView.setText(address);
            final float latitude = (float) item.getLatitude().doubleValue();
            final float longitude = (float) item.getLongitude().doubleValue();
            setMapView(address, latitude, longitude);
        }
    }

    private void setDetails(final ItemHolder itemHolder) {
        final String phone = itemHolder.getPhone();
        if (hasContent(phone)) {
            final LinearLayout phoneLinear = LayoutUtils.generateDetailsLinear(this,
                    detailsParent, getString(R.string.details_phone), R.drawable
                            .ic_local_phone_black_24dp, phone);
            detailsParent.addView(phoneLinear);
            phoneLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                }
            });
        }

        final String url = itemHolder.getUrl();
        if (hasContent(url)) {
            final View separator = LayoutInflater.from(this).inflate(R.layout.gray_separator,
                    ratingParent, false);
            detailsParent.addView(separator);
            final LinearLayout urlLinear = LayoutUtils.generateDetailsLinear(this, detailsParent,
                    getString(R.string.details_website), R.drawable.ic_public_black_24dp, url);
            detailsParent.addView(urlLinear);
            urlLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });
        }

        final int price = itemHolder.getPriceRange();
        if (price > 0) {
            final View separator = LayoutInflater.from(this).inflate(R.layout.gray_separator,
                    ratingParent, false);
            detailsParent.addView(separator);
            final String currency = "€";
            final String text = "<font color='#448AFF'>" + new String(new char[price]).replace
                    ("\0", currency) + "</font><font color='#ccbfbf'>" + new String(new char[5 -
                    price]).replace("\0", currency) + "</font>";
            final LinearLayout priceLinear = LayoutUtils.generateDetailsLinear(this,
                    detailsParent, getString(R.string.price_range), R.drawable
                            .ic_account_balance_wallet_black_24dp, "");
            final TextView priceContent = (TextView) priceLinear.findViewById(R.id
                    .details_linear_content);
            priceContent.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            priceContent.setTypeface(null, Typeface.BOLD);
            detailsParent.addView(priceLinear);
        }

    }

    private boolean hasContent(final String string) {
        return string != null && !string.equals("n/a") && !string.isEmpty();
    }

    private void setMapView(final String address, final float latitude, final float longitude) {

        mapView.measure(View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.MATCH_PARENT,
                View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(R.dimen
                .map_view_height, View.MeasureSpec.EXACTLY));

        Config config = new Config();
        config.setImageSize(mapView.getMeasuredHeight(), mapView.getMeasuredWidth()).setZoom(16)
                .setScale(2).setCenter(latitude, longitude);

        final Marker marker = config.addMarker();
        marker.setLocation(latitude, longitude);
        marker.setSize(Marker.Size.mid);

        Uri gmmIntentUri;
        if (latitude + longitude == 0.0f) {
            ((ViewGroup) mapView.getParent()).setVisibility(View.GONE);
            gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
        } else {
            String location = "" + latitude + "," + longitude;
            gmmIntentUri = Uri.parse("google.navigation:q=" + location);
            final Uri uri = Uri.parse(buildUrl(config, this));
            Picasso.with(this).load(uri).into(mapView);
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

    @Override
    public void onProgress(double v) {

    }

    @Override
    public void notifyOpen() {

    }

    @Override
    public void notifyClose() {
        this.finish();
    }
}

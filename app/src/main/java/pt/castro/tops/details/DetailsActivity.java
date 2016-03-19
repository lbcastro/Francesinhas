package pt.castro.tops.details;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import pt.castro.tops.tools.PhotoUtils;

/**
 * Created by lourenco on 03/12/15.
 */
public class DetailsActivity extends AppCompatActivity {

    private static List<Segment> segments = new LinkedList<Segment>();

    static {
        segments.add(new HeadSegment()); // must be first
        segments.add(new MapTypeSegment());
        segments.add(new ScaleSegment());
        segments.add(new MarkerSegment());
        segments.add(new PositionSegment());
    }

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

    private static String buildUrl(Config config, Context context) {
        final StringBuilder urlBuilder = new StringBuilder();
        for (Segment segment : segments) {
            segment.append(config, urlBuilder, context);
        }
        return urlBuilder.toString();
    }

    private static void addStarImage(final Context context, final ViewGroup parent, final int
            drawableResource) {
        final ImageView star = (ImageView) LayoutInflater.from(context).inflate(R.layout
                .rating_star, parent, false);
        star.setBackgroundResource(drawableResource);
        parent.addView(star);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setActionBar();
        setScroll();

        final Bundle data = getIntent().getExtras();
        final ItemHolder item = CustomApplication.getPlacesManager().getPlaces().get(data
                .getString("id")).getItemHolder();
        collapsingToolbarLayout.setTitle(item.getName());
        setTitle(item.getName());

        setBackdrop(item);
        setAddress(item);
        setPhone(item);
        setRatings(item);
        setPriceRange(item);
        setUrl(item);
    }

    @Override
    public void onBackPressed() {
        draggerView.closeActivity();

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
//        overridePendingTransition(0, 0);
        super.onPause();
        AppEventsLogger.deactivateApp(this);
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
        final Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneTextView.getText()));
        startActivity(intent);
    }

    @OnClick(R.id.details_url_parent)
    public void onUrlClick() {
        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlTextView.getText
                ().toString()));
        startActivity(browserIntent);
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

    private void setBackdrop(final ItemHolder item) {
        final String backgroundUrl = item.getPhotoUrl();
        if (!hasContent(backgroundUrl)) {
            backdrop.setImageResource(R.drawable.francesinha_blur);
        } else {
            Uri uri = Uri.parse(backgroundUrl);
            Picasso.with(this).load(uri).into(backdrop);
        }
    }

    private void setPriceRange(final ItemHolder item) {
        final int price = item.getPriceRange();
        if (price <= 0) {
            ((ViewGroup) priceContent.getParent()).setVisibility(View.GONE);
        } else {
            final String currency = "€";
            final String text = "<font color='#335EAE'>" + new String(new char[price]).replace
                    ("\0", currency) + "</font><font color='#ccbfbf'>" + new String(new char[5 -
                    price]).replace("\0", currency) + "</font>";
            priceContent.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
            final TextView textView = (TextView) ((ViewGroup) priceContent.getParent())
                    .findViewById(R.id.details_price_label);
            textView.setCompoundDrawablesWithIntrinsicBounds(PhotoUtils.tintedDrawable(this, R
                    .drawable.ic_account_balance_wallet_black_24dp), null, null, null);
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
            addStarImage(this, ratingBar, R.drawable.ic_star_white_18dp);
            total++;
        }
        if (rating - roundedRating >= 0.5) {
            addStarImage(this, ratingBar, R.drawable.ic_star_half_white_18dp);
            total++;
        }
        int rest = Math.round(5 - total);
        for (int x = 0; x < rest; x++) {
            addStarImage(this, ratingBar, R.drawable.ic_star_border_white_18dp);
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
            ((ViewGroup) addressParent.getParent()).setVisibility(View.GONE);
        } else {
            ((ViewGroup) addressParent.getParent()).setVisibility(View.VISIBLE);
            final TextView text = (TextView) addressParent.findViewById(R.id.details_address_label);
            text.setCompoundDrawablesWithIntrinsicBounds(PhotoUtils.tintedDrawable(this, R
                    .drawable.ic_map_black_24dp), null, null, null);
            addressTextView.setText(address);
            final float latitude = (float) item.getLatitude().doubleValue();
            final float longitude = (float) item.getLongitude().doubleValue();
            setMapView(address, latitude, longitude);
        }
    }

    private void setPhone(final ItemHolder item) {
        final String phone = item.getPhone();
        if (phone == null || phone.isEmpty()) {
            ((ViewGroup) phoneParent.getParent()).setVisibility(View.GONE);
        } else {
            ((ViewGroup) phoneParent.getParent()).setVisibility(View.VISIBLE);
            final TextView text = (TextView) phoneParent.findViewById(R.id.details_phone_label);
            text.setCompoundDrawablesWithIntrinsicBounds(PhotoUtils.tintedDrawable(this, R
                    .drawable.ic_local_phone_black_24dp), null, null, null);
            phoneTextView.setText(phone);
        }
    }

    private boolean hasContent(final String string) {
        return string != null && !string.equals("n/a");
    }

    private void setUrl(final ItemHolder item) {
        final String url = item.getUrl();
        if (!hasContent(url)) {
            ((ViewGroup) urlParent.getParent()).setVisibility(View.GONE);
        } else {
            ((ViewGroup) urlParent.getParent()).setVisibility(View.VISIBLE);
            final TextView text = (TextView) urlParent.findViewById(R.id.details_url_label);
            text.setCompoundDrawablesWithIntrinsicBounds(PhotoUtils.tintedDrawable(this, R
                    .drawable.ic_public_black_24dp), null, null, null);
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
}

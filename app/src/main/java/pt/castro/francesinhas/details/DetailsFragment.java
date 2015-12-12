package pt.castro.francesinhas.details;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.staticmap.Callback;
import dmax.staticmap.Config;
import dmax.staticmap.Marker;
import dmax.staticmap.StaticMap;
import pt.castro.francesinhas.R;

/**
 * Created by lourenco.castro on 13-06-2015.
 */
public class DetailsFragment extends Fragment {

    private final String TAG = getClass().getName();

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
    @Bind(R.id.scrollView)
    ObservableScrollView observable;
    @Bind(R.id.map_view)
    ImageView mapView;
    @Bind(R.id.details_card)
    CardView card;
    @Bind(R.id.text_parent)
    RelativeLayout textParent;
    @Bind(R.id.custom_row_name)
    TextView titleTextView;
    @Bind(R.id.votes_up)
    TextView votesUp;
    @Bind(R.id.votes_down)
    TextView votesDown;

    private String backgroundUrl;

    public static DetailsFragment newInstance(final Bundle bundle) {
        DetailsFragment fragment2 = new DetailsFragment();
        fragment2.setArguments(bundle);
        return fragment2;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_details3,
                container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
//        card.animate().translationY(-40).setDuration(600).setInterpolator(new
//                AccelerateDecelerateInterpolator()).setListener(new Animator
//                .AnimatorListener() {
//
//
//            @Override
//            public void onAnimationStart(Animator animation) {
//                detailsView.onViewCreated(textParent);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
        MaterialViewPagerHelper.registerScrollView(getActivity(), observable, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            setAddress(getArguments());
            setPhone(getArguments());
            setUrl(getArguments());
            setVotes(getArguments());
            setTitle(getArguments());

            setTextOrHide(addressTextView, getArguments().getString(DetailsKeys
                    .ITEM_ADDRESS));
            setTextOrHide(phoneTextView, getArguments().getString(DetailsKeys
                    .ITEM_PHONE));
            setTextOrHide(urlTextView, getArguments().getString(DetailsKeys.ITEM_URL));
        }
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

    public void setTitle(Bundle bundle) {
        titleTextView.setText(bundle.getString(DetailsKeys.ITEM_NAME));
    }

    public void setVotes(Bundle bundle) {
        votesUp.setText(Integer.toString(bundle.getInt(DetailsKeys.ITEM_VOTES_UP)));
        votesDown.setText(Integer.toString(bundle.getInt(DetailsKeys.ITEM_VOTES_DOWN)));
    }

    private void setTextOrHide(final TextView textView, final String text) {
        if (text == null || text.isEmpty() || text.equals("n/a")) {
            textView.setVisibility(View.GONE);
            return;
        }
        try {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            textView.setVisibility(View.GONE);
        }
    }

    private void setTextOrSave(final TextView textView, final String text, final String
            key) {
        try {
            setTextOrHide(textView, text);
        } catch (NullPointerException e) {
            Bundle bundle = this.getArguments();
            if (bundle == null) {
                bundle = new Bundle(4);
            }
            bundle.putString(key, text);
            setArguments(bundle);
        }
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
                    final Intent intent = new Intent(android.content.Intent
                            .ACTION_VIEW, Uri.parse("http://maps.google" +
                            ".com/maps?daddr=" + latitude + "," + longitude));
                    startActivity(intent);
                }
            }
        });

        Callback callback = new Callback() {
            public void onFailed(int errorCode, String errorMessage) {
            }

            public void onMapGenerated(Bitmap bitmap) {
                if (isAdded()) {
                    mapView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                }
            }
        };
        StaticMap.requestMapImage(getActivity(), config, callback);
    }

    public void hideCard() {
        card.animate().translationY(1000).setDuration(600).alpha(0).setInterpolator(new
                AccelerateDecelerateInterpolator());
    }
}
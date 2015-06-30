package pt.castro.francesinhas.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.tools.PhotoUtils;

/**
 * Created by lourenco.castro on 13-06-2015.
 */
public class DetailsFragment extends Fragment {

    public static final String ITEM_NAME = "name";
    public static final String ITEM_ADDRESS = "address";
    public static final String ITEM_PHONE = "phone";
    public static final String ITEM_URL = "url";
    public static final String ITEM_VOTES_UP = "votes_up";
    public static final String ITEM_VOTES_DOWN = "votesDown";
    public static final String ITEM_BACKGROUND_URL = "background_url";
    public static final String ITEM_LATITUDE = "latitude";
    public static final String ITEM_LONGITUDE = "longitude";
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

    private String backgroundUrl;

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_details2,
                container, false);
        ButterKnife.inject(this, view);
//        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            setTextOrHide(nameTextView, getArguments().getString(ITEM_NAME));
            setTextOrHide(addressTextView, getArguments().getString(ITEM_ADDRESS));
            setTextOrHide(phoneTextView, getArguments().getString(ITEM_PHONE));
            setTextOrHide(urlTextView, getArguments().getString(ITEM_URL));
//            setTextOrHide(votesUpTextView, getArguments().getString(ITEM_VOTES_UP));
//            setTextOrHide(votesDownTextView, getArguments().getString(ITEM_VOTES_DOWN));
        }

        if (backgroundUrl != null) {
            ImageLoader.getInstance().displayImage(backgroundUrl, imageView, PhotoUtils.getDisplayImageOptions());
        }

        addressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + addressTextView.getText
                        () + "(" + nameTextView.getText() + ")");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getActivity().startActivity(intent);
            }
        });

        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneTextView.getText()));
                startActivity(intent);
            }
        });
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

    private void setTextOrSave(final TextView textView, final String text, final String key) {
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

    public void setItemName(final String name) {
        Log.d(TAG, "setItemName: setting name");
        setTextOrSave(nameTextView, name, ITEM_NAME);
    }

    public void setItemAddress(final String address) {
        setTextOrSave(addressTextView, address, ITEM_ADDRESS);
    }

    public void setItemPhone(final String phone) {
        setTextOrSave(phoneTextView, phone, ITEM_PHONE);
    }

    public void setItemUrl(final String url) {
        try {
            URI uri = new URI(url);
            setTextOrSave(urlTextView, uri.getHost(), ITEM_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setVotesUp(final String votesUp) {
//        setTextOrSave(votesUpTextView, votesUp, ITEM_VOTES_UP);
    }

    public void setVotesDown(final String votesDown) {
//        setTextOrSave(votesDownTextView, votesDown, ITEM_VOTES_DOWN);
    }

    public void setBackgroundUrl(final String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }
}
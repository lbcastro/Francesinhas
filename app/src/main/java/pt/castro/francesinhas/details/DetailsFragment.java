package pt.castro.francesinhas.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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
    public static final String ITEM_ID = "item_id";
    public static final String USER_ID = "user_id";
    public static final String USER_VOTE = "user_vote";

    private final String TAG = getClass().getName();
    @InjectView(R.id.name)
    TextView nameTextView;
    @InjectView(R.id.address)
    TextView addressTextView;
    @InjectView(R.id.phone)
    TextView phoneTextView;
    @InjectView(R.id.url)
    TextView urlTextView;

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
        }

        if (backgroundUrl != null) {
            ImageLoader.getInstance().displayImage(backgroundUrl, imageView, PhotoUtils.getDisplayImageOptions());
        }
    }

    @OnClick(R.id.phone)
    public void onClickPhoneText() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneTextView.getText()));
        startActivity(intent);
    }

    @OnClick(R.id.address)
    public void onClickAddressText() {
        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + addressTextView.getText() + "(" + nameTextView.getText() + ")");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
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
}
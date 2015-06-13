package pt.castro.francesinhas.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pt.castro.francesinhas.R;

/**
 * Created by lourenco.castro on 13-06-2015.
 */
public class DetailsFragment extends DialogFragment {

    private static final String ITEM_NAME = "name";
    private static final String ITEM_ADDRESS = "address";
    private static final String ITEM_PHONE = "phone";
    private static final String ITEM_URL = "url";
    private static final String ITEM_VOTES_UP = "votes_up";
    private static final String ITEM_VOTES_DOWN = "votesDown";
    @InjectView(R.id.name) TextView nameTextView;
    @InjectView(R.id.address) TextView addressTextView;
    @InjectView(R.id.phone) TextView phoneTextView;
    @InjectView(R.id.url) TextView urlTextView;
    @InjectView(R.id.votes_up) TextView votesUpTextView;
    @InjectView(R.id.votes_down) TextView votesDownTextView;

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.MyAnimation_Window;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_details,
                container, false);
        ButterKnife.inject(this, view);
        if (getArguments() != null) {
            setTextOrHide(nameTextView, getArguments().getString(ITEM_NAME));
            setTextOrHide(addressTextView, getArguments().getString(ITEM_ADDRESS));
            setTextOrHide(phoneTextView, getArguments().getString(ITEM_PHONE));
            setTextOrHide(urlTextView, getArguments().getString(ITEM_URL));
            setTextOrHide(votesUpTextView, getArguments().getString(ITEM_VOTES_UP));
            setTextOrHide(votesDownTextView, getArguments().getString(ITEM_VOTES_DOWN));
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

        return view;
    }

    private void setTextOrHide(final TextView textView, final String text) {
        try {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            textView.setVisibility(View.GONE);
        }
    }

    private void setTextOrSave(final TextView textView, final String text, final String key) {
        try {
            textView.setText(text);
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
        setTextOrSave(nameTextView, name, ITEM_NAME);
    }

    public void setItemAddress(final String address) {
        setTextOrSave(addressTextView, address, ITEM_ADDRESS);
    }

    public void setItemPhone(final String phone) {
        setTextOrSave(phoneTextView, phone, ITEM_PHONE);
    }

    public void setItemUrl(final String url) {
        setTextOrSave(urlTextView, url, ITEM_URL);
    }

    public void setVotesUp(final String votesUp) {
        setTextOrSave(votesUpTextView, votesUp, ITEM_VOTES_UP);
    }

    public void setVotesDown(final String votesDown) {
        setTextOrSave(votesDownTextView, votesDown, ITEM_VOTES_DOWN);
    }
}
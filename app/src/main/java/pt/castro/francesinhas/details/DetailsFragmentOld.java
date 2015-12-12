package pt.castro.francesinhas.details;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pt.castro.francesinhas.R;

/**
 * Created by lourenco.castro on 13-06-2015.
 */
public class DetailsFragmentOld extends Fragment {

    private final String TAG = getClass().getName();
    @Bind(R.id.details_address_content)
    TextView addressTextView;
    @Bind(R.id.details_phone_content)
    TextView phoneTextView;
    @Bind(R.id.details_url_content)
    TextView urlTextView;
//    @Bind(R.id.backdrop)
//    ImageView imageView;

    private String backgroundUrl;

    public static DetailsFragmentOld newInstance() {
        return new DetailsFragmentOld();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout
                .fragment_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            setTextOrHide(addressTextView, getArguments().getString(DetailsKeys
                    .ITEM_ADDRESS));
            setTextOrHide(phoneTextView, getArguments().getString(DetailsKeys
                    .ITEM_PHONE));
            setTextOrHide(urlTextView, getArguments().getString(DetailsKeys.ITEM_URL));
        }

        if (backgroundUrl != null) {
//            ImageLoader.getInstance().displayImage(backgroundUrl, imageView,
// PhotoUtils.getDisplayImageOptions());
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
}
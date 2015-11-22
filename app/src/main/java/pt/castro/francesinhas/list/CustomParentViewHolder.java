package pt.castro.francesinhas.list;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pt.castro.francesinhas.R;
import pt.castro.francesinhas.backend.myApi.model.ItemHolder;
import pt.castro.francesinhas.tools.PhotoUtils;

/**
 * Created by lourenco on 14/11/15.
 */
public class CustomParentViewHolder extends ParentViewHolder {

    boolean expanded;

    @InjectView(R.id.custom_row_ranking)
    TextView rankingTextView;
    @InjectView(R.id.custom_row_name)
    TextView titleTextView;
    @InjectView(R.id.custom_row_location)
    TextView subtitleTextView;
    @InjectView(R.id.custom_row_image)
    ImageView imageView;
    @InjectView(R.id.custom_row_clickable)
    View clickable;

    /**
     * Default constructor.
     *
     * @param itemView The {@link View} being hosted in this ViewHolder
     */
    public CustomParentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.inject(this, itemView);
    }

    public void bind(ItemHolder itemHolder) {
        if (itemHolder.getPhotoUrl() != null && !itemHolder.getPhotoUrl().equals("n/a")) {
            final ImageLoader imageLoader = ImageLoader.getInstance();
            final ImageViewAware aware = new ImageViewAware(imageView, false);
            imageLoader.cancelDisplayTask(aware);
            imageLoader.displayImage(itemHolder.getPhotoUrl(), aware, PhotoUtils
                    .getDisplayImageOptions());
        } else {
            imageView.setImageDrawable(null);
        }
        final String text = Integer.toString(1);
        rankingTextView.setText(text);
        titleTextView.setText(itemHolder.getName());
        subtitleTextView.setText(itemHolder.getLocation());
        clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expanded) {
                    collapseView();
                } else {
                    expandView();
                }
                expanded = !expanded;
            }
        });
    }
}

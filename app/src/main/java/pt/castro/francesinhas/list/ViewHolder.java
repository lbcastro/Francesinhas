//package pt.castro.francesinhas.list;
//
//import android.support.v4.util.Pair;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import butterknife.ButterKnife;
//import butterknife.InjectView;
//import pt.castro.francesinhas.R;
//import pt.castro.francesinhas.communication.EndpointsAsyncTask;
//
///**
// * Holds view references for the CustomRecyclerViewAdapter class.
// */
//public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//    public ViewHolder(View itemView) {
//        super(itemView);
//        ButterKnife.inject(this, itemView);
//        itemView.setOnClickListener(this);
//        clickable.setOnClickListener(this);
//    }
//
//    @InjectView(R.id.custom_row_ranking)
//    TextView rankingTextView;
//    @InjectView(R.id.custom_row_name)
//    TextView titleTextView;
//    @InjectView(R.id.custom_row_location)
//    TextView subtitleTextView;
//    @InjectView(R.id.custom_row_image)
//    ImageView imageView;
//    @InjectView(R.id.custom_row_clickable)
//    View clickable;
//
//    @Override
//    public void onClick(View v) {
//        Log.d("ViewHolder", "Clicked");
//        new EndpointsAsyncTask().execute(new Pair<>(v.getContext(), titleTextView.getText().toString()));
//    }
//}

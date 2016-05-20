package ca.liandrew.awesometoronto.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ca.liandrew.awesometoronto.R;
import ca.liandrew.awesometoronto.model.Place;

/**
 * Created by liandrew on 2016-05-07.
 */
public class FavoritesAdapter extends
        RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<Place> mPlaces;

    public FavoritesAdapter() {
        mPlaces = Place.findWithQuery(Place.class, "Select * from Place where favorite = ?", "1");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_places, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Place place = mPlaces.get(position);

        // Set item views based on the data model
        TextView textView = holder.nameTextView;
        textView.setText(place.getTitle());

        Context context = holder.imgViewIcon.getContext();

        Picasso.with(context)
                .load(mPlaces.get(position).getImageUrl())
                .placeholder(R.drawable.ic_menu_gallery)
                .centerCrop()
                .fit()
                .tag(context)
                .into(holder.imgViewIcon);

        Picasso.with(context)
                .load((mPlaces.get(position).getFavorite() ? R.drawable.ic_favorite_18pt_3x : R.drawable.ic_favorite_border_18pt_3x))
                .into(holder.favBtn);

        holder.favBtn.setOnClickListener(new FavoriteClicker(position, holder.favBtn, context));
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    // used to cache view in item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView nameTextView;
        public ImageView imgViewIcon;
        public ImageButton favBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.place_name);
            imgViewIcon = (ImageView) itemView.findViewById(R.id.imgPlace);
            favBtn = (ImageButton) itemView.findViewById(R.id.btnFavorite);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();

            Place place = mPlaces.get(position);
            Uri uriUrl = Uri.parse(place.getUrl());
            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
            v.getContext().startActivity(launchBrowser);
        }
    }

    public class SampleScrollListener implements AbsListView.OnScrollListener {
        private final Context context;
        private final Object scrollTag = new Object();

        public SampleScrollListener(Context context) {
            this.context = context;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            final Picasso picasso = Picasso.with(context);
            if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                picasso.resumeTag(scrollTag);
            } else {
                picasso.pauseTag(scrollTag);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            // Do nothing
        }
    }

    public class FavoriteClicker implements View.OnClickListener {
        ImageButton favBtn;
        private int position;
        private Context context;

        public FavoriteClicker(int position, ImageButton favBtn, Context context){
            this.position = position;
            this.favBtn = favBtn;
            this.context = context;
        }

        @Override
        public void onClick(View v){
            Long pos = mPlaces.get(position).getId();

            if(mPlaces.get(position).getFavorite()){
                mPlaces.get(position).setFavorite(false);
                Picasso.with(context)
                        .load(R.drawable.ic_favorite_border_18pt_3x)
                        .into(favBtn);

                Place place = Place.findById(Place.class, pos.intValue());
                place.setFavorite(false);
                place.save(); // update entry with new values

                mPlaces.remove(mPlaces.get(position));
                notifyDataSetChanged();
            }
        }
    }

}

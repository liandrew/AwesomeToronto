package ca.liandrew.awesometoronto.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.liandrew.awesometoronto.R;

/**
 * Created by liandrew on 2016-05-07.
 */
public class FavoritesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.favorites_layout,
                container, false );

        FavoritesRecyclerView rvFavorites = (FavoritesRecyclerView) view.findViewById(R.id.rvFavorites);

        // when recycle view is empty show text description
        TextView evDescription = (TextView) view.findViewById(R.id.tvFavDescription);
        TextView evTitle = (TextView) view.findViewById(R.id.tvFavorites);
        View[] emptyTextViewList = {evDescription, evTitle};

        FavoritesAdapter adapter = new FavoritesAdapter();
        // set the empty views
        rvFavorites.setEmptyViews(emptyTextViewList);
        rvFavorites.setHasFixedSize(true);
        rvFavorites.setAdapter(adapter);
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

}

package ca.liandrew.awesometoronto.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.liandrew.awesometoronto.R;
import ca.liandrew.awesometoronto.model.Place;

/**
 * Created by liandrew on 2016-05-07.
 */
public class AttractionsFragment extends Fragment {

    private SharedPreferences mPrefs = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.attractions_layout,
                container, false );

        loadPlacesData();

        PlacesRecyclerView rvPlaces = (PlacesRecyclerView) view.findViewById(R.id.rvPlaces);

        // when recycle view is empty show text description
        TextView evDescription = (TextView) view.findViewById(R.id.tvAttractionsDescription);
        TextView evTitle = (TextView) view.findViewById(R.id.tvAttractions);
        View[] emptyTextViewList = {evDescription, evTitle};

        PlacesAdapter adapter = new PlacesAdapter();
        // set the empty views
        rvPlaces.setEmptyViews(emptyTextViewList);
        rvPlaces.setHasFixedSize(true);
        rvPlaces.setAdapter(adapter);
        rvPlaces.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void loadPlacesData(){
        mPrefs = getActivity().getSharedPreferences("ca.liandrew.awesometoronto", getActivity().MODE_PRIVATE);

        if (mPrefs.getBoolean("first-run", true)) {
            // load places on first run
            boolean loadSuccess = Place.loadFromPlist("places.plist", getContext());
            if(loadSuccess){
                mPrefs.edit().putBoolean("first-run", false).commit();
            }
        }
    }

}

package ca.liandrew.awesometoronto.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.longevitysoft.android.xml.plist.PListXMLHandler;
import com.longevitysoft.android.xml.plist.PListXMLParser;
import com.longevitysoft.android.xml.plist.domain.Array;
import com.longevitysoft.android.xml.plist.domain.Dict;
import com.longevitysoft.android.xml.plist.domain.PList;
import com.longevitysoft.android.xml.plist.domain.PListObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ca.liandrew.awesometoronto.R;
import ca.liandrew.awesometoronto.model.Place;

/**
 * Created by liandrew on 2016-05-07.
 */
public class AttractionsFragment extends Fragment {

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
        getActivity();
        SharedPreferences mPrefs = getActivity().getSharedPreferences("ca.liandrew.awesometoronto", Context.MODE_PRIVATE);

        if (mPrefs.getBoolean("first-run", true)) {
            // load places on first run
            boolean loadSuccess = loadFromPlist("places.plist", getContext());
            if(loadSuccess){
                mPrefs.edit().putBoolean("first-run", false).apply();
            }
        }
    }

    public static boolean loadFromPlist(String plist, Context context){
        //start reading
        PListXMLParser parser = new PListXMLParser();
        PListXMLHandler handler = new PListXMLHandler();
        parser.setHandler(handler);

        try {
            parser.parse(context.getAssets().open(plist));

            PList actualPList = ((PListXMLHandler) parser.getHandler()).getPlist();
            Array placesList = ((Array) actualPList.getRootElement());

            Map<String,String> placeImgList = new HashMap<String,String>();
            placeImgList.put("Ripley's Aquarium", "aquarium");
            placeImgList.put("Art Gallery of Ontario", "artgalleryontario");
            placeImgList.put("CN Tower", "cntower");
            placeImgList.put("Royal Ontario Museum", "rom");
            placeImgList.put("City Hall", "torontocityhall");
            placeImgList.put("Eaton Center", "torontoeatoncentre");
            placeImgList.put("Toronto Zoo", "torontozoo");
            placeImgList.put("Yorkdale Mall", "yorkdalemall");
            placeImgList.put("Hockey Hall of Fame", "hockeyhallfame");
            placeImgList.put("Air Canada Centre", "aircanadacentre");

            for (PListObject obj : placesList) {
                switch (obj.getType()) {
                    case DICT:
                        Dict dicPlace = (Dict) obj;

                        String lng = dicPlace.getConfiguration("lng").getValue();
                        String lat = dicPlace.getConfiguration("lat").getValue();
                        String title = dicPlace.getConfiguration("title").getValue();
                        String url = dicPlace.getConfiguration("url").getValue();

                        String imageFileName = placeImgList.get(title);
                        if(imageFileName == null){
                            // use place holder image instead
                            imageFileName = "ic_menu_gallery";
                        }

                        int imageId = context.getResources().getIdentifier(imageFileName.toLowerCase(Locale.getDefault()),
                                "drawable", context.getPackageName());

                        Place attraction = new Place(title, url, Double.parseDouble(lng), Double.parseDouble(lat),imageId);
                        attraction.save();

                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}

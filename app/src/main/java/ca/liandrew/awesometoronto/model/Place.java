package ca.liandrew.awesometoronto.model;

import android.content.Context;

import com.longevitysoft.android.xml.plist.PListXMLHandler;
import com.longevitysoft.android.xml.plist.PListXMLParser;
import com.longevitysoft.android.xml.plist.domain.Array;
import com.longevitysoft.android.xml.plist.domain.Dict;
import com.longevitysoft.android.xml.plist.domain.PList;
import com.longevitysoft.android.xml.plist.domain.PListObject;
import com.orm.SugarRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by liandrew on 2016-05-07.
 */
public class Place extends SugarRecord {
    private String title;
    private String url;
    private double lng;
    private double lat;
    private int imageUrl;
    private boolean favorite;

    public Place(){
    }

    public Place(String title, String url, double lng, double lat, int imageUrl) {
        this.title = title;
        this.url = url;
        this.lng = lng;
        this.lat = lat;
        this.imageUrl = imageUrl;
        this.favorite = false;
    }

    public static void loadFromPlist(String plist, Context context){
        //start reading
        PListXMLParser parser = new PListXMLParser();
        PListXMLHandler handler = new PListXMLHandler();
        parser.setHandler(handler);
        try {
            parser.parse(context.getAssets().open(plist));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PList actualPList = ((PListXMLHandler) parser.getHandler()).getPlist();
        Array placesList = ((Array) actualPList.getRootElement());

        Map<String,String> placeImgList = new HashMap<String,String>();
        placeImgList.put("aquarium", "Ripley's Aquarium");
        placeImgList.put("artgalleryontario", "Art Gallery of Ontario");
        placeImgList.put("cntower", "CN Tower");
        placeImgList.put("rom", "Royal Ontario Museum");
        placeImgList.put("torontocityhall", "City Hall");
        placeImgList.put("torontoeatoncentre", "Eaton Center");
        placeImgList.put("torontozoo", "Toronto Zoo");
        placeImgList.put("yorkdalemall", "Yorkdale Mall");

        for (PListObject obj : placesList) {
            switch (obj.getType()) {
                case DICT:
                    String imgName = "ic_menu_gallery";
                    Dict dicPlace = (Dict) obj;

                    String lng = dicPlace.getConfiguration("lng").getValue();
                    String lat = dicPlace.getConfiguration("lat").getValue();
                    String title = dicPlace.getConfiguration("title").getValue();
                    String url = dicPlace.getConfiguration("url").getValue();

                    if(placeImgList.containsValue(title)){
                        Iterator placeEntries = placeImgList.entrySet().iterator();
                        while(placeEntries.hasNext()) {
                            Map.Entry entry = (Map.Entry) placeEntries.next();
                            String key = (String)entry.getKey();
                            String value = (String)entry.getValue();
                            if(title.equalsIgnoreCase(value)){
                                imgName = key;
                            }
                        }
                    }

                    int imageId = context.getResources().getIdentifier(imgName.toLowerCase(Locale.getDefault()),
                            "drawable", context.getPackageName());

                    Place attraction = new Place(title, url, Double.parseDouble(lng), Double.parseDouble(lat),imageId);
                    attraction.save();

                    break;
                default:
                    break;
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}

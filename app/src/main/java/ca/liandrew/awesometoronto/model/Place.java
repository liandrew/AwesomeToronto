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

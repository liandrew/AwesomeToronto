package ca.liandrew.awesometoronto.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

/**
 * Created by liandrew on 2016-05-08.
 */
public class Station extends SugarRecord implements Parcelable {

    int stationId;
    String stationName;
    double lat;
    double lng;
    int availableBikes;
    String statusValue;

    public Station(){
    }

    public Station(int id, String stationName, double lat, double lng, int availableBikes, String statusValue){
        this.stationId = id;
        this.stationName = stationName;
        this.lat = lat;
        this.lng = lng;
        this.availableBikes = availableBikes;
        this.statusValue = statusValue;
    }

    protected Station(Parcel in) {
        stationId = in.readInt();
        stationName = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        availableBikes = in.readInt();
        statusValue = in.readString();
    }

    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };

    public int getStationId(){
        return stationId;
    }
    public void setStationId(int id){
        this.stationId = id;
    }
    public String getStationName(){
        return stationName;
    }
    public void setStationName(String stationName){
        this.stationName = stationName;
    }
    public double getLat(){
        return this.lat;
    }
    public void setLat(double lat){
        this.lat = lat;
    }
    public double getLng(){
        return this.lng;
    }
    public void setLng(double lng){
        this.lng = lng;
    }
    public int getAvailableBikes(){
        return this.availableBikes;
    }
    public void setAvailableBikes(int availableBikes){
        this.availableBikes = availableBikes;
    }
    public String getStatusValue(){
        return this.statusValue;
    }
    public void setStatusValue(String statusValue){
        this.statusValue = statusValue;
    }

    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(stationId);
        parcel.writeString(stationName);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeInt(availableBikes);
        parcel.writeString(statusValue);
    }
}

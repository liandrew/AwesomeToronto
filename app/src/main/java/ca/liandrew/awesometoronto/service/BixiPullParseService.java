package ca.liandrew.awesometoronto.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import ca.liandrew.awesometoronto.model.Station;

/**
 * Created by liandrew on 2015-03-31.
 */
public class BixiPullParseService extends IntentService {

    private ArrayList resultList;
    private String BIXI_URL = "http://feeds.bikesharetoronto.com/stations/stations.json";

    public BixiPullParseService(){
        super("bixiService");
    }

    @Override
    protected void onHandleIntent(Intent intent){

        try {
            getBixiInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent broadcastIntent = new Intent( "BIXI_BROADCAST_ACTION" );

        // add data to the intent
        broadcastIntent.putParcelableArrayListExtra("stationList", resultList);

        // broadcast the intent
        getBaseContext().sendBroadcast(broadcastIntent);
    }

    private List<Station> getBixiInfo() throws IOException {

        InputStream in = null;
        resultList = new ArrayList<Station>();

        in = OpenHttpConnection(BIXI_URL);
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();

                if (name.equals("stationBeanList")) {
                    reader.beginArray();
                    while(reader.hasNext()) {
                        resultList.add(readMessage(reader));
                    }
                    reader.endArray();
                }else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch( Exception ex ){
            ex.printStackTrace();
        }finally{
            reader.close();
        }
        return resultList;
    }

    public Station readMessage(JsonReader reader) throws IOException {
        int id = -1;
        String stationName=null;
        double lat=-1;
        double lng=-1;
        int availableBikes=-1;
        String statusValue=null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("stationName")) {
                stationName = reader.nextString();
            } else if (name.equals("latitude") && reader.peek() != JsonToken.NULL) {
                lat = Double.parseDouble(reader.nextString());
            } else if (name.equals("longitude")) {
                lng = Double.parseDouble(reader.nextString());
            } else if(name.equals("availableBikes")) {
                availableBikes = reader.nextInt();
            } else if(name.equals("statusValue")) {
                statusValue = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Station(id, stationName, lat, lng, availableBikes, statusValue);
    }


    // open a HTTP connection
    private InputStream OpenHttpConnection( String urlString )
            throws IOException {

        InputStream in = null;
        int response = -1;

        URL url = new URL(urlString);

        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException( "Not an HTTP connection" );

        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;

            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");

            httpConn.connect();

            response = httpConn.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
            throw new IOException("Error connecting");
        }
        return in;
    }

}

package ca.liandrew.awesometoronto.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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

    private final String LOG_TAG = BixiPullParseService.class.getSimpleName();
    private ArrayList resultList;

    public BixiPullParseService(){
        super("bixiService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        resultList = getBixiInfo();

        if(resultList != null && resultList.size() > 0){
            Intent broadcastIntent = new Intent( "BIXI_BROADCAST_ACTION" );
            // add data to the intent
            broadcastIntent.putParcelableArrayListExtra("stationList", resultList);

            // broadcast the intent
            getBaseContext().sendBroadcast(broadcastIntent);
        }else{
            Log.d(LOG_TAG, "Error parsing json results");
        }
    }

    private ArrayList<Station> getBixiInfo(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String bixiJsonStr = null;
        ArrayList<Station> stationData;

        try {

            final String BIXI_BASE_URL = "http://feeds.bikesharetoronto.com/stations/stations.json";

            Uri builtUri = Uri.parse(BIXI_BASE_URL);

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // newline for friendly debugging
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // empty stream
                return null;
            }
            bixiJsonStr = buffer.toString();
            Log.v(LOG_TAG, "JSON string " + bixiJsonStr);

        }catch(IOException e){
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try{
            stationData = getStationDataFromJson(bixiJsonStr);
        }catch(JSONException e){
            Log.e(LOG_TAG, "JSON Error ", e);
            return null;
        }

        return stationData;
    }

    private ArrayList<Station> getStationDataFromJson(String stationJsonStr)
            throws JSONException {

        final String BIXI_LIST = "stationBeanList";
        final String BIXI_ID = "id";
        final String BIXI_STATION_NAME = "stationName";
        final String BIXI_LAT = "latitude";
        final String BIXI_LONG = "longitude";
        final String BIXI_AVAILABLE_BIKE = "availableBikes";
        final String BIXI_STATUS = "statusValue";

        JSONObject stationJson = new JSONObject(stationJsonStr);
        JSONArray stationArray = stationJson.getJSONArray(BIXI_LIST);
        int numStations = stationArray.length();

        ArrayList<Station> result = new ArrayList<>();

        for(int i = 0; i < numStations; i++) {
            JSONObject stationResult = stationArray.getJSONObject(i);

            Station station = new Station();
            station.setStationId(Integer.parseInt(stationResult.get(BIXI_ID).toString()));
            station.setStationName(stationResult.get(BIXI_STATION_NAME).toString());
            station.setLat(Double.parseDouble(stationResult.get(BIXI_LAT).toString()));
            station.setLng(Double.parseDouble(stationResult.get(BIXI_LONG).toString()));
            station.setAvailableBikes(Integer.parseInt(stationResult.get(BIXI_AVAILABLE_BIKE).toString()));
            station.setStatusValue(stationResult.get(BIXI_STATUS).toString());

            result.add(station);
        }

        return result;
    }

}

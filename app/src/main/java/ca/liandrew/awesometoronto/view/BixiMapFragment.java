package ca.liandrew.awesometoronto.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import ca.liandrew.awesometoronto.R;
import ca.liandrew.awesometoronto.model.Station;
import ca.liandrew.awesometoronto.service.BixiPullParseService;
import ca.liandrew.awesometoronto.service.PermissionUtils;


public class BixiMapFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    private SupportMapFragment fragment;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final LatLng MaRS = new LatLng(43.659961, -79.388318);

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private Intent intentService;
    private BroadcastReceiver intentReceiver;
    private ArrayList<Station> bixiStationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(
                R.layout.bixibike_layout,
                container, false );

        // receive the bixi bike info from the background service
        intentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intentBroadcast) {
                try {
                    bixiStationList = intentBroadcast.getParcelableArrayListExtra("stationList");

                    if(bixiStationList != null && bixiStationList.size()>0){
                        for(int i=0; i<bixiStationList.size(); i++){
                            Station station = bixiStationList.get(i);

                            if(station != null){
                                LatLng bixiCords = new LatLng(station.getLat(), station.getLng());
                                // Add a marker for the station
                                Marker stationMarker = mMap.addMarker(new MarkerOptions()
                                        .position(bixiCords)
                                        .title(station.getStationName())
                                        .snippet("Available: " + station.getAvailableBikes()));
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        startBixiBikeService();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStart() {
        super.onStart();
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if(fragment==null){
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }
    }

    public void onMapReady() {
        if (mMap == null) {
            mMap = fragment.getMap();

            // get my current location
            mMap.setOnMyLocationButtonClickListener(this);
            enableMyLocation();

            // move camera to focus on toronto
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MaRS, 13));
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getActivity().getSupportFragmentManager(), "dialog");
    }

    /* register a broadcast receiver */
    @Override
    public void onResume(){
        super.onResume();

        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }

        IntentFilter intentFilter = new IntentFilter("BIXI_BROADCAST_ACTION" );
        getActivity().registerReceiver(intentReceiver, intentFilter);

        onMapReady();
    }

    /* unregister a broadcast receiver */
    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(intentReceiver);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getActivity().stopService(intentService);
    }

    public void startBixiBikeService(){
        // delegate downloading bixi bike info to bg service
        intentService = new Intent(getContext(), BixiPullParseService.class);
        getActivity().startService(intentService);
    }

    @Override
    public void onDetach(){
        super.onDetach();

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        android.support.v7.app.ActionBar supportActionBar = activity.getSupportActionBar();
        if(supportActionBar!=null){
            // bring back the action bar when leaving the map
            supportActionBar.show();
        }
    }

}

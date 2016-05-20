package ca.liandrew.awesometoronto.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import ca.liandrew.awesometoronto.R;
import ca.liandrew.awesometoronto.firebase.FirebaseDatabase;
import ca.liandrew.awesometoronto.firebase.FirebaseFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseDatabase mFirebaseRef = FirebaseFactory.getFirebaseDatabase();
    private Fragment mFragment = null;
    private static String APP_STATE = "drawerState";
    private int mItemId = 0;
    private String mUserName="";
    private String mUserEmail="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState==null) {
            // first instance
            if(mFirebaseRef!=null){
                mUserName = mFirebaseRef.getName();
                mUserEmail = mFirebaseRef.getEmail();
            }
            // first fragment to show on app start
            mItemId = R.id.nav_attractions;
        }else{
            // restore to previous state
            Bundle drawerState = savedInstanceState.getBundle(APP_STATE);
            if(drawerState!=null){
                restoreState(drawerState);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);

        TextView name = (TextView)header.findViewById(R.id.userName);
        TextView email = (TextView)header.findViewById(R.id.userEmail);

        name.setText(mUserName);
        email.setText(mUserEmail);

        mFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData == null) {
                    mUserName = null;
                    finish();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        displayView(mItemId);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mItemId = item.getItemId();
        displayView(mItemId);
        return true;
    }

    public void displayView(int viewId) {
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_attractions:
                mFragment = new AttractionsFragment();
                title  = "Attractions";
                break;
            case R.id.nav_bixibike:
                mFragment = new BixiMapFragment();
                title = "Bixi Bikes";
                break;
            case R.id.nav_favorites:
                mFragment = new FavoritesFragment();
                title = "Favorites";
                break;
            case R.id.nav_history:
                mFragment = new HistoryFragment();
                title = "History";
                break;
        }

        if (mFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, mFragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    public void onStart(){
        super.onStart();
    }


    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(APP_STATE, saveState());
    }

    public Bundle saveState() {
        Bundle drawer = new Bundle();

        drawer.putSerializable("mFirebaseRef", mFirebaseRef);
        drawer.putInt("mItemId", mItemId);
        getSupportFragmentManager().putFragment(drawer, "mFragment", mFragment);

        return drawer;
    }

    private void restoreState(Bundle drawerState) {
        mItemId = drawerState.getInt("mItemId");
        mFirebaseRef = (FirebaseDatabase) drawerState.getSerializable("mFirebaseRef");

        if(mFirebaseRef!=null){
            mUserName = mFirebaseRef.getName();
            mUserEmail = mFirebaseRef.getEmail();
        }

        // restore fragment instance
        mFragment = getSupportFragmentManager().getFragment(drawerState, "mFragment");
    }

    public void logout(View view){
        mFirebaseRef.logout();
    }
}

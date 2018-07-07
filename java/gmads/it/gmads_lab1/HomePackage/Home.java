package gmads.it.gmads_lab1.HomePackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import gmads.it.gmads_lab1.BookPackage.AddBook;
import gmads.it.gmads_lab1.Chat.ChatList;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.Login;
import gmads.it.gmads_lab1.Map.main.MapActivity;
import gmads.it.gmads_lab1.MyLibraryPackage.MyLibrary;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.RequestPackage.RequestActivity;
import gmads.it.gmads_lab1.ToolsPackege.Tools;
import gmads.it.gmads_lab1.UserPackage.EditProfile;
import gmads.it.gmads_lab1.UserPackage.ShowProfile;
import gmads.it.gmads_lab1.HomePackage.fragments.ActionHome;
import gmads.it.gmads_lab1.HomePackage.fragments.ComedyHome;
import gmads.it.gmads_lab1.HomePackage.fragments.FictionHome;
import gmads.it.gmads_lab1.HomePackage.fragments.ThrillerHome;
import gmads.it.gmads_lab1.ToolsPackege.FragmentViewPagerAdapter;
import gmads.it.gmads_lab1.UserPackage.Profile;
import gmads.it.gmads_lab1.HomePackage.fragments.AllHome;

public class  Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    SearchView searchview;
    TextView navName;
    TextView navMail;
    ImageView navImage;
    ImageView navReqNotification;
    NavigationView navigationView;
    DrawerLayout drawer;
    private Profile profile;
    private Bitmap myProfileBitImage;
    View headerView;
    AllHome tab1= new AllHome();
    ActionHome action= new ActionHome();
    ComedyHome comedy= new ComedyHome();
    FictionHome fiction= new FictionHome();
    ThrillerHome thrillerHome= new ThrillerHome();
    Tools tools;
    ProgressBar progressbar;
    ViewPager pager;
    CollapsingToolbarLayout collapsingtb;
    AppBarLayout appbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        tools = new Tools();
        appbarLayout = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        setNavViews();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        initCollapsingToolbar();

        pager= findViewById(R.id.viewPager);
        FragmentViewPagerAdapter vpadapter= new FragmentViewPagerAdapter(getSupportFragmentManager());
        vpadapter.addFragment(tab1);
        vpadapter.addFragment(action);
        vpadapter.addFragment(comedy);
        vpadapter.addFragment(thrillerHome);
        vpadapter.addFragment(fiction);
        pager.setAdapter(vpadapter);
        TabLayout tableLayout =findViewById(R.id.tabs);
        tableLayout.setupWithViewPager(pager);
        Objects.requireNonNull(tableLayout.getTabAt(0)).setText(getString(R.string.tab1));
        Objects.requireNonNull(tableLayout.getTabAt(1)).setText("action");
        Objects.requireNonNull(tableLayout.getTabAt(2)).setText("comedy");
        Objects.requireNonNull(tableLayout.getTabAt(3)).setText("thriller");
        Objects.requireNonNull(tableLayout.getTabAt(4)).setText("fiction");
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {

            }

            @Override
            public void onPageSelected( int position ) {
                    switch (position){
                        case 0:
                            tab1.clearlist();
                            tab1.setText(searchview.getQuery().toString());
                            tab1.setProfile(profile);
                            tab1.fetchdata();
                            tab1.setNpage(tab1.getNpage()+1);
                            break;
                        case 1:
                            action.clearlist();
                            action.setText(searchview.getQuery().toString());
                            action.setProfile(profile);
                            action.fetchdata();
                            action.setNpage(action.getNpage()+1);
                            break;
                        case 2:
                            comedy.clearlist();
                            comedy.setText(searchview.getQuery().toString());
                            comedy.setProfile(profile);
                            comedy.fetchdata();
                            comedy.setNpage(action.getNpage()+1);
                            break;
                        case 3:
                            thrillerHome.clearlist();
                            thrillerHome.setText(searchview.getQuery().toString());
                            thrillerHome.setProfile(profile);
                            thrillerHome.fetchdata();
                            thrillerHome.setNpage(thrillerHome.getNpage()+1);
                            break;
                        case 4:
                            fiction.clearlist();
                            fiction.setText(searchview.getQuery().toString());
                            fiction.setProfile(profile);
                            fiction.fetchdata();
                            fiction.setNpage(fiction.getNpage()+1);
                            break;
                        default:
                            break;

                    }
            }

            @Override
            public void onPageScrollStateChanged( int state ) {

            }
        });
        progressbar = findViewById(R.id.progress_bar);
//
        //era per mettere foto libri nell appbar, ma l'abbiamo messa come sfondo per ora
        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        getUserInfo();

    }

    @Override
    protected void onResume() {
        ImageView notfound = findViewById(R.id.not_found);
        TextView tnf = findViewById(R.id.textnotfound);
        super.onResume();
        progressbar.setVisibility(View.GONE);
        notfound.setVisibility(View.GONE);
        tnf.setVisibility(View.GONE);
        if(searchview!=null){
        tab1.setText(searchview.getQuery().toString());
        }
        else{
            tab1.setText("");
        }
        getUserInfo();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_showProfile) {
            Intent intentMod = new Intent(this, ShowProfile.class);
            startActivity(intentMod);
            finish();
            return true;
        } else if (id == R.id.nav_addBook) {
            Intent intentMod = new Intent(this, AddBook.class);
            startActivity(intentMod);
            finish();
            return true;
        } else if (id == R.id.nav_home) {
            //deve solo chiudersi la navbar
            drawer.closeDrawers();
            return true;
        } else if (id == R.id.nav_requests) {
            Intent intent = new Intent(this, RequestActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_chat){
            Intent intent = new Intent(this, ChatList.class);
            startActivity(intent);
            return true;
        } else if(id == R.id.nav_logout){
            AuthUI.getInstance().signOut(this).addOnCompleteListener(v->{
                startActivity(new Intent(this,Login.class));
                finish();
            });
            return true;
        }else if(id == R.id.nav_mylibrary){
            startActivity(new Intent(this,MyLibrary.class));
            finish();
            return true;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClickNotify(View view){
        Intent intentMod = new Intent(getApplicationContext(), RequestActivity.class);
        startActivity(intentMod);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem m= menu.findItem(R.id.search);

        searchview = (android.widget.SearchView)m.getActionView();
        searchview.setIconified(false);
        searchview.setFocusable(true);
        m.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                item.getActionView().requestFocus();
                collapseToolbar();
                ((InputMethodManager) Objects.requireNonNull(getSystemService(Context.INPUT_METHOD_SERVICE))).toggleSoftInput(0, 0);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                expandToolbar();
                ((InputMethodManager) Objects.requireNonNull(getSystemService(Context.INPUT_METHOD_SERVICE))).hideSoftInputFromWindow(item.getActionView().getWindowToken(), 0);
                return true;
            }
        });

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit( String text ) {
                switch (pager.getCurrentItem()){
                    case 0:
                        tab1.clearlist();
                        tab1.setText(searchview.getQuery().toString());
                        tab1.setProfile(profile);
                        tab1.fetchdata();
                        tab1.setNpage(tab1.getNpage()+1);
                        break;
                    case 1:
                        action.clearlist();
                        action.setText(searchview.getQuery().toString());
                        action.setProfile(profile);
                        action.fetchdata();
                        action.setNpage(action.getNpage()+1);
                        break;
                    case 2:
                        comedy.clearlist();
                        comedy.setText(searchview.getQuery().toString());
                        comedy.setProfile(profile);
                        comedy.fetchdata();
                        comedy.setNpage(action.getNpage()+1);
                        break;
                    case 3:
                        thrillerHome.clearlist();
                        thrillerHome.setText(searchview.getQuery().toString());
                        thrillerHome.setProfile(profile);
                        thrillerHome.fetchdata();
                        thrillerHome.setNpage(thrillerHome.getNpage()+1);
                        break;
                    case 4:
                        fiction.clearlist();
                        fiction.setText(searchview.getQuery().toString());
                        fiction.setProfile(profile);
                        fiction.fetchdata();
                        fiction.setNpage(fiction.getNpage()+1);
                        break;
                    default:
                        break;

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange( String newText ) {
                switch (pager.getCurrentItem()){
                    case 0:
                        tab1.clearlist();
                        tab1.setText(searchview.getQuery().toString());
                        tab1.setProfile(profile);
                        tab1.fetchdata();
                        tab1.setNpage(tab1.getNpage()+1);
                        break;
                    case 1:
                        action.clearlist();
                        action.setText(searchview.getQuery().toString());
                        action.setProfile(profile);
                        action.fetchdata();
                        action.setNpage(action.getNpage()+1);
                        break;
                    case 2:
                        comedy.clearlist();
                        comedy.setText(searchview.getQuery().toString());
                        comedy.setProfile(profile);
                        comedy.fetchdata();
                        comedy.setNpage(action.getNpage()+1);
                        break;
                    case 3:
                        thrillerHome.clearlist();
                        thrillerHome.setText(searchview.getQuery().toString());
                        thrillerHome.setProfile(profile);
                        thrillerHome.fetchdata();
                        thrillerHome.setNpage(thrillerHome.getNpage()+1);
                        break;
                    case 4:
                        fiction.clearlist();
                        fiction.setText(searchview.getQuery().toString());
                        fiction.setProfile(profile);
                        fiction.fetchdata();
                        fiction.setNpage(fiction.getNpage()+1);
                        break;
                    default:
                        break;

                }
                return true;
            }
        });
        return true;
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Home");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle("Home");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle("Home");
                    isShow = false;
                }
            }
        });
    }

    public void mapcreate( View view ) {
        if(tab1.getAdapter().getItemCount()>0) {
            Intent intentMod = new Intent(this, MapActivity.class);
            intentMod.putExtra("query", tab1.getText());
            intentMod.putExtra("lat", profile.getLat());
            intentMod.putExtra("lng", profile.getLng());
            intentMod.putExtra("numpages", tab1.getNpage());
            startActivity(intentMod);
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        }
    }

    public void setNavViews(){
        drawer =  findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        navName =  headerView.findViewById(R.id.navName);
        navMail =  headerView.findViewById(R.id.navMail);
        navImage =  headerView.findViewById(R.id.navImage);
        //navReqNotification = headerView.findViewById(R.id.req_notification);
        headerView.setBackgroundResource(R.color.colorPrimaryDark);

        if(profile!=null) {
            navName.setText(profile.getName());
            navMail.setText(profile.getEmail());
            if (myProfileBitImage != null) {
                navImage.setImageBitmap(myProfileBitImage);
            } else {
                navImage.setImageDrawable(getDrawable(R.drawable.default_picture));
            }
        }
    }

    private void getUserInfo(){
        progressbar.setVisibility(View.VISIBLE);
        FirebaseManagement.getDatabase().getReference().child("users").child(FirebaseManagement.getUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        profile = dataSnapshot.getValue(Profile.class);
                        if (profile != null) {
                            if(profile.getCAP()==null || profile.getCAP().length()==0){
                                Intent i=new Intent(getApplicationContext(), EditProfile.class);
                                startActivity(i);
                            }
                            navName.setText(profile.getName());
                            navMail.setText(profile.getEmail());
                            if(profile.isReqNotified()){
                                //per ricezione notifiche
                                Menu menu = navigationView.getMenu();
                                MenuItem item = menu.getItem(4);
                                item.setIcon(R.drawable.ic_round_notification_important_24px);
                                //
                                //navReqNotification.setVisibility(View.VISIBLE);
                            } else {
                                Menu menu = navigationView.getMenu();
                                MenuItem item = menu.getItem(4);
                                //item.setIcon(R.drawable.ic_round_notifications_24px);
                                item.setIcon(R.drawable.ic_round_notifications_24px);
                                //navReqNotification.setVisibility(View.GONE);
                            }

                            if (profile.getImage() != null) {
                                try {
                                    File localFile = File.createTempFile("images", "jpg");
                                    StorageReference profileImageRef =
                                            FirebaseManagement
                                                    .getStorage()
                                                    .getReference()
                                                    .child("users")
                                                    .child(FirebaseManagement.getUser().getUid())
                                                    .child("profileimage.jpg");

                                    profileImageRef.getFile(localFile)
                                            .addOnSuccessListener(taskSnapshot -> navImage.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath())))
                                            .addOnFailureListener(e -> Log.d("ERROR",e.toString()));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                navImage.setImageDrawable(getDrawable(R.drawable.default_picture));
                            }

                            getStartingHomeBooks();
                        }else{
                            Intent i=new Intent(getApplicationContext(), EditProfile.class);
                            startActivity(i);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String activity;
        Intent intent = getIntent();
        if((activity = intent.getStringExtra("activity")) != null){
            if(activity.equals("login")){
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        }
    }

    private void getStartingHomeBooks(){

        progressbar.setVisibility(View.VISIBLE);
        if(tools.isOnline(getApplicationContext())) {
            if(searchview!= null){
            tab1.setText(searchview.getQuery().toString());
            }else{
                tab1.setText("");

            }
            tab1.setProfile(profile);
            tab1.fetchdata();
            tab1.setNpage(tab1.getNpage()+1);
        } else {
            android.app.AlertDialog.Builder ad = tools.showPopup(this, getString(R.string.noInternet), "", "");
            ad.setPositiveButton(getString(R.string.retry), (vi, w) -> onStart());
            ad.setCancelable(false);
            ad.show();
        }
    }
    public void collapseToolbar(){
        appbarLayout.setExpanded(false);
    }

    public void expandToolbar(){
        appbarLayout.setExpanded(true);
    }


}




package gmads.it.gmads_lab1.MyLibraryPackage;

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
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
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
import gmads.it.gmads_lab1.HomePackage.Home;
import gmads.it.gmads_lab1.MyLibraryPackage.MyLibraryFragments.LibraryLanded;
import gmads.it.gmads_lab1.MyLibraryPackage.MyLibraryFragments.LibraryMines;
import gmads.it.gmads_lab1.MyLibraryPackage.MyLibraryFragments.LibraryRented;
import gmads.it.gmads_lab1.Login;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.RequestPackage.RequestActivity;
import gmads.it.gmads_lab1.ToolsPackege.FragmentViewPagerAdapter;
import gmads.it.gmads_lab1.ToolsPackege.Tools;
import gmads.it.gmads_lab1.UserPackage.EditProfile;
import gmads.it.gmads_lab1.UserPackage.ShowProfile;
import gmads.it.gmads_lab1.UserPackage.Profile;

public class MyLibrary extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    SearchView searchview;
    Client algoClient;
    Index algoIndex;
    TextView navName;
    TextView navMail;
    ImageView navImage;
    ImageView navReqNotification;
    String query="";
    NavigationView navigationView;
    DrawerLayout drawer;
    private Profile profile;
    private Bitmap myProfileBitImage;
    View headerView;
    Tools tools;
    ProgressBar progressbar;

    LibraryRented libraryRented = new LibraryRented();
    LibraryLanded libraryLanded = new LibraryLanded();
    LibraryMines libraryMines = new LibraryMines();
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylibrary);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tools = new Tools();

        progressbar = findViewById(R.id.progress_bar);
        pager = findViewById(R.id.viewPager);

        setNavViews();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        initCollapsingToolbar();
        ViewPager pager= findViewById(R.id.viewPager);
        FragmentViewPagerAdapter vpadapter= new FragmentViewPagerAdapter(getSupportFragmentManager());

        vpadapter.addFragment(libraryMines);
        vpadapter.addFragment(libraryLanded);
        vpadapter.addFragment(libraryRented);

        pager.setAdapter(vpadapter);
        TabLayout tableLayout =findViewById(R.id.tabsl);
        tableLayout.setupWithViewPager(pager);

        Objects.requireNonNull(tableLayout.getTabAt(0)).setText(getString(R.string.MyBooks));
        Objects.requireNonNull(tableLayout.getTabAt(1)).setText(getString(R.string.LentBooks));
        Objects.requireNonNull(tableLayout.getTabAt(2)).setText(getString(R.string.RentedBooks));

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {

            }

            @Override
            public void onPageSelected( int position ) {
                switch (position){
                    case 0:
                        libraryMines.clearlist();
                        //libraryMines.setText(searchview.getQuery().toString());
                        libraryMines.setProfile(profile);
                        libraryMines.fetchdata();
                        libraryMines.setNpage(libraryMines.getNpage()+1);
                        break;
                    case 1:
                        libraryLanded.clearlist();
                        //libraryLanded.setText(searchview.getQuery().toString());
                        libraryLanded.setProfile(profile);
                        libraryLanded.fetchdata();
                        libraryLanded.setNpage(libraryLanded.getNpage()+1);
                        break;
                    case 2:
                        libraryRented.clearlist();
                        //libraryRented.setText(searchview.getQuery().toString());
                        libraryRented.setProfile(profile);
                        libraryRented.fetchdata();
                        libraryRented.setNpage(libraryRented.getNpage()+1);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged( int state ) {

            }
        });
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
    public void onBackPressed() {
        Intent i = new Intent(this, Home.class);
        startActivity(i);
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
            Intent intentMod = new Intent(this, Home.class);
            startActivity(intentMod);
            finish();
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
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_empty, menu);

        return true;
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("My Library");
        AppBarLayout appBarLayout =  findViewById(R.id.appbar);
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
                    collapsingToolbar.setTitle("My Library");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle("My Library ");
                    isShow = false;
                }
            }
        });
    }

    /*

    public void mapcreate( View view ) {
        Intent intentMod = new Intent(this, MapActivity.class);
        intentMod.putExtra("query",query);
        intentMod.putExtra("lat",profile.getLat());
        intentMod.putExtra("lng",profile.getLng());
        startActivity(intentMod);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

    }
    */

    public void setNavViews(){
        drawer =  findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        navName =  headerView.findViewById(R.id.navName);
        navMail =  headerView.findViewById(R.id.navMail);
        navImage =  headerView.findViewById(R.id.navImage);
        navReqNotification = headerView.findViewById(R.id.req_notification);
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

        if(tools.isOnline(getApplicationContext())) {

            algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
            algoIndex = algoClient.getIndex("BookIndex");

            FirebaseManagement.getDatabase().getReference().child("users").child(FirebaseManagement.getUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            profile = dataSnapshot.getValue(Profile.class);
                            if (profile != null) {
                                if (profile.getCAP() == null || profile.getCAP().length() == 0) {
                                    Intent i = new Intent(getApplicationContext(), EditProfile.class);
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
                                                .addOnSuccessListener(taskSnapshot -> navImage.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath()))).addOnFailureListener(e -> {
                                        });

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    navImage.setImageDrawable(getDrawable(R.drawable.default_picture));
                                }

                                getStartingMyBooks();
                            } else {
                                Intent i = new Intent(getApplicationContext(), EditProfile.class);
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                        }
                    });

        } else {
            android.app.AlertDialog.Builder ad = tools.showPopup(this, getString(R.string.noInternet), "", "");
            ad.setPositiveButton(getString(R.string.retry), (vi, w) -> onStart());
            ad.setCancelable(false);
            ad.show();
        }

    }

    private void getStartingMyBooks(){

        progressbar.setVisibility(View.VISIBLE);

        if(tools.isOnline(getApplicationContext())) {
           /*
            if(searchview!= null){
                libraryMines.setText(searchview.getQuery().toString());
            }else{
                libraryMines.setText("");

            }
            */
            libraryMines.setProfile(profile);
            libraryMines.fetchdata();
            libraryMines.setNpage(libraryMines.getNpage()+1);
        } else {
            android.app.AlertDialog.Builder ad = tools.showPopup(this, getString(R.string.noInternet), "", "");
            ad.setPositiveButton(getString(R.string.retry), (vi, w) -> onStart());
            ad.setCancelable(false);
            ad.show();
        }
    }

    public void onClickNotify(View view){
        Intent intentMod = new Intent(getApplicationContext(), RequestActivity.class);
        startActivity(intentMod);
        //finish();
    }

}



package gmads.it.gmads_lab1.UserPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.design.widget.AppBarLayout;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gmads.it.gmads_lab1.BookPackage.AddBook;
import gmads.it.gmads_lab1.Chat.ChatList;
import gmads.it.gmads_lab1.Chat.glide.GlideApp;
import gmads.it.gmads_lab1.FirebasePackage.Datasource;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.HomePackage.Home;
import gmads.it.gmads_lab1.Login;
import gmads.it.gmads_lab1.MyLibraryPackage.MyLibrary;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.RequestPackage.RequestActivity;
import gmads.it.gmads_lab1.ToolsPackege.Tools;
import gmads.it.gmads_lab1.ReviewPackage.Review;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ShowProfile extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    Tools tools;
    Context context;
    private TextView downloaded;
    private AppBarLayout appbar;
    private ImageView coverImage;
    private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
    private TextView textviewTitle;
    private TextView uploaded;
    TextView navName;
    TextView navMail;
    ImageView navImage;
    ImageView navReqNotification;
    NavigationView navigationView;
    DrawerLayout drawer;
    //view della attività
    ImageView avatar;
    ProgressBar progressbar;
    View headerView;
    TextView vName;
    TextView vEmail;
    TextView vBio;
    TextView total;
    TextView cap;
    TextView count;
    List<Review> reviews = new ArrayList<>();
    private Profile profile;
    private Bitmap myProfileBitImage;
    LinearLayout ll_parent;
    RatingBar rating;

    private void findViews() {
        downloaded=findViewById(R.id.downloaded);
        total=findViewById(R.id.totbooks);
        uploaded= findViewById(R.id.uploaded);
        appbar = findViewById(R.id.appbar);
        coverImage = findViewById(R.id.imageview_placeholder);
        linearlayoutTitle = findViewById(R.id.linearlayout_title);
        toolbar = findViewById(R.id.toolbar);
        textviewTitle =  findViewById(R.id.name_surname_tbar);
        rating= findViewById(R.id.rating);
        avatar = findViewById(R.id.avatar);
        ll_parent = findViewById(R.id.parent);
        count = findViewById(R.id.count);
    }

    public void setActViews(){
        cap=findViewById(R.id.cap);
        toolbar =  findViewById(R.id.toolbar);
        vName = findViewById(R.id.name_surname);
        vEmail = findViewById(R.id.email);
        vBio = findViewById(R.id.bio);
        setSupportActionBar(toolbar);
        if(profile!=null) {
            vName.setText(profile.getName());
            vEmail.setText(profile.getEmail());
            vBio.setText(profile.getDescription());
            //nome cognome nella toolbar
            textviewTitle.setText(profile.getName());

            if (myProfileBitImage != null) {
                avatar.setImageBitmap(myProfileBitImage);
            } else {
                avatar.setImageDrawable(getDrawable(R.drawable.default_picture));
            }
        }

    }
    public void setNavViews(){
        drawer =  findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        headerView = navigationView.getHeaderView(0);
        navName =  headerView.findViewById(R.id.navName);
        navMail =  headerView.findViewById(R.id.navMail);
        navImage =  headerView.findViewById(R.id.navImage);
        navReqNotification = headerView.findViewById(R.id.req_notification);
        headerView.setBackgroundResource(R.color.colorPrimaryDark);

        if(profile!=null) {
            navName.setText(profile.getName());
            navMail.setText(profile.getEmail());

            if ( profile!= null) {
                navImage.setImageBitmap(myProfileBitImage);
            } else {
                navImage.setImageDrawable(getDrawable(R.drawable.default_picture));
            }
        }
    }

    protected void onStart(){
        super.onStart();
        getUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        myProfileBitImage = Datasource.getInstance().getMyProfileBitImage();
        //set avatar and cover
        findViews();
        setActViews();
        setNavViews();
        avatar.setImageResource(R.drawable.default_picture);
        coverImage.setImageResource(R.drawable.cover);
        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        tools = new Tools();
        context = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_showp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentMod = new Intent(this, EditProfile.class);
        startActivity(intentMod);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        return true;
    }

    //to close the application on back button
    @Override
    public void onBackPressed() {
        Intent intentMod = new Intent(this, Home.class);
        startActivity(intentMod);
        finish();
        //moveTaskToBack(true);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    //@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_showProfile) {
            //deve solo chiudersi la navbar
            drawer.closeDrawers();
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
        }else if(id == R.id.nav_mylibrary){
            startActivity(new Intent(this,MyLibrary.class));
            finish();

            return true;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;
        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void getUserInfo(){
        navImage.setImageDrawable(getDrawable(R.drawable.default_picture));
        if(tools.isOnline(getApplicationContext())) {

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
                                cap.setText(profile.getCAP());
                                textviewTitle.setText(profile.getName());
                                startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);
                                vName.setText(profile.getName());
                                navName.setText(profile.getName());
                                vEmail.setText(profile.getEmail());
                                navMail.setText(profile.getEmail());
                                vBio.setText(profile.getDescription());
                                //setto il pallino notifiche su alarm
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

                                //commenti in card2
                                ll_parent.removeAllViews();
                                String s = "( " + profile.getReviews().size() + " )";
                                count.setText(s);
                                float average= averageReviews(profile.getReviews());
                                rating.setRating(average);
                                int c = 0;
                                for(Review r: profile.getReviews()){
                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.card_recensioni, null);
                                    ll_parent.addView(rowView, ll_parent.getChildCount());
                                    RelativeLayout rl;
                                    rl = (RelativeLayout) ll_parent.getChildAt(ll_parent.getChildCount()-1);
                                    if(c == 0){
                                        View v = (View) rl.getChildAt(0);
                                        v.setVisibility(View.GONE);
                                    }
                                    c++;
                                    TextView name = (TextView) rl.getChildAt(1);
                                    TextView rate = (TextView) rl.getChildAt(2);
                                    TextView comment = (TextView) rl.getChildAt(3);
                                    name.setText(r.getUser());
                                    rate.setText(String.valueOf(r.getRate()));
                                    if(r.getComment().isEmpty() || r.getComment()==null) {
                                        comment.setVisibility(View.GONE);
                                    }
                                    else{
                                        comment.setText(r.getComment());
                                    }
                                }
                                //
                                downloaded.setText(String.valueOf(profile.getLent()));
                                if (profile.hasUploaded()) {
                                    uploaded.setText(String.valueOf(profile.takennBooks()));
                                    total.setText(String.valueOf(profile.takennBooks()+profile.getLent()));
                                } else {
                                    uploaded.setText("0");
                                    total.setText("0");
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

                                        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                GlideApp.with(context)
                                                        .load(uri.toString())
                                                        .apply(bitmapTransform(new BlurTransformation(25, 3)))
                                                        .into(coverImage);
                                            }
                                        });

                                        profileImageRef.getFile(localFile)
                                                .addOnSuccessListener(taskSnapshot -> {
                                                    //progressbar.setVisibility(View.GONE);
                                                    avatar.setVisibility(View.VISIBLE);
                                                    avatar.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath()));
                                                    navImage.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath()));
                                                }).addOnFailureListener(e -> {
                                            //progressbar.setVisibility(View.GONE);
                                            avatar.setVisibility(View.VISIBLE);


                                        });

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    //progressbar.setVisibility(View.GONE);
                                    avatar.setVisibility(View.VISIBLE);
                                    navImage.setImageDrawable(getDrawable(R.drawable.default_picture));
                                }
                            } else {
                                Intent i = new Intent(getApplicationContext(), EditProfile.class);
                                startActivity(i);
                                vName.setText(getString(R.string.name));
                                //vName.append(" " + getString(R.string.surname));
                                navName.setText(getString(R.string.name));
                                //navName.append(" " + getString(R.string.surname));
                                vEmail.setText(getString(R.string.email));
                                navMail.setText(getString(R.string.email));
                                vBio.setText(getString(R.string.description));
                                //progressbar.setVisibility(View.GONE);
                                //avatar.setVisibility(View.VISIBLE);
                                navImage.setImageDrawable(getDrawable(R.drawable.default_picture));
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

    public float averageReviews(List<Review> reviews){
            float result;
            int n=0;
            float tot=0;
            for(Review r : reviews){
                n++;
                tot=tot+r.getRate();
            }
            result=tot/n;
            return result;
    }
    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    public void onClickNotify(View view){
        Intent intentMod = new Intent(getApplicationContext(), RequestActivity.class);
        startActivity(intentMod);
        //finish();
    }

}
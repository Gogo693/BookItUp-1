package gmads.it.gmads_lab1.UserPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import gmads.it.gmads_lab1.Chat.ChatActivity;
import gmads.it.gmads_lab1.Chat.glide.GlideApp;
import gmads.it.gmads_lab1.constants.AppConstants;
import gmads.it.gmads_lab1.FirebasePackage.Datasource;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.ToolsPackege.Tools;
import gmads.it.gmads_lab1.ReviewPackage.Review;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ShowUserProfile extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AppBarLayout appbar;
    private ImageView coverImage;
    private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
    private TextView textviewTitle;
    private TextView uploaded;

    //view della attività
    ImageView avatar;
    ProgressBar progressbar;
    TextView vName;
    TextView vBio;
    TextView total;
    TextView cap;
    private Profile profile;
    private Bitmap myProfileBitImage;
    Tools tools;
    Context context;
    LinearLayout ll_parent;
    RatingBar rating;
    TextView count;

    private void findViews() {
        total=findViewById(R.id.totbooks);
        uploaded= findViewById(R.id.uploaded);
        appbar =  findViewById(R.id.appbar);
        coverImage = findViewById(R.id.imageview_placeholder);
        linearlayoutTitle = findViewById(R.id.linearlayout_title);
        toolbar = findViewById(R.id.toolbar);
        textviewTitle = findViewById(R.id.name_surname_tbar);
        avatar = findViewById(R.id.avatar);
        ll_parent = findViewById(R.id.parent);
        rating= findViewById(R.id.rating);
        count = findViewById(R.id.count);
    }

    public void setActViews(){
        cap=findViewById(R.id.cap);
        toolbar =  findViewById(R.id.toolbar);
        vName = findViewById(R.id.name_surname);
        vBio = findViewById(R.id.bio);
        setSupportActionBar(toolbar);

        if(profile!=null) {
            vName.setText(profile.getName());
            vBio.setText(profile.getDescription());
            //nome cognome nella toolbar
            //textviewTitle.setText(profile.getName());
            //textviewTitle.append(" "+ profile.getSurname());

            if (myProfileBitImage != null) {
                avatar.setImageBitmap(myProfileBitImage);
            } else {
                avatar.setImageDrawable(getDrawable(R.drawable.default_picture));
            }
        }

        //progressbar.setVisibility(View.GONE);
        //avatar.setVisibility(View.VISIBLE);
    }

    protected void onStart(){
        super.onStart();
        String userId = getIntent().getStringExtra("userId");
        getUserInfo(userId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userId = getIntent().getStringExtra("userId");
        getUserInfo(userId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fresco.initialize(this);
        setContentView(R.layout.activity_show_p_others);
        profile = Datasource.getInstance().getMyProfile();
        myProfileBitImage = Datasource.getInstance().getMyProfileBitImage();
        //set avatar and cover
        findViews();
        setActViews();
        avatar.setImageResource(R.drawable.default_picture);
        coverImage.setImageResource(R.drawable.cover);
        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);
        setSupportActionBar(toolbar);
        startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tools = new Tools();
        context = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_showp_others, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra(AppConstants.USER_NAME, profile.name);
                intent.putExtra(AppConstants.USER_ID, profile.id);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                //FINISH???
                break;
        }
        //Intent intentMod = new Intent(this, Home.class);
        //startActivity(intentMod);
        return true;
        /*
            settare animazione???
         */
        //overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    //to close the application on back button
    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
        /*
        Intent intentMod = new Intent(this, Home.class);
        startActivity(intentMod);
        finish();
        */
        //moveTaskToBack(true);
    }

    private void getUserInfo(String userId) {
        //progressbar.setVisibility(View.VISIBLE);
        //avatar.setVisibility(View.GONE);
        if (tools.isOnline(getApplicationContext())) {
            FirebaseManagement.getDatabase().getReference().child("users").child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            profile = dataSnapshot.getValue(Profile.class);
                            if (profile != null) {
                                cap.setText(profile.getCAP());
                                vName.setText(profile.getName());
                                vBio.setText(profile.getDescription());
                                textviewTitle.setText(profile.getName());
                                if (profile.hasUploaded()) {
                                    uploaded.setText(String.valueOf(profile.takennBooks()));
                                    total.setText(String.valueOf(profile.takennBooks()));
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
                                                        .child(userId)
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
                                                }).addOnFailureListener(e -> {
                                            //progressbar.setVisibility(View.GONE);
                                            avatar.setVisibility(View.VISIBLE);
                                        });
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
                                            else {
                                                comment.setText(r.getComment());
                                            }
                                        }
                                        //

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    avatar.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Intent i = new Intent(getApplicationContext(), EditProfile.class);
                                startActivity(i);
                                vName.setText(getString(R.string.name));
                                vBio.setText(getString(R.string.description));
                                textviewTitle.setText(getString(R.string.name));
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

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
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
}
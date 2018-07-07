package gmads.it.gmads_lab1.BookPackage;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import android.view.animation.AlphaAnimation;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import gmads.it.gmads_lab1.ReviewPackage.AddReview;
import gmads.it.gmads_lab1.ToolsPackege.ViewPagerAdapter;
import gmads.it.gmads_lab1.UserPackage.Profile;
import gmads.it.gmads_lab1.UserPackage.Profile;
import gmads.it.gmads_lab1.UserPackage.ShowUserProfile;
import gmads.it.gmads_lab1.constants.AppConstants;
import gmads.it.gmads_lab1.Chat.glide.GlideApp;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.RequestPackage.SearchRequestsJsonParser;
import gmads.it.gmads_lab1.ToolsPackege.Tools;
import gmads.it.gmads_lab1.RequestPackage.Request;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ShowBook extends AppCompatActivity implements OnMapReadyCallback /*implements AppBarLayout.OnOffsetChangedListener*/{

    //private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    //private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    //private static final int ALPHA_ANIMATIONS_DURATION = 200;
    //private boolean mIsTheTitleVisible = false;
    //private boolean mIsTheTitleContainerVisible = true;
   // private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
   // private TextView textviewTitle;
    //private SimpleDraweeView avatar;
    private ImageView avatar;
    static final int MY_CAMERA_REQUEST_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1888;
    private boolean isMyBook;
    private List<String> booksRequested;
    // roba di algolia
    private Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
    private Index algoIndex = algoClient.getIndex("requests");
    private Index algoBookIndex = algoClient.getIndex("BookIndex");
    private Gson gson = new Gson();
    ContextWrapper cw;
    Book book;
    String bookId;
    TextView vTitle;
    TextView vAuthor;
    TextView vdate;
    TextView vCategory;
    TextView vOwner;
    TextView vDescription;
    TextView vNotes;
    TextView vCondition;
    TextView Veditor;
    Tools tools;
    CardView card2;
    TextView titleDescr;
    TextView titleData;
    TextView titleNote;
    TextView titleImg;
    TextView titleConditions;
    ImageView bookPhoto;
    ImageView bookBackground;
    Button bReserveOrReturn;
    ProgressBar progressBar;
    ProgressBar progressBarRequest;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    LinearLayout vOwnerInfo;
    TextView vOwnerName;
    ImageView vOwnerImage;
    TextView vOwnerRating;
    List<String> images=new ArrayList<>();
    GoogleMap mmap;
    View vMap;

    private void findViews() {
        viewPager= findViewById(R.id.viewPager);
        card2 = findViewById(R.id.card2);
        toolbar =  findViewById(R.id.toolbar);
        avatar = findViewById(R.id.avatar);
        avatar.setImageDrawable(getDrawable(R.drawable.default_book));
        vTitle = findViewById(R.id.title);
        Veditor=findViewById(R.id.editore);
        vAuthor = findViewById(R.id.autore);
        vCategory = findViewById(R.id.categorie);
        vdate=findViewById(R.id.data);
        vCondition=findViewById(R.id.condizioni);
        vOwner = findViewById(R.id.owner);
        vDescription = findViewById(R.id.descrizione);
        vNotes= findViewById(R.id.notes);
        titleData = findViewById(R.id.bp_data);
        titleConditions = findViewById(R.id.tv2);
        titleDescr = findViewById(R.id.bp_descr);
        titleNote = findViewById(R.id.tv4);
        titleImg = findViewById(R.id.photoTitle);
        bookPhoto = findViewById(R.id.photoBook);
        bookBackground = findViewById(R.id.avatar_background);
        bReserveOrReturn = findViewById(R.id.reserveOrReturn);
        progressBar = findViewById(R.id.progress_bar);
        progressBarRequest = findViewById(R.id.progress_bar_request);

        vOwnerInfo = findViewById(R.id.owner_info);
        vOwnerName = findViewById(R.id.owner_name);
        vOwnerImage = findViewById(R.id.owner_image);
        vOwnerRating = findViewById(R.id.owner_rating);
        vMap = findViewById(R.id.map);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showbook);
        findViews();

        GlideApp.with(getApplicationContext())
                .load(R.drawable.default_book)
                .into(avatar);
        toolbar.setTitle(R.string.showBook);

        GlideApp.with(this)
                .load(R.drawable.default_picture)
                .into(vOwnerImage);

        setSupportActionBar(toolbar);
        adapter= new ViewPagerAdapter(ShowBook.this,images);
        viewPager.setAdapter(adapter);
        //set avatar and cover
        avatar.setImageResource(R.drawable.default_book);
        cw = new ContextWrapper(getApplicationContext());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //inizialize  user data
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        bReserveOrReturn.setOnClickListener(this::onReserveOrReturnClick);
        tools = new Tools();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupUI(findViewById(R.id.root));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //getBookInfo();
    }

    private void setFocusOnClick(View v){
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            }

        }
    }
/*
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
*/
    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    //animation back arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
            default://caso Save

        }
        return super.onOptionsItemSelected(item);
    }
    //animation back button
    @Override
    public void onBackPressed() {

        if(viewPager.getVisibility()==View.VISIBLE){
            viewPager.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
    }

    private void getIsMyBook(){
        if(book.getOwner().equals(FirebaseManagement.getUser().getUid())){
            isMyBook = true;
            vMap.setVisibility(View.GONE);
            bReserveOrReturn.setText(R.string.returnBook);
            if(book.getStato() == AppConstants.NOT_AVAILABLE){
                bReserveOrReturn.setVisibility(View.VISIBLE);
                bReserveOrReturn.setEnabled(true);
            } else {
                bReserveOrReturn.setVisibility(View.GONE);
                bReserveOrReturn.setEnabled(false);
            }

        } else {
            isMyBook = false;
            booksRequested = new LinkedList<>();
            //booksRequested.add(book.getBId());
            bReserveOrReturn.setText(R.string.reserve);
            getIsReservedByMe();
            /*
            if(book.getStato() == AppConstants.AVAILABLE){
                bReserveOrReturn.setVisibility(View.VISIBLE);
                bReserveOrReturn.setEnabled(true);
            } else {
                bReserveOrReturn.setVisibility(View.GONE);
                bReserveOrReturn.setEnabled(false);

            }*/

            FirebaseManagement.getDatabase().getReference()
                    .child("users")
                    .child(book.getOwner())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Profile profile = dataSnapshot.getValue(Profile.class);

                            if(profile != null) {

                                FirebaseManagement.getStorage().getReference()
                                        .child("users")
                                        .child(book.getOwner())
                                        .child("profileimage.jpg")
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                GlideApp.with(cw)
                                                        .load(profile.getImage())
                                                        .into(vOwnerImage);
                                            }

                                        });

                                Intent intent = new Intent(cw, ShowUserProfile.class);
                                intent.putExtra("userId", book.getOwner());

                                vOwnerImage.setOnClickListener(v -> startActivity(intent));

                                vOwnerName.setText(book.getNomeproprietario());

                                if(profile.getNrates() > 0) {
                                    vOwnerRating.setText(String.valueOf(profile.getValutation()));
                                }
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            vOwnerInfo.setVisibility(View.VISIBLE);

        }
    }

    private void getIsReservedByMe(){
        booksRequested = new LinkedList<>();

        Query query = new Query().setFilters("renterId:" + FirebaseManagement.getUser().getUid() + " AND ( bId:"+book.getBId()+" )");
        algoIndex.searchAsync(query, ( jsonObject, e ) -> {
            if(e == null){

                SearchRequestsJsonParser search= new SearchRequestsJsonParser();
                Log.d("lista",jsonObject.toString());
                List<Request> tempReqList = new ArrayList<>(search.parseResults(jsonObject));

                for(Request tempReq : tempReqList){
                    if((tempReq.getRequestStatus() != AppConstants.COMPLETED) )
                        booksRequested.add(tempReq.getbId());
                }

                if(booksRequested.contains(book.getBId()) || book.getStato() == AppConstants.NOT_AVAILABLE){
                    bReserveOrReturn.setEnabled(false);
                    bReserveOrReturn.setVisibility(View.GONE);
                } else {
                    bReserveOrReturn.setEnabled(true);
                    bReserveOrReturn.setVisibility(View.VISIBLE);
                }

            }

        });
    }

    public void getBookInfo(){

        if(tools.isOnline(getApplicationContext())) {

            getIntent().getStringExtra("book_id");
            bookId = Objects.requireNonNull(getIntent().getExtras()).getString("book_id");
            FirebaseManagement.getDatabase().getReference().child("books").child(bookId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            StringBuilder authors= new StringBuilder();
                            StringBuilder categ= new StringBuilder();
                            int c = 0;

                            book = dataSnapshot.getValue(Book.class);
                            //foto ufficiale libro
                            if(book!=null) {
                                if (book.getUrlimage() == null || book.getUrlimage().compareTo("") == 0) {
                                    avatar.setImageDrawable(getDrawable(R.drawable.default_book));
                                } else {
                                    images.add(book.getUrlimage());
                                    adapter.notifyDataSetChanged();
                                    avatar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick( View v ) {
                                            viewPager.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    adapter.notifyDataSetChanged();
                                    GlideApp.with(getApplicationContext())
                                            .load(book.getUrlimage())
                                            .into(avatar);

                                    GlideApp.with(getApplicationContext())
                                            .load(book.getUrlimage())
                                            .apply(bitmapTransform(new BlurTransformation(25, 3)))
                                            .into(bookBackground);
                                }
                                //titolo Cé SEMPRE
                                vTitle.setText(book.getTitle());
                                //editore Cé SEMPRE
                                Veditor.setText(book.getPublisher());
                                for (String a : book.getAuthor()) {
                                    if (c < book.getAuthor().size() - 1) {
                                        c++;
                                        authors.append(a).append(", ");
                                    } else {
                                        authors.append(a);
                                    }
                                }
                                FirebaseManagement.getDatabase().getReference().child("users").child(FirebaseManagement.getUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange( DataSnapshot dataSnapshot ) {
                                                                                Profile profile= dataSnapshot.getValue(Profile.class);
                                                                                if(profile!= null){
                                                                                    mmap.clear();
                                                                                    Marker mbook = mmap.addMarker(new MarkerOptions().position(new LatLng(book.get_geoloc().getLat(), book.get_geoloc().getLng())).title(book.getTitle()));
                                                                                    MarkerOptions mprofile = new MarkerOptions().position(new LatLng(profile.getLat(), profile.getLng())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("you");

                                                                                    mmap.addMarker(mprofile);

                                                                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                                                                        builder.include(mbook.getPosition());
                                                                                    builder.include(mprofile.getPosition());

                                                                                    LatLngBounds bounds = builder.build();
                                                                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 16);
                                                                                    mmap.setPadding(50, 200, 50, 50);
                                                                                    mmap.moveCamera(cu);

                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled( DatabaseError databaseError ) {

                                                                            }
                                                                        });

                                vAuthor.setText(authors.toString());
                                //}
                                //owner
                                //vOwner.setText(book.getNomeproprietario());
                                //categorie Cé SEMPRE
                                c = 0;

                                for (String a : book.getCategories()) {
                                    if (c < book.getCategories().size() - 1) {
                                        c++;
                                        categ.append(a).append(", ");
                                    } else {
                                        categ.append(a);
                                    }
                                }
                                vCategory.setText(categ.toString());
                                vdate.setText(book.getPublishDate());
                                vDescription.setText(book.getDescription());
                                StorageReference bookImageRef =
                                        FirebaseManagement
                                                .getStorage()
                                                .getReference()
                                                .child("books")
                                                .child(bookId)
                                                .child("personal_images")
                                                .child("1.jpg");


                                Glide.with(getApplicationContext() /* context */)
                                        .load(bookImageRef)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed( @Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource ) {
                                                if ((book.getCondition().isEmpty() || book.getCondition().compareTo("") == 0) && book.getNotes().size() == 0 /*&&
                            book.*///ci va il controllo della presenza fotografia libro)
                                                        ) {
                                                    //mancano tutte quindi nascondo direttamente la card2
                                                    card2.setVisibility(View.GONE);
                                                }
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady( Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource ) {
                                                bookPhoto.setVisibility(View.VISIBLE);
                                                titleImg.setVisibility(View.VISIBLE);
                                               bookImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                   @Override
                                                   public void onSuccess( Uri uri ) {
                                                  adapter.addUrl(uri.toString());
                                                  adapter.notifyDataSetChanged();
                                                   }
                                               });

                                                return false;
                                            }
                                        })
                                        .into(bookPhoto);
                                //CARD2
                                //condizioni
                                if (book.getCondition().isEmpty() || book.getCondition().compareTo("") == 0) {
                                    titleConditions.setVisibility(View.GONE);
                                    vCondition.setVisibility(View.GONE);
                                } else {
                                    vCondition.setText(book.getCondition());
                                }
                                //note
                                StringBuilder notes;
                                if (book.getNotes().size() == 0) {
                                    titleNote.setVisibility(View.GONE);
                                    vNotes.setVisibility(View.GONE);
                                } else {
                                    //condizioni
                                    if (book.getCondition().isEmpty() || book.getCondition().compareTo("") == 0) {
                                        titleConditions.setVisibility(View.GONE);
                                        vCondition.setVisibility(View.GONE);
                                    } else {
                                        vCondition.setText(book.getCondition());
                                    }
                                    //note
                                    notes = new StringBuilder();
                                    if (book.getNotes().size() == 0) {
                                        titleNote.setVisibility(View.GONE);
                                        vNotes.setVisibility(View.GONE);
                                    } else {
                                        c = 0;
                                        for (String key : book.getNotes().keySet()) {
                                            if (c != book.getNotes().size() - 1) {
                                                String value = book.getNotes().get(key);
                                                notes.append(key).append(": ").append(value).append("\n");
                                                c++;
                                            } else {
                                                String value = book.getNotes().get(key);
                                                notes.append(key).append(": ").append(value);
                                            }
                                        }
                                    }
                                    vNotes.setText(notes.toString());
                                }

                                getIsMyBook();
                            }

                            //foto libro (fare come gli altri controlli:
                            //NON Cè: titleImg e bookPhoto vanno rese invisibili
                            //c'è: va settata e basta direi)

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        } else {
            android.app.AlertDialog.Builder ad = tools.showPopup(this, getString(R.string.noInternet), "", "");
            ad.setPositiveButton(getString(R.string.retry), (vi, w) -> onStart());
            ad.setCancelable(false);
            ad.show();
        }
    }

    public void onReserveOrReturnClick(View v){
        if(isMyBook){
            returnBook();
        } else {
            reserveBook();
        }
    }

    public void returnBook(){

        List<Request> requestList = new ArrayList<>();

        final boolean[] outcome = {false};

        // prendo riD
        //progressbar.setVisibility(View.VISIBLE);

        Query query = new Query("").setFilters("bId:"+book.getBId()+" AND (requestStatus:"+ AppConstants.ACCEPTED+" )");
        Intent i= new Intent(this, AddReview.class);
        i.putExtra("userid", book.getHolder());
        i.putExtra("bookname", book.getTitle());

        algoIndex.searchAsync(query, ( jsonObject, e ) -> {
            if(e == null){
                Request req;
                SearchRequestsJsonParser search= new SearchRequestsJsonParser();
                Log.d("lista",jsonObject.toString());
                requestList.addAll(search.parseResults(jsonObject));
                if( !requestList.isEmpty()){
                    req = requestList.get(0);
                }
                else{
                    Toast.makeText(this, "No book found", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseManagement.getDatabase().getReference()
                        .child("requests")
                        .child(req.getrId())
                        .child("requestStatus")
                        .setValue(AppConstants.COMPLETED)
                        .addOnCompleteListener(task -> {
                            Gson gson = new Gson();
                            try {
                                req.setRequestStatus(AppConstants.COMPLETED);
                                algoIndex.saveObjectAsync(new JSONObject(gson.toJson(req)),
                                        req.getObjectID().toString(),
                                        new CompletionHandler() {
                                            @Override
                                            public void requestCompleted( JSONObject jsonObject, AlgoliaException e ) {
                                                if(e!=null){
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            try {
                                book.setStato(AppConstants.AVAILABLE);
                                book.setHolder(FirebaseManagement.getUser().getUid());
                                algoBookIndex.saveObjectAsync(new JSONObject(gson.toJson(book)),
                                        book.getObjectID().toString(),
                                        new CompletionHandler() {
                                            @Override
                                            public void requestCompleted( JSONObject jsonObject, AlgoliaException e ) {
                                                if(e!=null){
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        });
                FirebaseManagement.sendMessage(getString(R.string.notify_end_request),FirebaseManagement.getUser().getDisplayName(),book.getHolder(),1);

                FirebaseManagement.getDatabase().getReference()
                        .child("users")
                        .child(book.getHolder())
                        .child("lent")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange( DataSnapshot dataSnapshot ) {
                               Long value =(Long) dataSnapshot.getValue();
                               if(value !=null) {
                                   value = value - 1;
                                   dataSnapshot.getRef().setValue(value);
                               }else{
                                   value = 0L;
                                   dataSnapshot.getRef().setValue(value);
                               }
                            }

                            @Override
                            public void onCancelled( DatabaseError databaseError ) {

                            }
                        });
                        book.setStato(AppConstants.AVAILABLE);
                        book.setHolder(req.getOwnerId());
                try {
                    algoBookIndex.saveObjectAsync(new JSONObject(gson.toJson(book)),
                            book.getObjectID().toString(),
                            null);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                FirebaseManagement.getDatabase().getReference()
                                .child("books")
                                .child(req.getbId())
                                .setValue(book);
                        outcome[0] = true;

                //progressbar.setVisibility(View.GONE);
                Toast.makeText(this, "Book returned successfully", Toast.LENGTH_SHORT).show();
                bReserveOrReturn.setVisibility(View.GONE);
                if(req.getReviewStatusOwner()== AppConstants.NOT_REVIEWED) {
                    i.putExtra("reqid",req.getrId());
                    startActivity(i);
                }
            }
        });
    }

    public void reserveBook(){

        final boolean[] completed = {true};
        final boolean[] alreadyRequested = new boolean[1];
        List<Request> requestList = new LinkedList<>();
        //progressbar.setVisibility(View.VISIBLE);

        alreadyRequested[0] = false;
        // query per controlla se io ho gia mandato una richiesta per quel libro

        Query query = new Query().setFilters("renterId:" + FirebaseManagement.getUser().getUid() + " AND ("
                + "bId:" + book.getBId()+" AND  requestStatus:" + AppConstants.PENDING+")");

        algoIndex.searchAsync(query, ( jsonObject, e ) -> {
            if(e == null){
                SearchRequestsJsonParser search= new SearchRequestsJsonParser();
                Log.d("lista",jsonObject.toString());
                requestList.addAll(search.parseResults(jsonObject));

                if(requestList.size() != 0){
                    alreadyRequested[0] = true;
                }

                if(book.getStato() == AppConstants.AVAILABLE && !alreadyRequested[0]) {
                    try {

                        bReserveOrReturn.setVisibility(View.GONE);
                        progressBarRequest.setVisibility(View.VISIBLE);

                        FirebaseManagement.sendMessage(getString(R.string.notify_new_request),FirebaseManagement.getUser().getDisplayName(),book.getOwner(),1);

                        String rId = FirebaseManagement.getDatabase().getReference().child("requests").push().getKey();
                        Request request = new Request("0", AppConstants.NOT_REVIEWED, AppConstants.NOT_REVIEWED,
                                AppConstants.PENDING, book.getOwner(), book.getBId(), book.getTitle(),
                                FirebaseManagement.getUser().getUid(), book.getNomeproprietario(), FirebaseManagement.getUser()
                                .getDisplayName(), book.getUrlimage(), null);

                        FirebaseManagement.getDatabase().getReference().child("requests").child(rId).setValue(request);
                        FirebaseManagement.getDatabase().getReference()
                                .child("users")
                                .child(book.getOwner())
                                .child("reqNotified")
                                .setValue(true);
                        request.setrId(rId);

                        algoIndex.addObjectAsync(new JSONObject(gson.toJson(request)), ( jsonObject1, exception ) -> {
                            if(exception == null){
                                try{
                                    Long id= jsonObject1.getLong("objectID");
                                    request.setObjectID(id);
                                }catch (Exception e1){
                                    completed[0] = false;
                                }
                                if(completed[0]) {
                                    FirebaseManagement.getDatabase().getReference().child("requests").child(rId).setValue(request);
                                }
                            } else{
                                Toast.makeText(getApplicationContext(), "Error in algolia occurred", Toast.LENGTH_SHORT).show();
                                exception.getMessage();
                                Log.d("error",exception.toString());
                                completed[0] = false;
                                return;
                            }
                            if(completed[0]) {
                                Toast.makeText(getApplicationContext(), R.string.request_sent, Toast.LENGTH_SHORT).show();
                                progressBarRequest.setVisibility(View.GONE);
                                bReserveOrReturn.setEnabled(false);
                            }
                            
                        });

                    }catch (Exception e2){
                        Toast.makeText(this, "Exception Occurred", Toast.LENGTH_SHORT).show();
                        e2.getMessage();
                    }
                }
                else{
                    Toast.makeText(this, "Book not available or already requested from you", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public Drawable loadImageFromURL(String url, String name) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            return Drawable.createFromStream(is, name);
        } catch (Exception e) {
            return null;
        }
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof ViewPager)) {

            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    //hideSoftKeyboard(ShowBook.this);
                    viewPager.setVisibility(View.GONE);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(
                Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
    }

    @Override
    public void onMapReady( GoogleMap googleMap ) {
        mmap=googleMap;
        getBookInfo();
    }
}
package gmads.it.gmads_lab1.BookPackage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import gmads.it.gmads_lab1.constants.AppConstants;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.HomePackage.Home;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.ToolsPackege.Tools;
import gmads.it.gmads_lab1.UserPackage.Profile;

public class SaveBook extends AppCompatActivity{

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private AppBarLayout appbar;
    private ImageView coverImage;
    private LinearLayout linearlayoutTitle;
    //private TextView textviewTitle;
    private static final String EXTRA_ISBN ="ISBN";
    private DatabaseReference mProfileReference;
    private DatabaseReference mBooksReference;
    private StorageReference storageReference;
    private String isbn;
    private String urlimage=null;
    static final int MY_CAMERA_REQUEST_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1888;
    static final int REQUEST_IMAGE_LIBRARY = 1889;
    private ImageView addImage; //foto nella view in basso
    private Bitmap newBitMapBookImage; //temp for new image
    private SharedPreferences prefs;
    boolean imagechanged=false;
    Toolbar toolbar;
    ContextWrapper cw;
    File directory;
    String path;
    LinearLayout ll;
    EditText vTitle;
    EditText vDate;
    EditText vAuthor;
    EditText vCategories;
    EditText vDescription;
    EditText vPublisher;
    Tools t1;
    Button add;
    Book book;
    ProgressBar progressBar;
    Client algoClient;
    Index algoIndex;
    Gson gson = new Gson();
    Profile profile;
    List<String> authors= new ArrayList<>();
    List<String> categories= new ArrayList<>();
    Map<String,String> notes= new HashMap<>();
    ViewPager viewPager;
    LinearLayout ll_author;
    LinearLayout ll_categ;
    LinearLayout ll_notes;
    boolean isRawData;
    EditText condition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_book);
        findActViews();
        setupUI(findViewById(R.id.linearlayout));
        controlFocus();
        //coverImage.setImageResource(R.drawable.cover_edit);
        toolbar.setTitle(getString(R.string.bookTitle));
        //textviewTitle.setText(getString(R.string.bookTitle));
        setSupportActionBar(toolbar);
        //startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);
        t1 = new Tools();
        if (!(t1.isOnline(getApplicationContext()))){
            android.app.AlertDialog.Builder ad = t1.showPopup(this, getString(R.string.noInternet), "", "");
            //tasto retry rimanda ad addbook
            ad.setPositiveButton(getString(R.string.retry), (vi, w) -> onBackPressed());
            ad.setCancelable(false);
            ad.show();
        }
        setReferences();
        cw = new ContextWrapper(getApplicationContext());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        directory = cw.getDir(getString(R.string.imageDirectory), Context.MODE_PRIVATE);
        path = directory.getPath();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        isRawData = getIntent().getBooleanExtra("rawData", false);
        getUserInfo();
        algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
        algoIndex = algoClient.getIndex("BookIndex");

    }

    public void setReferences(){
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isbn = prefs.getString(EXTRA_ISBN, null);
        if (isbn != null) {
            mProfileReference = FirebaseManagement.getDatabase().getReference()
                    .child("users")
                    .child(FirebaseManagement.getUser().getUid());
            mBooksReference = FirebaseManagement.getDatabase().getReference()
                    .child("books");
        }
    }
    public void findActViews(){
        appbar = findViewById(R.id.appbar);
        coverImage = findViewById(R.id.imageview_placeholder);
        linearlayoutTitle = findViewById(R.id.linearlayout_title);
        toolbar = findViewById(R.id.toolbar);
        ImageView avatar = findViewById(R.id.avatar);
        avatar.setImageDrawable(getDrawable(R.drawable.default_book));
        condition=findViewById(R.id.condition);
        ll_author=findViewById(R.id.ll_author);
        ll_categ=findViewById(R.id.ll_categ);
        ll_notes=findViewById(R.id.ll_notes);
        vTitle = findViewById(R.id.title);
        vDate = findViewById(R.id.data);
        vAuthor = findViewById(R.id.author);
        vCategories = findViewById(R.id.categorie);
        vPublisher = findViewById(R.id.editore);
        vDescription = findViewById(R.id.descrizione);
        addImage = findViewById(R.id.bookphoto);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.bookTitle));
        setSupportActionBar(toolbar);
    }
    public void onAddAuthor(View v){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.author_layout, null);
        // Add the new row before the add field button.
        if(ll_author.getChildCount()<3){

            LinearLayout ll;
            ll=(LinearLayout) ll_author.getChildAt(ll_author.getChildCount()-1);
            EditText field=(EditText) ll.getChildAt(0);
            if(field.getText().length()!=0) {
                ll_author.addView(rowView, ll_author.getChildCount() );
            }
        }
    }

    public void onAddCateg(View v){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.categ_layout, null);
        // Add the new row before the add field button.
        if(ll_categ.getChildCount()<3) {
            LinearLayout ll;
            ll=(LinearLayout) ll_categ.getChildAt(ll_categ.getChildCount()-1);
            EditText field=(EditText) ll.getChildAt(0);
            if (field.getText().length() != 0) {
                ll_categ.addView(rowView, ll_categ.getChildCount());
            }
        }
    }
    public void onAddNotes(View v){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.notes_layout, null);
        // Add the new row before the add field button.
        LinearLayout ll;
        ll=(LinearLayout) ll_notes.getChildAt(ll_notes.getChildCount()-1);
        EditText field=(EditText) ll.getChildAt(0);
        if(field.getText().length()!=0) {
            ll_notes.addView(rowView, ll_notes.getChildCount());
        }
    }
    public void getjson(Context c,  String isbn) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=ISBN:<";
        url = url + isbn + ">";
        RequestQueue queue = Volley.newRequestQueue(c);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {//Display the first 500 characters of the response string.
                    LinearLayout l2;
                    String title;
                    String author;
                    String publisher;
                    String publishdate;
                    String categories;
                    String description;
                    int valuesFound;
                    JSONObject resultObject;
                    JSONObject volumeObject;
                    JSONArray bookArray;
                    JSONObject bookObject;
                    try {
                        //piglio Json
                        resultObject = new JSONObject(response);
                        valuesFound = resultObject.getInt("totalItems");
                        if(valuesFound == 0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Book not found, check inserted isbn!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", ( dialog, id ) -> {
                                        Intent i = new Intent(c, AddBook.class);
                                        startActivity(i);
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                        bookArray = resultObject.getJSONArray("items");
                        bookObject = bookArray.getJSONObject(0);
                        volumeObject = bookObject.getJSONObject("volumeInfo");
                        //piglio stringhe
                    }catch (Exception e){
                        volumeObject= new JSONObject();
                    }
                    try{
                        title = volumeObject.getString("title");
                        vTitle.setText(title);
                    }catch (Exception e){
                        //vTitle.setText(getString(R.string.titleNotFound));
                    }
                    try{
                        author = volumeObject.getString("authors");
                        author = author.replaceAll("[\"\\[\\]]","");
                        l2=(LinearLayout)ll_author.getChildAt(0);
                        EditText et=(EditText)l2.getChildAt(0);
                        et.setText(author);
                    }catch (Exception e){
                        //vAuthor.setText(getString(R.string.authorNotFound));
                    }
                    try{
                        if(!volumeObject.isNull("publisher")&& volumeObject.has("publisher")){
                            publisher = volumeObject.getString("publisher");
                            vPublisher.setText(publisher);
                        }else{
                            //vPublisher.setText(getString(R.string.publisherNotFound));
                        }
                    }catch (Exception e){
                        //vPublisher.setText(getString(R.string.publisherNotFound));
                    }
                    try{
                        publishdate= volumeObject.getString("publishedDate");
                        vDate.setText(publishdate);
                    }catch (Exception e){
                        //vDate.setText(getString(R.string.pDateNotFound));
                    }
                    try{
                        categories = volumeObject.getString("categories");
                        categories = categories.replaceAll("[\"\\[\\]]","");
                        l2=(LinearLayout)ll_categ.getChildAt(0);
                        EditText edit_cat=(EditText)l2.getChildAt(0);
                        edit_cat.setText(categories);
                       // vCategories.setText(categories);
                    }catch (Exception e){
                        //vCategories.setText(getString(R.string.categoryNotFound));
                    }
                    try{
                        urlimage = volumeObject.getJSONObject("imageLinks").getString("thumbnail");
                    }catch (Exception e){
                        urlimage="";
                    }
                    try {
                        if(!volumeObject.isNull("description")&& volumeObject.has("description")) {
                            description = volumeObject.getString("description");
                            vDescription.setText(description);
                        }else{
                            //vDescription.setText(R.string.descriptionNotFound);
                        }
                    }catch(Exception e){
                        //vDescription.setText(R.string.descriptionNotFound);
                    }
                    Glide.with(this).load(urlimage).into((ImageView)findViewById(R.id.avatar));
                    prefs = PreferenceManager.getDefaultSharedPreferences(c);

                }, error -> Log.d("That didn't work!","Error: "+error));
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public boolean fieldsEmpty(){
        if(vTitle.getText().toString().isEmpty()){
            vTitle.setError(getString(R.string.titleNotFound));
            vTitle.requestFocus();
            return false;
        }else if(vDate.getText().toString().isEmpty()){
            vDate.setError(getString(R.string.pDateNotFound));
            vDate.requestFocus();
            return false;
        }else if(vCategories.getText().toString().isEmpty()){
            vCategories.setError(getString(R.string.categoryNotFound));
            vCategories.requestFocus();
            return false;
        }else if(vAuthor.getText().toString().isEmpty()){
            vAuthor.setError(getString(R.string.authorNotFound));
            vAuthor.requestFocus();
            return false;
        }else if(vPublisher.getText().toString().isEmpty()){
            vPublisher.setError(getString(R.string.publisherNotFound));
            vPublisher.requestFocus();
            return false;
        }else if(vDescription.getText().toString().isEmpty()){
            vDescription.setError(getString(R.string.descriptionNotFound));
            vDescription.requestFocus();
            return false;
        }
        return true;
    }
    //save data on click save
    private void onSaveClick() {
        //check on email using a regex
        View view = this.getCurrentFocus();
        if(fieldsEmpty()) {
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                assert imm != null;
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            Tools t = new Tools();

            book = new Book(
                    null,
                    isbn,
                    vTitle.getText().toString(),
                    vDescription.getText().toString(),
                    urlimage,
                    vDate.getText().toString(),
                    authors,
                    categories,
                    vPublisher.getText().toString(),
                    FirebaseManagement.getUser().getUid(),
                    profile.getLat(), //da prendere dal profilo utente da firebase
                    profile.getLng(),
                    AppConstants.AVAILABLE
            );
            //set popup
            android.app.AlertDialog.Builder ad = t.showPopup(this, getString(R.string.saveQuestion), "", getString(R.string.cancel));
            ad.setPositiveButton("Ok", ( vi, w ) -> {
                mBooksReference = FirebaseManagement.getDatabase().getReference().child("books");
                String bookKey = mBooksReference.push().getKey();
                final ProgressDialog progressDialog = new ProgressDialog(this);
                LinearLayout l1;
                EditText et;
                EditText et2;

                for(int i=0; i< ll_author.getChildCount();i++){
                    ll=(LinearLayout)ll_author.getChildAt(i);
                    et=(EditText)ll.getChildAt(0);
                    if(et.getText().length()!=0){
                        authors.add(et.getText().toString());
                    }
                }
                for(int i=0; i< ll_categ.getChildCount();i++){
                    ll=(LinearLayout)ll_categ.getChildAt(i);
                    et=(EditText)ll.getChildAt(0);
                    if(et.getText().length()!=0){
                        this.categories.add(et.getText().toString());
                    }
                }

                for(int i=0; i< ll_notes.getChildCount();i++){
                    ll=(LinearLayout)ll_notes.getChildAt(i);
                    et=(EditText)ll.getChildAt(0);
                    et2=(EditText)ll.getChildAt(1);
                    if(et.getText().length()!=0 && et2.getText().length()!=0) {
                        this.notes.put(et.getText().toString(), et2.getText().toString());
                    }
                }
                book.setNomeproprietario(profile.getName());
                book.setCondition(condition.getText().toString());
                book.setAuthor(authors);
                book.setCategories(categories);
                book.setNotes(notes);
                book.setBId(bookKey);

                try {
                    algoIndex.addObjectAsync(new JSONObject(gson.toJson(book)), ( jsonObject, e ) -> {
                        if(e==null){
                            try{
                            Long id= jsonObject.getLong("objectID");
                            book.setObjectID(id);
                            }catch (Exception e2){
                                book.setObjectID(Long.getLong("0"));
                            }
                            mBooksReference.child(bookKey).setValue(book);
                            //fine inserimento nelle liste
                            mProfileReference = FirebaseManagement.getDatabase().getReference();
                            mProfileReference.child("users").child(book.getOwner()).child("myBooks").child(bookKey).setValue(book.getIsbn());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (newBitMapBookImage != null) {
                    storageReference = FirebaseManagement
                            .getStorage()
                            .getReference()
                            .child("books")
                            .child(this.book.getBId())
                            .child("personal_images")
                            .child("1.jpg");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    newBitMapBookImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = storageReference.putBytes(data);
                    uploadTask
                            .addOnFailureListener(exception -> toastMessage(getString(R.string.failed_book_upload)))
                            .addOnSuccessListener(taskSnapshot -> {
                                toastMessage("Image upload successful");
                                progressDialog.dismiss();
                            });
                }
                Intent pickIntent = new Intent(this, Home.class);
                prefs.edit().putString(EXTRA_ISBN, isbn).apply();
                startActivity(pickIntent);
                finish();
            });
            ad.show();
        }
    }
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    //for SaveButton in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.save_book, menu);
        return true;
    }

    private void setFocusOnClick(View v){
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void onAddPhotoClick(View v) {
        //set popup
        setFocusOnClick(Objects.requireNonNull(this.getCurrentFocus()));
        v.getContext();
        android.app.AlertDialog.Builder ad=t1.showPopup(this,getString(R.string.addBookImage),getString(R.string.selectGallery),getString(R.string.selectFromCamera));
        ad.setPositiveButton(getString(R.string.selectGallery),(vi,w)->{
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            startActivityForResult(pickIntent, REQUEST_IMAGE_LIBRARY);
        });
        ad.setNegativeButton(getString(R.string.selectFromCamera),(vi,w)->{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });
        ad.show();
        //-->
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
    @Override
    //-->function activated when a request is terminated
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //manage request image capture
        if (requestCode == REQUEST_IMAGE_CAPTURE  && resultCode == RESULT_OK) {
            imagechanged=true;
            Bundle imageUri = data.getExtras();
            newBitMapBookImage = (Bitmap) Objects.requireNonNull(imageUri).get("data");
            addImage.setImageBitmap(newBitMapBookImage);
            //manage request image from gallery
        } else if (requestCode==REQUEST_IMAGE_LIBRARY && resultCode == RESULT_OK) {
            imagechanged=true;
            try{
                final Uri imageUri = data.getData();
                assert imageUri != null;
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                newBitMapBookImage = BitmapFactory.decodeStream(imageStream);
                // bookImage.loadUrl(bitmapToUrl(newBitMapBookImage));
                addImage.setImageBitmap(newBitMapBookImage);
                //bookImage.setImageBitmap(newBitMapBookImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //function used to save the image in the correct path

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
                onSaveClick();
        }
        return super.onOptionsItemSelected(item);
    }
    //animation back button
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
    public String bitmapToUrl(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return "data:image/png;base64," + imgageBase64;
    }
    private void getUserInfo(){
        FirebaseManagement.getDatabase().getReference().child("users").child(FirebaseManagement.getUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        profile = dataSnapshot.getValue(Profile.class);
                        if(!isRawData) {
                            getjson(getApplicationContext(), isbn);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                });

    }
    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(SaveBook.this);
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
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void collapseToolbar(){
        AppBarLayout appbarLayout = findViewById(R.id.appbar);
        appbarLayout.setExpanded(false);
    }

    public void controlFocus(){
        //
        vTitle.setOnFocusChangeListener((arg0, hasfocus) -> {
            if (hasfocus) {
                collapseToolbar();
            }
        });
        vAuthor.setOnFocusChangeListener((arg0, hasfocus) -> {
            if (hasfocus) {
                collapseToolbar();
            }
        });
        vCategories.setOnFocusChangeListener((arg0, hasfocus) -> {
            if (hasfocus) {
                collapseToolbar();
            }
        });
        vPublisher.setOnFocusChangeListener((arg0, hasfocus) -> {
            if (hasfocus) {
                collapseToolbar();
            }
        });
        vDescription.setOnFocusChangeListener((arg0, hasfocus) -> {
            if (hasfocus) {
                collapseToolbar();
            }
        });
        //
    }
}

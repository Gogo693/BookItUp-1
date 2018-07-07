package gmads.it.gmads_lab1.BookPackage;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import gmads.it.gmads_lab1.Chat.ChatList;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.HomePackage.Home;
import gmads.it.gmads_lab1.Login;
import gmads.it.gmads_lab1.MyLibraryPackage.MyLibrary;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.RequestPackage.RequestActivity;
import gmads.it.gmads_lab1.ToolsPackege.Scanner;
import gmads.it.gmads_lab1.UserPackage.ShowProfile;
import gmads.it.gmads_lab1.ToolsPackege.Tools;
import gmads.it.gmads_lab1.UserPackage.Profile;

public class AddBook extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_ISBN_IMAGE = 1888;
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    Button ISBNbutton;
    Button insertButton;
    ImageView next;
    private EditText editISBN;
    Toolbar toolbar;
    SharedPreferences prefs;
    DrawerLayout drawer;
    private DatabaseReference mProfileReference;
    ValueEventListener mProfileListener;
    private TextView navName;
    private TextView navMail;
    private ImageView navImage;
    private ImageView navReqNotification;
    NavigationView navigationView;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
            mProfileReference = FirebaseManagement.getDatabase()
                    .getReference()
                    .child("users")
                    .child(FirebaseManagement.getUser().getUid());
            mProfileReference.keepSynced(true);
        //toolbar
        findActViews();
        findNavViews();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //settare navbar
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        headerView.setBackgroundResource(R.color.colorPrimaryDark);
        next.setOnClickListener(v->onNextClick());
        ISBNbutton.setOnClickListener(v->onGetISBNClick());
        insertButton.setOnClickListener(v->onInsertClick());
    }
    public void findNavViews(){
        drawer =  findViewById(R.id.drawer_layout);
        navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        navName =  headerView.findViewById(R.id.navName);
        navMail = headerView.findViewById(R.id.navMail);
        navImage = headerView.findViewById(R.id.navImage);
        navReqNotification = headerView.findViewById(R.id.req_notification);
    }
    public void findActViews(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Scanner");
        setSupportActionBar(toolbar);
        this.editISBN =  findViewById(R.id.et_isbn);
        this.ISBNbutton = findViewById(R.id.b_scan);
        this.next = findViewById(R.id.b_next);
        this.insertButton = findViewById(R.id.insert);
    }
    @Override
    public void onStart() {
        super.onStart();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile myuser = dataSnapshot.getValue(Profile.class);
                if (myuser !=null) {
                    //dati navbar
                    navName.setText(myuser.getName());
                    //navName.append(" " + myuser.getSurname());
                    navMail.setText(myuser.getEmail());
                    if(myuser.isReqNotified()){
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
                    //setto foto
                    if (Objects.requireNonNull(myuser).getImage() != null) {
                        try {
                            File localFile = File.createTempFile("image", "jpg");
                            StorageReference profileImageRef = FirebaseManagement.getStorage().getReference()
                                    .child("users")
                                    .child(FirebaseManagement.getUser().getUid())
                                    .child("profileimage.jpg");

                            profileImageRef.getFile(localFile)
                                    .addOnSuccessListener(taskSnapshot -> navImage.setImageBitmap(BitmapFactory.decodeFile(localFile.getPath())))
                                    .addOnFailureListener(e -> Log.d("errore", e.toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {//default image
                        navImage.setImageDrawable(getDrawable(R.drawable.default_picture));
                    }
                }
                else{
                    navName.setText(getString(R.string.nameExample));
                    navMail.setText(getString(R.string.emailExample));
                    navImage.setImageDrawable(getDrawable(R.drawable.default_picture));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mProfileReference.addValueEventListener(postListener);
        mProfileListener = postListener;
    }
    public void onNextClick(){
            String ISBNcode=editISBN.getText().toString();
            if(ISBNcode.length() != 13){
                Tools t= new Tools();
                t.showPopup(this,getString(R.string.isbnerror),"", "Ok").show();
            }else {
                Intent pickIntent = new Intent(this, SaveBook.class);
                prefs.edit().putString("ISBN",ISBNcode).apply();
                startActivity(pickIntent);
            }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            Intent intentMod = new Intent(this, Home.class);
            startActivity(intentMod);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_showProfile) {
            // Handle the camera action
            Intent intentMod = new Intent(this, ShowProfile.class);
            startActivity(intentMod);
            return true;
        } else if (id == R.id.nav_addBook) {
            //deve solo chiudersi la navbar
            drawer.closeDrawers();
        } else if(id == R.id.nav_home){
            Intent intentMod = new Intent(this, Home.class);
            startActivity(intentMod);
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
        } else if(id == R.id.nav_mylibrary){
            startActivity(new Intent(this,MyLibrary.class));
            finish();

            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onGetISBNClick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, Scanner.class);
            startActivityForResult(intent, REQUEST_ISBN_IMAGE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ZBAR_CAMERA_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                onGetISBNClick();

            } else {

                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode == REQUEST_ISBN_IMAGE) {
           if(resultCode == RESULT_OK) {
               String ISBNcode = Objects.requireNonNull(data.getData()).toString();
               editISBN.setText(ISBNcode);
           }
       }
    }

    private void onInsertClick(){
        Intent intent = new Intent(this, SaveBook.class);
        intent.putExtra("rawData", true);
        startActivity(intent);
    }

    public void onClickNotify(View view){
        Intent intentMod = new Intent(getApplicationContext(), RequestActivity.class);
        startActivity(intentMod);
        //finish();
    }
}

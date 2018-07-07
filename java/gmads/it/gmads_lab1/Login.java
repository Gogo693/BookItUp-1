package gmads.it.gmads_lab1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;
import java.util.Objects;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import gmads.it.gmads_lab1.FirebasePackage.Datasource;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.HomePackage.Home;
import gmads.it.gmads_lab1.UserPackage.ShowProfile;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);//prima di aprire il login di firebase immagine di home come sfondo
        login();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void login(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //controllo se è già loggato
        if (auth.getCurrentUser() != null) {
            // already signed in
            //se è gia loggato invio alla classe home uid e chiudo l'attività
            FirebaseManagement.loginUser();
            Datasource.getInstance().sincMyProfile();
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("activity", "login");
            startActivity(intent);
            //finish();
        } else {
            //se non è loggato mi loggo attraverso l'attività di firebase

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()
                                    ,new AuthUI.IdpConfig.FacebookBuilder().build()
                            ))
                            .setLogo(R.mipmap.my_launcher_overbooking_round)
                            .setTheme(R.style.LoginTheme)
                            .build(),
                    RC_SIGN_IN);
            // not signed in
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //ritorno dall'attività di firebase e se si è loggato vado a home
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(this, ShowProfile.class);
            intent.putExtra("activity", "login");
            FirebaseManagement.getDatabase().getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() == null){
                                FirebaseManagement.createUser(getApplicationContext(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(),intent);
                            }
                            else{
                                FirebaseManagement.loginUser();

                                startActivity(intent);

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}

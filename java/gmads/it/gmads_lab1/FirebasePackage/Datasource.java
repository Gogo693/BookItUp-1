package gmads.it.gmads_lab1.FirebasePackage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import gmads.it.gmads_lab1.UserPackage.Profile;


/* HOW TO USE
    In ogni actvity dove serve lanciamo all'inizio un
    Datasource dsInstance = Datasource.getInstance();
    In questo modo posso accedere a tutte le funzioni del datasource scrivendo dsInstance.funzione
    Alternativa: scrivo ogni volta Datasource.getInstance().funzione
 */

public class Datasource {
    private static volatile Datasource dsInstance;

    private Profile myProfile = null;
    private File myProfileImage = null;
    private DatabaseReference profileRef = null;
    private ValueEventListener profileListener = null;

    private Bitmap myProfileBitImage = null;

    //private constructor.
    private Datasource() {

        //Prevent form the reflection api.
        if (dsInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static Datasource getInstance() {

        if (dsInstance == null) { //if there is no instance available... create new one
            synchronized (Datasource.class) { //fa un double check per prevenire copia su diversi thread
                if (dsInstance == null) dsInstance = new Datasource();
            }
        }

        return dsInstance;
    }

    public Profile getMyProfile(){
        return myProfile;
    }

    public Bitmap getMyProfileBitImage() {
        return myProfileBitImage;
    }

    public void sincMyProfile(){

        myProfileImage = new File("myProfileImage", "jpg");

        profileRef = FirebaseManagement.getDatabase().getReference().child("users").child(FirebaseManagement.getUser().getUid());

        profileRef.keepSynced(true);

        profileListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myProfile = dataSnapshot.getValue(Profile.class);
                        if (myProfile != null) {
                            if (myProfile.getImage() != null) {
                                try {
                                    StorageReference profileImageRef =
                                            FirebaseManagement
                                                    .getStorage()
                                                    .getReference()
                                                    .child("users")
                                                    .child(FirebaseManagement.getUser().getUid())
                                                    .child("myProfileImage.jpg");

                                    profileImageRef.getFile(myProfileImage)
                                            .addOnSuccessListener(taskSnapshot -> myProfileBitImage = BitmapFactory.decodeFile(myProfileImage.getPath()))
                                            .addOnFailureListener(e -> Log.d("DB ERROR", "Failed to retrieve image from server"));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                    }
                };

        profileRef.addValueEventListener(profileListener);

        if(myProfileBitImage == null && myProfileImage != null) {
            myProfileBitImage = BitmapFactory.decodeFile(myProfileImage.getPath());
        }
    }


}

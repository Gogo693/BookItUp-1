package gmads.it.gmads_lab1.UserPackage;

import android.graphics.Bitmap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;

import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;

public class ProfileInfoSync {
    public static ProfileInfoSync pISInstance = new ProfileInfoSync();

    public Bitmap profileImage = null;
    private Profile myProfile = null;

    public void loadProfileInfo(){
        FirebaseManagement.getDatabase().getReference().child("users").child(FirebaseManagement.getUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myProfile = dataSnapshot.getValue(Profile.class);
                        if(myProfile!=null){
                            if(myProfile.getImage()!=null) {
                                try {
                                    File localFile = File.createTempFile("images", "jpg");

                                    StorageReference profileImageRef = FirebaseManagement.getStorage().getReference()
                                            .child("users")
                                            .child(FirebaseManagement.getUser().getUid())
                                            .child("profileimage.jpg");

                                    profileImageRef.getFile(localFile)
                                            .addOnSuccessListener(taskSnapshot -> {
                                            }).addOnFailureListener(e -> {

                                    });


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



}

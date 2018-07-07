package gmads.it.gmads_lab1.FirebasePackage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.functions.FirebaseFunctions;
import java.util.Objects;

import gmads.it.gmads_lab1.BookPackage.Book;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.UserPackage.Profile;
import gmads.it.gmads_lab1.Chat.service.MyFirebaseInstanceIDService;
import com.google.firebase.functions.FirebaseFunctionsException;

import org.json.JSONObject;
import com.google.gson.Gson;

public class FirebaseManagement {

    private static volatile FirebaseManagement firebaseManagementInstance = new FirebaseManagement();
    private static FirebaseAuth Auth;
    private static FirebaseDatabase Database;
    private static FirebaseStorage Storage;
    private static FirebaseUser User;
    private static FirebaseStorage storage;
    private static FirebaseFunctions mFunctions;
    public static FirebaseAuth getAuth() {
        return Auth;
    }

    public static FirebaseUser getUser() {
        if(User==null){
            User=Auth.getCurrentUser();
        }
        return User;
    }

    public static FirebaseStorage getStorage(){
        if(storage==null){
            storage= FirebaseStorage.getInstance();
        }
        return storage;
    }
    public static void sendMessage(String text,String from,String to,int type){
        mFunctions = FirebaseFunctions.getInstance();
     java.util.Map<String, Object> data = new java.util.HashMap<>();
    data.put("text", text);
    data.put("from",from);
    data.put("to",to);
    data.put("type",type);
    Task<String> r=mFunctions
            .getHttpsCallable("addMessage")
            .call(data)
            .continueWith(task-> {
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                return (String) task.getResult().getData();
                });
    r.addOnCompleteListener(task-> {
                                        if (!task.isSuccessful()) {
                                            Exception e = task.getException();
                                            if (e instanceof FirebaseFunctionsException) {
                                               FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                            }
                                         }
                                    });
    }

    public static FirebaseDatabase getDatabase(){
        if(Database ==null){
            Database =FirebaseDatabase.getInstance();
            Database.setPersistenceEnabled(true);
            return Database;
        }
        else return Database;
}
    //private constructor
    public FirebaseManagement() {

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Database = FirebaseDatabase.getInstance();
        Database.setPersistenceEnabled(true);
        Auth = FirebaseAuth.getInstance();
        Storage = FirebaseStorage.getInstance();
    }

    public static Task<Void> updateUserData( Profile profile){
        if(User != null) {
            return Database.getReference().child("users").child(User.getUid()).setValue(profile);
        }
        return null;
    }

    public static void createUser(Context context, String email,Intent i){
        User = Auth.getCurrentUser();
        Profile newProfile;
        newProfile = new Profile(User.getUid(),getUser().getDisplayName(),"surname da togliere", email, context.getString(R.string.bioExample));
        Database.getReference().child("users").child(User.getUid()).setValue(newProfile).addOnCompleteListener(task -> {
            MyFirebaseInstanceIDService fInstance = new MyFirebaseInstanceIDService();
            fInstance.addToken(FirebaseInstanceId.getInstance().getToken());
            Datasource.getInstance().sincMyProfile();
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });
    }

    public static void loginUser(){
        User = Auth.getCurrentUser();

        String newRegistrationToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("refresh",newRegistrationToken);
        if (User != null)
            MyFirebaseInstanceIDService.Companion.addTokenToFirebase(newRegistrationToken);
    }

    public void removebook(String bid){
        Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
        Index algoIndex = algoClient.getIndex("BookIndex");
        FirebaseManagement.getDatabase().getReference().child("books").child(bid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot ) {
                       Book book = dataSnapshot.getValue(Book.class);
                       if(book!= null){
                            try {
                                algoIndex.deleteObject(String.valueOf(Objects.requireNonNull(book).getObjectID()),null);
                                FirebaseManagement.getDatabase().getReference().child("books").child(bid).removeValue();
                            } catch (AlgoliaException e) {
                                e.printStackTrace();
                            }
                       }
                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError ) {

                    }
                });

    }
    public void updatebook(String bid){
        Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
        Index algoIndex = algoClient.getIndex("BookIndex");
        FirebaseManagement.getDatabase().getReference().child("books").child(bid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot ) {
                        Book book = dataSnapshot.getValue(Book.class);
                        if(book!= null){
                            try {
                                Gson gson = new Gson();
                                algoIndex.saveObject(new JSONObject(gson.toJson(book)), String.valueOf(book.getObjectID()),null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError ) {

                    }
                });

    }
}

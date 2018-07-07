package gmads.it.gmads_lab1.ToolsPackege;


import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import org.json.JSONObject;
import de.hdodenhof.circleimageview.CircleImageView;
import gmads.it.gmads_lab1.UserPackage.Profile;
import gmads.it.gmads_lab1.UserPackage.ShowUserProfile;
import gmads.it.gmads_lab1.constants.AppConstants;
import gmads.it.gmads_lab1.Chat.glide.GlideApp;
import gmads.it.gmads_lab1.BookPackage.Book;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.RequestPackage.Request;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ExpandableListAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<Book> listHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Request>> listChild;

    private Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
    private Index algoReqIndex = algoClient.getIndex("requests");
    private Index algoBookIndex = algoClient.getIndex("BookIndex");

    public ExpandableListAdapter(Context context, List<Book> listDataHeader,
                                 HashMap<String, List<Request>> listChildData) {
        this.context = context;
        this.listHeader = listDataHeader;
        this.listChild = listChildData;
    }

    @Override
    public Request getChild(int groupPosition, int childPosititon) {
        return this.listChild.get(this.listHeader.get(groupPosition).getBId())
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Request child = getChild(groupPosition, childPosition);
        final String childText =  child.getRenterName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(infalInflater).inflate(R.layout.item_others_request, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.name);
        TextView vRate = convertView.findViewById(R.id.rate);
        CircleImageView bimage = convertView.findViewById(R.id.userphoto);
        //CircleImageView civ = (CircleImageView) convertView.findViewById(R.id.ownerphoto);
        //settare foto user se c'è DOPO AVER SETTATO QUELLA DI DEFAULT
        StorageReference userImageRef =
                FirebaseManagement
                        .getStorage()
                        .getReference()
                        .child("users")
                        .child(child.getRenterId())
                        .child("profileimage.jpg");
        /*
        GlideApp.with(context)
                .load(userImageRef)
                .placeholder(R.drawable.default_picture)
                .into(bimage);*/

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.default_picture);
        requestOptions.error(R.drawable.default_picture);
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(userImageRef).into(bimage);

        txtListChild.setText(childText);

        FirebaseManagement.getDatabase().getReference()
                .child("users")
                .child(child.getRenterId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Profile profile = dataSnapshot.getValue(Profile.class);

                        if(profile.getNrates() > 0){
                            if(profile.getValutation() != 0) {
                                vRate.setText(String.valueOf(profile.getValutation()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        TextView bProfile = convertView.findViewById(R.id.name);

        bProfile.setOnClickListener(v -> onClickProfile(child));
        bimage.setOnClickListener(v->onClickProfile(child));

        TextView bYes = convertView.findViewById(R.id.yes);
        bYes.setOnClickListener( v -> onClickYes(child));

        TextView bNo = convertView.findViewById(R.id.no);
        bNo.setOnClickListener( v -> onClickNo(child));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listChild.get(this.listHeader.get(groupPosition).getBId()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Book b=(Book) getGroup(groupPosition);
        String headerTitle =  b.getTitle();
        if (convertView == null) {
            LayoutInflater linflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(linflater).inflate(R.layout.card_request_root, null);
        }

        TextView lblListHeader =  convertView.findViewById(R.id.bookname);
        TextView number =  convertView.findViewById(R.id.number);
        ImageView civ = convertView.findViewById(R.id.bookphoto);
        //settare foto libro se c'è DOPO AVER SETTATO QUELLA DI DEFAULT
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.default_book);
        requestOptions.error(R.drawable.default_book);
        //if(book.getUrlimage()!=null && book.getUrlimage().length()!=0){
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(b.getUrlimage()).into(civ);
       /* GlideApp.with(context)
                .load(R.drawable.default_book)
                .centerCrop()
                .into(civ);*/
        //lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        //setto numerino richieste
        String count = String.valueOf(getChildrenCount(groupPosition));
        number.setText(String.valueOf(count));
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void onClickYes( Request request ){

        FirebaseManagement.getDatabase().getReference()
                .child("requests")
                .child(request.getrId())
                .child("requestStatus")
                .setValue(AppConstants.ACCEPTED)
                .addOnCompleteListener(task -> {
                    Gson gson = new Gson();
                    try {
                        request.setRequestStatus(AppConstants.ACCEPTED);
                        algoReqIndex.saveObjectAsync(new JSONObject(gson.toJson(request)),
                                request.getObjectID().toString(),
                                ( jsonObject, e ) -> {

                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        FirebaseManagement.getDatabase().getReference()
                .child("books")
                .child(request.getbId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Book book = dataSnapshot.getValue(Book.class);

                        if(book != null) {

                            FirebaseManagement.getDatabase().getReference()
                                    .child("books")
                                    .child(request.getbId())
                                    .child("stato")
                                    .setValue(AppConstants.NOT_AVAILABLE)
                                    .addOnCompleteListener(task -> {
                                        Gson gson = new Gson();

                                        try {
                                            book.setStato(AppConstants.NOT_AVAILABLE);
                                            book.setHolder(request.getRenterId());
                                            FirebaseManagement.getDatabase().getReference()
                                                    .child("users")
                                                    .child(book.getHolder())
                                                    .child("lent")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange( DataSnapshot dataSnapshot ) {
                                                            Long value =(Long) dataSnapshot.getValue();
                                                            if(value!=null) {
                                                                value = value + 1;
                                                                dataSnapshot.getRef().setValue(value);
                                                                FirebaseManagement.sendMessage(context.getResources().getString(R.string.notify_accepted_request),FirebaseManagement.getUser().getDisplayName(),book.getHolder(),1);
                                                            }else{
                                                                value = 1L;
                                                                dataSnapshot.getRef().setValue(value);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled( DatabaseError databaseError ) {

                                                        }
                                                    });
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

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });

                            FirebaseManagement.getDatabase().getReference()
                                    .child("books")
                                    .child(request.getbId())
                                    .child("holder")
                                    .setValue(request.getRenterId()).addOnSuccessListener(aVoid -> {
                                        List<Request> l=listChild.get(request.getbId());
                                        l.clear();
                                        listChild.put(request.getbId(),l);
                                       ExpandableListAdapter.super.notifyDataSetChanged();
                                        //RequestActivity.refresh(context);
                                    });

                            FirebaseManagement.getDatabase().getReference()
                                    .child("users")
                                    .child(request.getRenterId())
                                    .child("reqNotified")
                                    .setValue(true);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    private void onClickNo( Request request ){
        FirebaseManagement.sendMessage(context.getResources().getString(R.string.notify_refused_request),FirebaseManagement.getUser().getDisplayName(),request.getRenterId(),1);

        FirebaseManagement.getDatabase().getReference()
                .child("requests")
                .child(request.getrId())
                .child("requestStatus")
                .setValue(AppConstants.REFUSED)
                .addOnCompleteListener(task -> {
                    Gson gson = new Gson();
                    try {
                        request.setRequestStatus(AppConstants.REFUSED);
                        algoReqIndex.saveObjectAsync(new JSONObject(gson.toJson(request)),
                                request.getObjectID().toString(),
                                ( jsonObject, e ) -> {

                                    List<Request> l=listChild.get(request.getbId());
                                    l.remove(request);
                                    listChild.put(request.getbId(),l);
                                    ExpandableListAdapter.super.notifyDataSetChanged();
                                });

                        FirebaseManagement.getDatabase().getReference()
                                .child("users")
                                .child(request.getRenterId())
                                .child("reqNotified")
                                .setValue(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

    }

    private void onClickProfile(Request request){
        Intent intent = new Intent(context, ShowUserProfile.class);
        intent.putExtra("userId", request.getRenterId());
        context.startActivity(intent);
    }

}
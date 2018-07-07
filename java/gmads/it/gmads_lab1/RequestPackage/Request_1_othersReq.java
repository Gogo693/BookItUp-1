
package gmads.it.gmads_lab1.RequestPackage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import gmads.it.gmads_lab1.constants.AppConstants;
import gmads.it.gmads_lab1.BookPackage.Book;
import gmads.it.gmads_lab1.ToolsPackege.ExpandableListAdapter;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.R;

public class Request_1_othersReq extends Fragment {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<Book> listDataHeader;
    ViewPager tab;
    HashMap<String, List<Request>> listDataChild;
    Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
    Index algoIndex = algoClient.getIndex("requests");

    public void setViewPager(ViewPager vp){
        tab=vp;
    }
    public Request_1_othersReq() {

    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // preparing list data
        View root = inflater.inflate(R.layout.fragment_list_others_request, container, false);
        expListView = root.findViewById(R.id.explv);
        prepareListData();


        return root;
    }
    public void prepareListDataonCreate() {



        Query query = new Query("").setFilters("ownerId:" + FirebaseManagement.getUser().getUid() + " AND ( requestStatus:"
                + AppConstants.PENDING + " OR requestStatus:" + AppConstants.ACCEPTED+" )")
                .setHitsPerPage(100);

        algoIndex.searchAsync(query, ( jsonObject, e ) -> {
            if(e==null){
                boolean ifAccepted;
                SearchRequestsJsonParser parser=new  SearchRequestsJsonParser();
                List<Request> listrequest=parser.parseResults(jsonObject);

                listDataChild = new HashMap<>();
                listDataHeader= new ArrayList<>();
                Book tempBook;
                for(Request rr : listrequest){

                    if(!listDataChild.containsKey(rr.getbId())){
                        tempBook = new Book();
                        tempBook.setUrlimage(rr.getUrlBookImage());
                        tempBook.setBId(rr.getbId());
                        tempBook.setTitle(rr.getbName());
                        listDataHeader.add(tempBook);
                        listDataChild.put(rr.getbId(), new ArrayList<>());

                    }

                    listDataChild.get(rr.getbId()).add(rr);
                }
                List <String> keys= new ArrayList<>();
                for(String key : listDataChild.keySet()){
                    List<Request> list2 = listDataChild.get(key);
                    ifAccepted = false;
                    for (Request request : list2){
                        if(request.getRequestStatus() == AppConstants.ACCEPTED){
                            ifAccepted = true;
                        }
                    }
                    if(ifAccepted){
                        keys.add(key);

                    }
                }
                for(String key : keys) {
                    listDataChild.remove(key);
                    Book a=new Book();
                    Boolean taken=false;
                    for(Book b : listDataHeader){
                        if(b.getBId().equals(key)){
                            taken=true;
                            a=b;
                        }
                    }
                    if(taken) {
                        listDataHeader.remove(a);
                        taken=false;
                    }
                }
                if(listDataChild.size()==0){
                    tab.setCurrentItem(1);

                   // Toast.makeText(getContext(),"nessuna richiesta ricevuta",Toast.LENGTH_LONG).show();
                    return;
                }else{
                    ImageView notfound = Objects.requireNonNull(getActivity()).findViewById(R.id.not_found2);
                    TextView tnf = getActivity().findViewById(R.id.textnotfound2);
                    ExpandableListView exp= getActivity().findViewById(R.id.explv);
                       // notfound.setVisibility(View.GONE);
                       // tnf.setVisibility(View.GONE);
                        exp.setVisibility(View.VISIBLE);

                }
                listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

                // setting list adapter
                expListView.setAdapter(listAdapter);
                // Listview Group click listener
                expListView.setOnGroupClickListener(( parent, v, groupPosition, id ) -> {
                    //Toast.makeText(getApplicationContext(),"Group Clicked " + listDataHeader.get(groupPosition),Toast.LENGTH_SHORT).show();
                    //parent.expandGroup(groupPosition);
                    return false;
                });

                // Listview Group expanded listener
                expListView.setOnGroupExpandListener(groupPosition -> {
                    //Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " Expanded", Toast.LENGTH_SHORT).show();
                });

                // Listview Group collapsed listener
                expListView.setOnGroupCollapseListener(groupPosition -> {
                    //Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition) + " Collapsed",Toast.LENGTH_SHORT).show();
                });

            }else{

            }

        });

    }

    public void prepareListData() {



        Query query = new Query("").setFilters("ownerId:" + FirebaseManagement.getUser().getUid() + " AND ( requestStatus:"
                + AppConstants.PENDING + " OR requestStatus:" + AppConstants.ACCEPTED+" )")
                .setHitsPerPage(100);

        algoIndex.searchAsync(query, ( jsonObject, e ) -> {
            if(e==null){
                boolean ifAccepted;
                SearchRequestsJsonParser parser=new  SearchRequestsJsonParser();
                List<Request> listrequest=parser.parseResults(jsonObject);

                listDataChild = new HashMap<>();
                listDataHeader= new ArrayList<>();
                Book tempBook;
                for(Request rr : listrequest){

                    if(!listDataChild.containsKey(rr.getbId())){
                        tempBook = new Book();
                        tempBook.setUrlimage(rr.getUrlBookImage());
                        tempBook.setBId(rr.getbId());
                        tempBook.setTitle(rr.getbName());
                        listDataHeader.add(tempBook);
                        listDataChild.put(rr.getbId(), new ArrayList<>());

                    }

                    listDataChild.get(rr.getbId()).add(rr);
                }
                List <String> keys= new ArrayList<>();
                for(String key : listDataChild.keySet()){
                    List<Request> list2 = listDataChild.get(key);
                    ifAccepted = false;
                    for (Request request : list2){
                        if(request.getRequestStatus() == AppConstants.ACCEPTED){
                            ifAccepted = true;
                        }
                    }
                    if(ifAccepted){
                        keys.add(key);

                    }
                }
                for(String key : keys) {
                    listDataChild.remove(key);
                    Book a=new Book();
                    Boolean taken=false;
                    for(Book b : listDataHeader){
                        if(b.getBId().equals(key)){
                            taken=true;
                            a=b;
                        }
                    }
                    if(taken) {
                        listDataHeader.remove(a);
                        taken=false;
                    }
                }
                ImageView notfound = Objects.requireNonNull(getActivity()).findViewById(R.id.not_found2);
                TextView tnf = getActivity().findViewById(R.id.textnotfound2);
                ExpandableListView exp= getActivity().findViewById(R.id.explv);
                if(listDataChild.size()==0){
                    notfound.setVisibility(View.VISIBLE);
                    tnf.setVisibility(View.VISIBLE);
                    exp.setVisibility(View.INVISIBLE);
                    return;
                }
                else{
                    notfound.setVisibility(View.GONE);
                    tnf.setVisibility(View.GONE);
                    exp.setVisibility(View.VISIBLE);
                }

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

                // setting list adapter
                expListView.setAdapter(listAdapter);
                // Listview Group click listener
                expListView.setOnGroupClickListener(( parent, v, groupPosition, id ) -> {
                    //Toast.makeText(getApplicationContext(),"Group Clicked " + listDataHeader.get(groupPosition),Toast.LENGTH_SHORT).show();
                    //parent.expandGroup(groupPosition);
                    return false;
                });

                // Listview Group expanded listener
                expListView.setOnGroupExpandListener(groupPosition -> {
                    //Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " Expanded", Toast.LENGTH_SHORT).show();
                });

                // Listview Group collapsed listener
                expListView.setOnGroupCollapseListener(groupPosition -> {
                    //Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition) + " Collapsed",Toast.LENGTH_SHORT).show();
                });

            }else{

            }

        });

    }

}

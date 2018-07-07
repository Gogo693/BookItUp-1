package gmads.it.gmads_lab1.RequestPackage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.R;

public class Request_2_myReq extends Fragment {
    RecyclerView recycle;
    private RequestAdapter adapter;
    private List<Request> requests;
    ViewPager tab;
    Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
    Index algoIndex = algoClient.getIndex("requests");

    public void setViewPager(ViewPager vp){
        tab=vp;
    }
    public Request_2_myReq() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View root = inflater.inflate(R.layout.fragment_home_1, container, false);
        recycle =  root.findViewById(R.id.recycler_view);
        requests = new ArrayList<>();
        adapter = new RequestAdapter(getContext(),requests);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recycle.setLayoutManager(mLayoutManager);
        recycle.setAdapter(adapter);
        prepareRequest();

        // Inflate the layout for this fragment
        return root;
    }

    public void updateData(List<Request> list){
        requests.clear();
        requests = list;
        adapter.notifyDataSetChanged();
    }

    public RequestAdapter getAdapter() {
        return adapter;
    }

    /**
     * Adding few albums for testing
     */
    public void prepareRequest() {

        Query query = new Query("").setFilters("renterId:" + FirebaseManagement.getUser().getUid())
                .setHitsPerPage(100);

        algoIndex.searchAsync(query, ( jsonObject, e ) -> {
            if(e==null) {
                SearchRequestsJsonParser parser = new SearchRequestsJsonParser();
                List<Request> listrequest = parser.parseResults(jsonObject);
                adapter.setbooks(listrequest);
                adapter.notifyDataSetChanged();
                ImageView notfound = Objects.requireNonNull(getActivity()).findViewById(R.id.not_found2);
                TextView tnf = getActivity().findViewById(R.id.textnotfound2);
                if(listrequest.size()==0){
                    if(tab.getCurrentItem()==1) {
                        notfound.setVisibility(View.VISIBLE);
                        tnf.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                else{
                    notfound.setVisibility(View.GONE);
                    tnf.setVisibility(View.GONE);
                }
            }
        });



    }
}


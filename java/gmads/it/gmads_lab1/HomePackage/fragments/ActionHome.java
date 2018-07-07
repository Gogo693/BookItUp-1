
package gmads.it.gmads_lab1.HomePackage.fragments;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.algolia.search.saas.AbstractQuery;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import gmads.it.gmads_lab1.BookPackage.BookAdapter;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.BookPackage.SearchResultsJsonParser;
import gmads.it.gmads_lab1.BookPackage.Book;
import gmads.it.gmads_lab1.UserPackage.Profile;

public class ActionHome extends Fragment {
    RecyclerView recycle;
    private BookAdapter adapter;
    private List<Book> bookList;
    private boolean isscrolling=false;
    private int currentItems;
    private int totalItems;
    private int scrollOutItems;
    private GridLayoutManager manager;
    private Profile profile;
    private int npage=0;




    private String text;
    Client algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
    Index algoIndex = algoClient.getIndex("BookIndex");
    public int getNpage() {
        return npage;
    }

    public void setNpage( int npage ) {
        this.npage = npage;
    }
    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList( List<Book> bookList ) {
        this.bookList = bookList;
    }
    public Profile getProfile() {
        return profile;
    }

    public void setProfile( Profile profile ) {
        this.profile = profile;
    }

    public String getText() {
        return text;
    }

    public void setText( String text ) {
        this.text = text;
    }
    public ActionHome() {

    }
    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View root = inflater.inflate(R.layout.fragment_home_1, container, false);
        recycle =  root.findViewById(R.id.recycler_view);
        bookList = new ArrayList<>();
        adapter = new BookAdapter(getContext(), bookList);

        manager = new GridLayoutManager(getContext(), 2);

        recycle.setLayoutManager(manager);
        recycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged( RecyclerView recyclerView, int newState ) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isscrolling=true;
                }
            }

            @Override
            public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems= manager.getChildCount();
                totalItems=manager.getItemCount();
                scrollOutItems=manager.findFirstVisibleItemPosition();
                if(isscrolling && (currentItems+scrollOutItems==totalItems)){
                    isscrolling=false;
                    fetchdata();
                    npage++;
                }
            }
        });
        recycle.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(), true));
        recycle.setItemAnimator(new DefaultItemAnimator());
        prepareBooks();
        recycle.setAdapter(adapter);

        // Inflate the layout for this fragment
        return root;
    }

    public void fetchdata() {
        Query query = new Query(text)
                .setAroundLatLng(new AbstractQuery.LatLng(profile.getLat(), profile.getLng())).setGetRankingInfo(true).setPage(npage)
                .setHitsPerPage(20).setFilters("categories:action OR categories:azione");

        algoIndex.searchAsync(query, ( jsonObject, e ) -> {
            ImageView notfound = Objects.requireNonNull(getActivity()).findViewById(R.id.not_found);
            TextView tnf = getActivity().findViewById(R.id.textnotfound);
            ProgressBar p= Objects.requireNonNull(getActivity()).findViewById(R.id.progress_bar);
            if(e==null){
                SearchResultsJsonParser search= new SearchResultsJsonParser();
                Log.d("lista",jsonObject.toString());
                bookList.addAll(search.parseResults(jsonObject));
                Collections.sort(bookList, ( o1, o2 ) -> (int)o1.getDistance()-(int)o2.getDistance());
                List<Book> books2= new ArrayList<>();
                for (Book b : bookList) {
                    if (b.getOwner().equals(FirebaseManagement.getUser().getUid())) {
                        books2.add(b);
                    }
                }
                for(Book b: books2){
                    bookList.remove(b);
                }

                if(bookList.size()==0){

                    notfound.setVisibility(View.VISIBLE);
                    tnf.setVisibility(View.VISIBLE);
                }
                else{
                    notfound.setVisibility(View.GONE);
                    tnf.setVisibility(View.GONE);
                }
            }else{
                notfound.setVisibility(View.VISIBLE);
                tnf.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();

            p.setVisibility(View.INVISIBLE);
        });
    }
    public void updateData(List<Book> books){
        bookList.clear();
        bookList = books;
        adapter.notifyDataSetChanged();
    }

    public BookAdapter getAdapter() {
        return adapter;
    }

    /**
     * Adding few albums for testing
     */
    private void prepareBooks() {


        adapter.notifyDataSetChanged();
    }

    public void clearlist() {
        bookList.clear();
        npage=0;
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

         GridSpacingItemDecoration( int spanCount, int spacing, boolean includeEdge ) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets( Rect outRect, View view, RecyclerView parent, RecyclerView.State state ) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx() {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics()));
    }
}
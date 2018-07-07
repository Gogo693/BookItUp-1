package gmads.it.gmads_lab1.Map.common.model;

import com.algolia.search.saas.AbstractQuery;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import gmads.it.gmads_lab1.BookPackage.Book;
import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.BookPackage.SearchResultsJsonParser;

public class LibraryProvider {
    private final static String JSON_PATH = "bali.json";

    private static LibraryProvider sInstance;
    Client algoClient;
    Index algoIndex;
    private Library mLibrary;

    private LibraryProvider() {

    }

    public static LibraryProvider instance() {
        if(sInstance == null) {
            sInstance = new LibraryProvider();
            return sInstance;
        }
        return sInstance;
    }

    public void initialize(String query,Double lat,Double lng,int num) {
        algoClient = new Client("L6B7L7WXZW", "9d2de9e724fa9289953e6b2d5ec978a5");
        algoIndex = algoClient.getIndex("BookIndex");

        //algoIndex.searchAsync(new Query(query), ( jsonObject, e ) -> {
              // SearchResultsJsonParser search= new SearchResultsJsonParser();
            //    Log.d("lista",jsonObject.toString());
              //  mLibrary= new Library();
               // mLibrary.setBookList(search.parseResults(jsonObject));
           // }
        //);mLibrary
     //  while (mLibrary==null||mLibrary.isEmpty()){
       //    Log.d("waiting","waiting");
       //}
        //
        mLibrary =new Library();
        Thread t= new Thread(() -> {/*
           algoIndex.searchAsync(new Query(query), new CompletionHandler() {
                @Override
                public void requestCompleted( JSONObject jsonObject, AlgoliaException e ) {
                    SearchResultsJsonParser search= new SearchResultsJsonParser();
                    mLibrary.setBookList(search.parseResults(jsonObject));
                    return;
                }
            });*/
            SearchResultsJsonParser search= new SearchResultsJsonParser();
            try {
                List<Book> books = search.parseResults(algoIndex.searchSync(new Query(query).setAroundLatLng(new AbstractQuery.LatLng(lat,lng)).setGetRankingInfo(true).setHitsPerPage(20*num)));
                List<Book> books2= new ArrayList<>();
                for (Book b : books) {
                    if (b.getOwner().equals(FirebaseManagement.getUser().getUid())) {
                        books2.add(b);
                    }
                    else{
                        b.setfinder(lat,lng);
                    }
                }
                for(Book b: books2){
                    books.remove(b);
                }
                mLibrary.setBookList(books);
            }catch (Exception e){
                e.printStackTrace();
            }

        });
        t.start();
        try {
            t.join();
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    public LatLngBounds provideLatLngBoundsForAllPlaces() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Book place : mLibrary.getBookList()) {
            builder.include(new LatLng(place.get_geoloc().getLat(), place.get_geoloc().getLng()));
        }
        return builder.build();
    }

    public List<Book> providePlacesList() {
        return mLibrary.getBookList();
    }

    public double getLatByPosition(final int position) {
        return mLibrary.getBookList().get(position).get_geoloc().getLat();
    }

    public double getLngByPosition(final int position) {
        return mLibrary.getBookList().get(position).get_geoloc().getLng();
    }
}

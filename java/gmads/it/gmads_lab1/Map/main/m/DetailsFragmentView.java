package gmads.it.gmads_lab1.Map.main.m;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import java.util.ArrayList;
import java.util.List;

import gmads.it.gmads_lab1.BookPackage.Book;
import gmads.it.gmads_lab1.Map.common.mvp.MvpView;

public interface DetailsFragmentView extends MvpView {
    void drawPolylinesOnMap(ArrayList<LatLng> decode);

    void provideBaliData(List<Book> places);

    void onBackPressedWithScene(LatLngBounds latLngBounds);

    void moveMapAndAddMaker(LatLngBounds latLngBounds);

    void updateMapZoomAndRegion(LatLng northeastLatLng, LatLng southwestLatLng);
}
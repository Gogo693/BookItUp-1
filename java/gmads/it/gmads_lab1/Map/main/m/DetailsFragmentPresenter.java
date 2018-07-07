package gmads.it.gmads_lab1.Map.main.m;


import com.google.android.gms.maps.model.LatLng;

import gmads.it.gmads_lab1.Map.common.mvp.MvpPresenter;

public interface DetailsFragmentPresenter extends MvpPresenter<DetailsFragmentView> {

    void drawRoute(LatLng first, final int position);

    void provideBaliData();

    void onBackPressedWithScene();

    void moveMapAndAddMarker();
}
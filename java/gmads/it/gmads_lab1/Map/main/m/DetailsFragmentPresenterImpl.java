package gmads.it.gmads_lab1.Map.main.m;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.maps.android.PolyUtil;
import gmads.it.gmads_lab1.Map.common.maps.MapsUtil;
import gmads.it.gmads_lab1.Map.common.model.LibraryProvider;
import gmads.it.gmads_lab1.Map.common.model.Bounds;
import gmads.it.gmads_lab1.Map.common.model.DirectionsResponse;
import gmads.it.gmads_lab1.Map.common.model.MapsApiManager;
import gmads.it.gmads_lab1.Map.common.model.Route;
import gmads.it.gmads_lab1.Map.common.mvp.MvpPresenterImpl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetailsFragmentPresenterImpl extends MvpPresenterImpl<DetailsFragmentView> implements DetailsFragmentPresenter {

    private MapsApiManager mapsApiManager = MapsApiManager.instance();
    private LibraryProvider libraryProvider = LibraryProvider.instance();

    @Override
    public void drawRoute(final LatLng first, final int position) {
        final LatLng second = new LatLng(libraryProvider.getLatByPosition(position), libraryProvider.getLngByPosition(position));
        mapsApiManager.getRoute(first, second, new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                List<Route> routes = new Gson().fromJson(response.body().charStream(), DirectionsResponse.class).getRoutes();
                if(!routes.isEmpty()){
                    Route route=routes.get(0);
                    providePolylineToDraw(route.getOverviewPolyline().getPoints());
                    updateMapZoomAndRegion(route.getBounds());
                }
            }
        });
    }

    @Override
    public void provideBaliData() {
        getView().provideBaliData(libraryProvider.providePlacesList());
    }

    @Override
    public void onBackPressedWithScene() {
        getView().onBackPressedWithScene(libraryProvider.provideLatLngBoundsForAllPlaces());
    }

    @Override
    public void moveMapAndAddMarker() {
        getView().moveMapAndAddMaker(libraryProvider.provideLatLngBoundsForAllPlaces());
    }

    private void updateMapZoomAndRegion(final Bounds bounds) {
        bounds.getSouthwest().setLat(MapsUtil.increaseLatitude(bounds));
        getView().updateMapZoomAndRegion(bounds.getNortheastLatLng(), bounds.getSouthwestLatLng());
    }

    private void providePolylineToDraw(final String points) {
        getView().drawPolylinesOnMap(new ArrayList<>(PolyUtil.decode(points)));
    }
}

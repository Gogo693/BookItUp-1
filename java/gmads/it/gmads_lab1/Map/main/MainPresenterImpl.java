package gmads.it.gmads_lab1.Map.main;

import android.graphics.Bitmap;

import gmads.it.gmads_lab1.Map.common.maps.MapBitmapCache;
import gmads.it.gmads_lab1.Map.common.model.LibraryProvider;
import gmads.it.gmads_lab1.Map.common.mvp.MvpPresenterImpl;


public class MainPresenterImpl extends MvpPresenterImpl<MainView> implements MainPresenter {
    @Override
    public void saveBitmap(final Bitmap bitmap) {
        MapBitmapCache.instance().putBitmap(bitmap);
    }

    @Override
    public void provideMapLatLngBounds() {
        getView().setMapLatLngBounds(LibraryProvider.instance().provideLatLngBoundsForAllPlaces());
    }
}

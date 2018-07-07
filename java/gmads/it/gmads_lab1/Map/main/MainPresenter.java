package gmads.it.gmads_lab1.Map.main;

import android.graphics.Bitmap;
import gmads.it.gmads_lab1.Map.common.mvp.MvpPresenter;


public interface MainPresenter extends MvpPresenter<MainView> {
    void saveBitmap(Bitmap googleMap);

    void provideMapLatLngBounds();
}

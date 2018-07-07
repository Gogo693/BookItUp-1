package gmads.it.gmads_lab1.Map.main;


import com.google.android.gms.maps.model.LatLngBounds;
import gmads.it.gmads_lab1.Map.common.mvp.MvpView;

public interface MainView extends MvpView {
    void setMapLatLngBounds(final LatLngBounds latLngBounds);
}

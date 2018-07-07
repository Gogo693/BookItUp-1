package gmads.it.gmads_lab1.Map.main;
import android.os.Bundle;

import gmads.it.gmads_lab1.Map.common.maps.MapsUtil;
import gmads.it.gmads_lab1.Map.common.model.LibraryProvider;
import gmads.it.gmads_lab1.Map.common.mvp.MvpActivity;
import gmads.it.gmads_lab1.Map.common.mvp.MvpFragment;
import gmads.it.gmads_lab1.Map.main.m.DetailsFragment;
import gmads.it.gmads_lab1.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapActivity extends MvpActivity<MainView, MainPresenter> implements MainView, OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private LatLngBounds mapLatLngBounds;

    @Override
    protected MainPresenter createPresenter() {
        return new MainPresenterImpl();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_map_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Double lat= getIntent().getDoubleExtra("lat",0.0);
        Double lng=getIntent().getDoubleExtra("lng",0.0);
        String query=getIntent().getStringExtra("query");
        int num=getIntent().getIntExtra("numpages",0);
        LibraryProvider.instance().initialize(query,lat,lng,num);

        presenter.provideMapLatLngBounds();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, DetailsFragment.newInstance(this), DetailsFragment.TAG)
                .addToBackStack(DetailsFragment.TAG)
                .commit();
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }
/*
    @OnClick(R.id.explore)
    public void onItemExploreClicked() {
        if(getSupportFragmentManager().findFragmentByTag(DetailsFragment.TAG) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.container, DetailsFragment.newInstance(this), DetailsFragment.TAG)
                    .addToBackStack(DetailsFragment.TAG)
                    .commit();
        }
    }
*/
public void onClickShowBook(String bid){

}
    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            triggerFragmentBackPress(getSupportFragmentManager().getBackStackEntryCount());
            if(getSupportFragmentManager().getBackStackEntryCount()==0){
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    public void setMapLatLngBounds(final LatLngBounds latLngBounds) {
        mapLatLngBounds = latLngBounds;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                mapLatLngBounds,
                MapsUtil.calculateWidth(getWindowManager()),
                MapsUtil.calculateHeight(getWindowManager(), getResources().getDimensionPixelSize(R.dimen.map_margin_bottom)), 150));
        googleMap.setOnMapLoadedCallback(() -> googleMap.snapshot(presenter::saveBitmap));
    }

    private void triggerFragmentBackPress(final int count) {
        ((MvpFragment)getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager().getBackStackEntryAt(count - 1).getName())).onBackPressed();
    }

    public void superOnBackPressed() {
        super.onBackPressed();
    }
}
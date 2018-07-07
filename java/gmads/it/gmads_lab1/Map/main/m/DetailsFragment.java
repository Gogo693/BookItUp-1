package gmads.it.gmads_lab1.Map.main.m;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import gmads.it.gmads_lab1.BookPackage.Book;
import gmads.it.gmads_lab1.Map.common.maps.MapBitmapCache;
import gmads.it.gmads_lab1.Map.common.maps.PulseOverlayLayout;
import gmads.it.gmads_lab1.Map.common.mvp.MvpFragment;
import gmads.it.gmads_lab1.Map.common.transitions.ScaleDownImageTransition;
import gmads.it.gmads_lab1.Map.common.transitions.TransitionUtils;
import gmads.it.gmads_lab1.Map.common.views.HorizontalRecyclerViewScrollListener;
import gmads.it.gmads_lab1.Map.common.views.TranslateItemAnimator;
import gmads.it.gmads_lab1.Map.main.MapActivity;
import gmads.it.gmads_lab1.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends MvpFragment<DetailsFragmentView, DetailsFragmentPresenter>
        implements DetailsFragmentView, OnMapReadyCallback, SearchBooksAdapter.OnPlaceClickListener, HorizontalRecyclerViewScrollListener.OnItemCoverListener {
    public static final String TAG = DetailsFragment.class.getSimpleName();

    @BindView(R.id.recyclerview) RecyclerView recyclerView;
    @BindView(R.id.container) FrameLayout containerLayout;
    @BindView(R.id.mapOverlayLayout)
    PulseOverlayLayout mapOverlayLayout;
    private List<Book> booklist;
    private SearchBooksAdapter libraryAdapter;
    private String currentTransitionName;
    private Scene detailsScene;
    private Double lat;
    private Double lng;
    public Double getLat() {
        return lat;
    }

    public void setLat( Double lat ) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng( Double lng ) {
        this.lng = lng;
    }


    public static Fragment newInstance(final Context ctx) {
        DetailsFragment fragment = new DetailsFragment();
        ScaleDownImageTransition transition = new ScaleDownImageTransition(ctx, MapBitmapCache.instance().getBitmap());
        transition.addTarget(ctx.getString(R.string.mapPlaceholderTransition));
        transition.setDuration(600);
        fragment.setEnterTransition(transition);
        return fragment;
    }

    @Override
    protected DetailsFragmentPresenter createPresenter() {
        return new DetailsFragmentPresenterImpl();
    }

    @Override
    public void onBackPressed() {
        if (detailsScene != null) {
            presenter.onBackPressedWithScene();
        } else {
            ((MapActivity) getActivity()).superOnBackPressed();
        }
    }

    private View getSharedViewByPosition(final int childPosition) {
        recyclerView.getAdapter().notifyDataSetChanged();
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            if (childPosition == recyclerView.getChildAdapterPosition(recyclerView.getChildAt(i))) {
                return recyclerView.getChildAt(i);
            }
        }
        return recyclerView.getChildAt(recyclerView.getChildCount()-1);
    }

    @Override
    public int getLayout() {
        return R.layout.layout_map_recycle;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupBaliData();
        setupMapFragment();
        setupRecyclerView();
    }

    private void setupBaliData() {
        presenter.provideBaliData();
    }

    private void setupMapFragment() {
        ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.mapFragment)).getMapAsync(this);
    }

    private void setupRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        libraryAdapter = new SearchBooksAdapter(this, getActivity());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mapOverlayLayout.setupMap(googleMap,booklist.get(0));
        setupGoogleMap();
        addDataToRecyclerView();
    }

    private void setupGoogleMap() {
        presenter.moveMapAndAddMarker();
    }

    private void addDataToRecyclerView() {
        recyclerView.setItemAnimator(new TranslateItemAnimator());
        recyclerView.setAdapter(libraryAdapter);
        libraryAdapter.setBooksList(booklist);
        recyclerView.addOnScrollListener(new HorizontalRecyclerViewScrollListener(this));
    }

    @Override
    public void onPlaceClicked(final View sharedView, final String transitionName, final int position) {
        currentTransitionName = transitionName;
        detailsScene = DetailsLayout.showScene(getActivity(), containerLayout, sharedView, transitionName, booklist.get(position));
        drawRoute(position);
        hideAllMarkers();
    }

    private void drawRoute(final int position) {
        presenter.drawRoute(mapOverlayLayout.getCurrentLatLng(), position);
    }

    private void hideAllMarkers() {
        mapOverlayLayout.setOnCameraIdleListener(null);
        mapOverlayLayout.hideAllMarkers();
    }

    @Override
    public void drawPolylinesOnMap(final ArrayList<LatLng> polylines) {
        getActivity().runOnUiThread(() -> mapOverlayLayout.addPolyline(polylines));
    }

    @Override
    public void provideBaliData(final List<Book> places) {
        booklist = places;
    }

    @Override
    public void onBackPressedWithScene(final LatLngBounds latLngBounds) {
        int childPosition = TransitionUtils.getItemPositionFromTransition(currentTransitionName);
        DetailsLayout.hideScene(getActivity(), containerLayout, getSharedViewByPosition(childPosition), currentTransitionName);
        notifyLayoutAfterBackPress(childPosition);
        mapOverlayLayout.onBackPressed(latLngBounds);
        detailsScene = null;
    }

    private void notifyLayoutAfterBackPress(final int childPosition) {
        containerLayout.removeAllViews();
        containerLayout.addView(recyclerView);
        recyclerView.requestLayout();
        libraryAdapter.notifyItemChanged(childPosition);
    }

    @Override
    public void moveMapAndAddMaker(final LatLngBounds latLngBounds) {
        mapOverlayLayout.moveCamera(latLngBounds);
        mapOverlayLayout.setOnCameraIdleListener(() -> {
            for (int i = 0; i < booklist.size(); i++) {
                mapOverlayLayout.createAndShowMarker(i, new LatLng(booklist.get(i).get_geoloc().getLat(), booklist.get(i).get_geoloc().getLng()));
            }
            mapOverlayLayout.setOnCameraIdleListener(null);
        });
        mapOverlayLayout.setOnCameraMoveListener(mapOverlayLayout::refresh);
    }

    @Override
    public void updateMapZoomAndRegion(final LatLng northeastLatLng, final LatLng southwestLatLng) {
        getActivity().runOnUiThread(() -> {
            mapOverlayLayout.animateCamera(new LatLngBounds(southwestLatLng, northeastLatLng));
            mapOverlayLayout.setOnCameraIdleListener(() -> mapOverlayLayout.drawStartAndFinishMarker());
        });
    }

    @Override
    public void onItemCover(final int position) {
        mapOverlayLayout.showMarker(position);
    }
}
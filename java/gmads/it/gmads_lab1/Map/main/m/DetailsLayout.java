package gmads.it.gmads_lab1.Map.main.m;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import gmads.it.gmads_lab1.BookPackage.Book;
import gmads.it.gmads_lab1.BookPackage.ShowBook;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.UserPackage.ShowUserProfile;


public class DetailsLayout extends CoordinatorLayout {

    @BindView(R.id.cardview) CardView cardViewContainer;
    @BindView(R.id.headerImage) ImageView imageViewPlaceDetails;
    @BindView(R.id.title) TextView textViewTitle;
    @BindView(R.id.description) TextView textViewDescription;
    @BindView(R.id.tobook) TextView toBook;
    @BindView(R.id.toprofile) TextView toProfile;

    String bid;
    public DetailsLayout(final Context context) {
        this(context, null);
    }

    public DetailsLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    private void setData(Book place) {
        if(place.getUrlimage()!=null && place.getUrlimage().length()!=0){
            Glide.with(getContext()).load(place.getUrlimage()).into(imageViewPlaceDetails);
        }
        else {
            imageViewPlaceDetails.setImageDrawable(getContext().getDrawable(R.drawable.default_book));
        }
        textViewTitle.setText(place.getTitle());
        bid=place.getBId();
        textViewDescription.setText(place.getNomeproprietario());
        toBook.setOnClickListener(v->{
            Intent i=new Intent(getContext(), ShowBook.class);
            i.putExtra("book_id",bid);
            getContext().startActivity(i);
        });

        toProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ShowUserProfile.class);
                i.putExtra("userId", place.getOwner());
                getContext().startActivity(i);
            }
        });
    }

    public static Scene showScene(Activity activity, final ViewGroup container, final View sharedView, final String transitionName, final Book data) {
        DetailsLayout detailsLayout = (DetailsLayout) activity.getLayoutInflater().inflate(R.layout.item_book_map_big, container, false);
        detailsLayout.setData(data);

        TransitionSet set = new ShowDetailsTransitionSet(activity, transitionName, sharedView, detailsLayout);
        Scene scene = new Scene(container, (View) detailsLayout);
        TransitionManager.go(scene, set);
        return scene;
    }

    public static Scene hideScene(Activity activity, final ViewGroup container, final View sharedView, final String transitionName) {
        DetailsLayout detailsLayout = (DetailsLayout) container.findViewById(R.id.bali_details_container);

        TransitionSet set = new HideDetailsTransitionSet(activity, transitionName, sharedView, detailsLayout);
        Scene scene = new Scene(container, (View) detailsLayout);
        TransitionManager.go(scene, set);
        return scene;
    }
}
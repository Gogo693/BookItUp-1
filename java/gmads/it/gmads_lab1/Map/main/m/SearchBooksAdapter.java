package gmads.it.gmads_lab1.Map.main.m;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import gmads.it.gmads_lab1.BookPackage.Book;
import gmads.it.gmads_lab1.Map.common.transitions.TransitionUtils;
import gmads.it.gmads_lab1.R;

import java.util.ArrayList;
import java.util.List;

class SearchBooksAdapter extends RecyclerView.Adapter<SearchBooksAdapter.LibraryViewHolder> {

    private final OnPlaceClickListener listener;
    private Context context;
    private List<Book> BookList = new ArrayList<>();

    SearchBooksAdapter( OnPlaceClickListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    @Override
    public LibraryViewHolder onCreateViewHolder( final ViewGroup parent, final int viewType) {
        return new LibraryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_map_small, parent, false));
    }

    @Override
    public void onBindViewHolder( final LibraryViewHolder holder, final int position) {
        holder.title.setText(BookList.get(position).getTitle());
        holder.owner.setText(String.valueOf(context.getString(R.string.of)+ ": " +BookList.get(position).getNomeproprietario()));
        if(BookList.get(position).getUrlimage()!=null && BookList.get(position).getUrlimage().length()!=0){
            Glide.with(context).load(BookList.get(position).getUrlimage()).into( holder.BookPhoto);
        }else{
            Glide.with(context).load(context.getDrawable(R.drawable.default_book)).into(holder.BookPhoto);
        }
        //holder.BookPhoto.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_camera));
        holder.root.setOnClickListener(view -> listener.onPlaceClicked(holder.root, TransitionUtils.getRecyclerViewTransitionName(position), position));
        holder.number.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return BookList.size();
    }

    void setBooksList( List<Book> placesList) {
        BookList = placesList;
        for (int i = 0; i < BookList.size(); i++) {
            notifyItemInserted(i);
        }
    }

    interface OnPlaceClickListener {
        void onPlaceClicked(View sharedView, String transitionName, final int position);
    }

    static class LibraryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title) TextView title;
        @BindView(R.id.owner2) TextView owner;
        @BindView(R.id.root) CardView root;
        @BindView(R.id.headerImage) ImageView BookPhoto;
        @BindView(R.id.number) TextView number;
        LibraryViewHolder( final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
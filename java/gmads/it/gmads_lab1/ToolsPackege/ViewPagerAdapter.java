package gmads.it.gmads_lab1.ToolsPackege;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import gmads.it.gmads_lab1.R;

public class ViewPagerAdapter extends PagerAdapter{
    Activity activity;
    List<String> images;
    LayoutInflater inflater;
    StorageReference sr;
    public ViewPagerAdapter(Activity activity,List<String> images){

        this.activity=activity;
        this.images=images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject( @NonNull View view, @NonNull Object object ) {
        return view==object;
    }
    public Object instantiateItem( ViewGroup container,int position){
        inflater= (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView= inflater.inflate(R.layout.viewpager_item,container,false);
        ImageView image;
        image = (ImageView)itemView.findViewById(R.id.imageView2);
        DisplayMetrics dis= new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
        int height = dis.heightPixels;
        int width= dis.widthPixels;
        image.setMinimumHeight(height);
        image.setMinimumWidth(width);

        try{
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.ic_add_book_24dp);
            requestOptions.error(R.drawable.pencil_mod);
            Glide.with(activity.getApplicationContext())
                    .load(images.get(position))
                    .apply(requestOptions)
                    .into(image);
        }catch(Exception e){

        }
        container.addView(itemView);
        return itemView;
    }
    public  void destroyItem(ViewGroup container,int position,Object object){
        ((ViewPager)container).removeView((View)object);
    }
    public void addUrl(String url){
        images.add(url);
    }

}

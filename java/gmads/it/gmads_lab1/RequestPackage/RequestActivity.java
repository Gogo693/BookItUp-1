package gmads.it.gmads_lab1.RequestPackage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import java.util.Objects;

import gmads.it.gmads_lab1.FirebasePackage.FirebaseManagement;
import gmads.it.gmads_lab1.R;
import gmads.it.gmads_lab1.ToolsPackege.FragmentViewPagerAdapter;

public class RequestActivity extends AppCompatActivity{

    ExpandableListView expListView;
    TabLayout tab;
    ViewPager pager;
    Request_1_othersReq r1=new Request_1_othersReq();
    Request_2_myReq r2=new Request_2_myReq();

    private void setViews() {
        expListView = findViewById(R.id.explv);
        tab=findViewById(R.id.tabs);
        pager= findViewById(R.id.viewPager);
    }

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_home);
        setContentView(R.layout.activity_request);
        setViews();
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.request));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FragmentViewPagerAdapter vadapter = new FragmentViewPagerAdapter(getSupportFragmentManager());
        vadapter.addFragment(r1);
        vadapter.addFragment(r2);
        r1.setViewPager(pager);
        r2.setViewPager(pager);
        pager.setAdapter(vadapter);
        tab.setupWithViewPager(pager);
        r1.prepareListDataonCreate();
        Objects.requireNonNull(tab.getTabAt(0)).setText(getText(R.string.others_req));
        Objects.requireNonNull(tab.getTabAt(1)).setText(getText(R.string.my_req));

        FirebaseManagement.getDatabase().getReference()
                .child("users")
                .child(FirebaseManagement.getUser().getUid())
                .child("reqNotified")
                .setValue(false);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {

            }

            @Override
            public void onPageSelected( int position ) {
                switch (position) {
                    case 0:
                        r1.prepareListData();
                        break;
                    case 1:
                        r2.prepareRequest();
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged( int state ) {

            }
        });
    }

    public static void refresh(Context context){
        Intent intent = new Intent(context, RequestActivity.class);
        context.startActivity(intent);
    }

    public void onClickYes(View v){

        Toast.makeText(getApplicationContext(), R.string.request_accepted,Toast.LENGTH_LONG).show();
    }

    public void onClickNo(View v){

        Toast.makeText(getApplicationContext(), R.string.request_refused,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

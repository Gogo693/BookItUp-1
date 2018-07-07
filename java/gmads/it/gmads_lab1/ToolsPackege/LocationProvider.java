package gmads.it.gmads_lab1.ToolsPackege;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.content.pm.PackageManager;
import android.widget.Toast;

import gmads.it.gmads_lab1.ToolsPackege.PermissionManager;


public class LocationProvider extends Activity{

    private LocationManager lm;
    boolean gps_enabled=false;
    boolean network_enabled=false;
    private Location location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(lm==null)
            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        getLocation(this);
    }

    public void getLocation(Context context) {

        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        Location net_loc=null, gps_loc=null;

        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        if(!gps_enabled && !network_enabled)
            finish();

        if(!(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED)) {

            Intent intent = new Intent(this, PermissionManager.class);
            startActivityForResult(intent, 1);
        } else {
            if(gps_enabled) {
                try {
                    gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch (SecurityException e) {
                }
            }
            if(network_enabled) {
                try {
                    net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                } catch (SecurityException e) {
                }
            }

            if(gps_loc!=null && net_loc!=null){
                if(gps_loc.getTime() > net_loc.getTime()) {
                    returnIntent.putExtra("location", gps_loc);
                    this.finish();
                }
                else {
                    returnIntent.putExtra("location", net_loc);
                    this.finish();
                }
            }

            if(gps_loc!=null){
                returnIntent.putExtra("location", gps_loc);
                this.finish();
            }
            if(net_loc!=null) {
                returnIntent.putExtra("location", net_loc);
                this.finish();
            }

            setResult(Activity.RESULT_CANCELED,returnIntent);
            this.finish();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            Intent returnIntent = new Intent();

            if(resultCode == Activity.RESULT_OK){
                Location net_loc=null, gps_loc=null;
                if(gps_enabled) {
                    try {
                        gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    } catch (SecurityException e) {
                    }
                }
                if(network_enabled) {
                    try {
                        net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    } catch (SecurityException e) {
                    }
                }


                setResult(Activity.RESULT_OK,returnIntent);
                returnIntent.putExtra("location", gps_loc);
                this.finish();



                if(gps_loc!=null && net_loc!=null){
                    if(gps_loc.getTime() > net_loc.getTime()) {
                        returnIntent.putExtra("location", gps_loc);
                        this.finish();
                    }
                    else {
                        returnIntent.putExtra("location", net_loc);
                        this.finish();
                    }
                }

                if(gps_loc!=null){
                    returnIntent.putExtra("location", gps_loc);
                    this.finish();
                }
                if(net_loc!=null) {
                    returnIntent.putExtra("location", net_loc);
                    this.finish();
                }

                setResult(Activity.RESULT_CANCELED,returnIntent);
                this.finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Permission not granted, insert manually", Toast.LENGTH_LONG).show();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                this.finish();
            }
        }
    }//onActivityResult
}

package gmads.it.gmads_lab1.Map.common;

import android.app.Application;

import gmads.it.gmads_lab1.Map.common.model.LibraryProvider;
import gmads.it.gmads_lab1.Map.common.model.MapsApiManager;
import gmads.it.gmads_lab1.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class WorkcationApp extends Application {

    private static WorkcationApp sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        MapsApiManager.instance().initialize();
        LibraryProvider.instance().initialize("",0.0,0.0,0);
        initCalligraphy();
    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static WorkcationApp getInstance() {
        return sInstance;
    }
}
package gmads.it.gmads_lab1.ToolsPackege;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;
import java.util.Objects;

public class Tools extends AppCompatActivity {

    public Tools(){

    }


    public boolean isOnline(Context c) {
        ConnectivityManager conMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(conMgr).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected() && netInfo.isAvailable();
    }

    public AlertDialog.Builder showPopup(Activity element, String title, String msg1, String msg2) {
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(element);
        TextView msg = new TextView(element);
        msg.setText(title);
        msg.setGravity(Gravity.CENTER);
        alertDlg.setView(msg);
        alertDlg.setCancelable(true);
        alertDlg.setPositiveButton(msg1, (dialog, which) -> {});
        alertDlg.setNegativeButton(msg2, (dialog, which) -> {});
        return alertDlg;
    }

}

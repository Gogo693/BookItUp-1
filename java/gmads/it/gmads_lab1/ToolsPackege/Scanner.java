package gmads.it.gmads_lab1.ToolsPackege;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import gmads.it.gmads_lab1.R;

public class Scanner extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        // Use builder
        mCodeScanner = CodeScanner.builder()
                /*camera can be specified by calling .camera(cameraId),
                first back-facing camera on the device by default*/
                /*code formats*/
                .formats(CodeScanner.ALL_FORMATS)/*List<BarcodeFormat>*/
                /*or .formats(BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX, ...)*/
                /*or .format(BarcodeFormat.QR_CODE) - only one format*/
                /*auto focus*/
                .autoFocus(true).autoFocusMode(AutoFocusMode.SAFE).autoFocusInterval(2000L)
                /*flash*/
                .flash(false)
                /*decode callback*/
                .onDecoded(result -> runOnUiThread(() -> {
                    Toast.makeText(Scanner.this, result.getText(), Toast.LENGTH_LONG).show();
                    Intent data = new Intent();
                    data.setData(Uri.parse(result.getText()));
                    setResult(RESULT_OK, data);
                    finish();
                }))
                /*error callback*/
                .onError(error -> runOnUiThread(() -> Toast.makeText(Scanner.this, error.getMessage(),
                        Toast.LENGTH_LONG).show())).build(this, scannerView);
        // Or use constructor to create scanner with default parameters
        // All parameters can be changed after scanner created
        // mCodeScanner = new CodeScanner(this, scannerView);
        // mCodeScanner.setDecodeCallback(...);
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
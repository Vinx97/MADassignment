package my.edu.tarc.madassignment.studentMenu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.core.CameraPreview;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import my.edu.tarc.madassignment.R;

public class cameraPreviewActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("subjectid", rawResult.toString());
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}

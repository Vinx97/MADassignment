package my.edu.tarc.madassignment.studentSubjectAction;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import my.edu.tarc.madassignment.R;

public class displayQRCodeActivity extends AppCompatActivity {

    String subjectID;

    ImageView QRCodeImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        final Context context = this;
        QRCodeImageView = (ImageView) findViewById(R.id.QRCodeImgStudent);

        //Bundle extra = getIntent().getExtras();
        //subjectID = extra.getString("clickedsubject");
        subjectID = getIntent().getExtras().getString("subjectid");

        MultiFormatWriter multiFormWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormWriter.encode(subjectID, BarcodeFormat.QR_CODE, 260, 260);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qrcodebitmap = barcodeEncoder.createBitmap(bitMatrix);
            QRCodeImageView.setImageBitmap(qrcodebitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }


}

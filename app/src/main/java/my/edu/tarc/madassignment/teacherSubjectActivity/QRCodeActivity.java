package my.edu.tarc.madassignment.teacherSubjectActivity;

/**
 * Created by ASUS on 19/11/2017.
 */
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import my.edu.tarc.madassignment.R;
import my.edu.tarc.madassignment.entities.Subject;


public class QRCodeActivity extends Fragment{

    private ImageView qrcode;
    private String subjectid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_join_code, container, false);
        //((teacherSubjectActionActivity) getActivity()).hideFloatingActionButton();
        //((teacherSubjectActionActivity) getActivity()).hideFloatingActionButton();
        qrcode = (ImageView)rootView.findViewById(R.id.QRCodeImgTeacher);

        Subject selectedSubject = (Subject)getActivity().getIntent().getSerializableExtra("clickedsubject");
        subjectid=selectedSubject.getId();

        MultiFormatWriter multiFormWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormWriter.encode(subjectid, BarcodeFormat.QR_CODE, 260, 260);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qrcodebitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrcode.setImageBitmap(qrcodebitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }


        return rootView;
        //return null;

    }

    @Override
    public void onResume() {
        super.onResume();
    }


}

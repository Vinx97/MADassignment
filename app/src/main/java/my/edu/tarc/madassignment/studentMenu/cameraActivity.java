package my.edu.tarc.madassignment.studentMenu;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.qrcode.encoder.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import my.edu.tarc.madassignment.MainActivity;
import my.edu.tarc.madassignment.R;
import my.edu.tarc.madassignment.entities.Subject;
import my.edu.tarc.madassignment.studentSubjectAction.studentSubjectActionActivity;

import static android.app.Activity.RESULT_OK;

public class cameraActivity extends Fragment implements ZXingScannerView.ResultHandler{

    private ZXingScannerView zXingScannerView;
    //TextView scanDescriptionTextView;
    Button scanButton;
    final int requestCameraPermissionID = 1001;
    String subjectID;
    String studentID;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_camera2, container, false);

        scanButton = (Button)rootView.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), cameraPreviewActivity.class);
                startActivityForResult(intent, 1011);
            }
        });
        studentID = ((studentMenuActivity) getActivity()).getUserid();
        return rootView;
    }

    @Override
    public void handleResult(Result result) {
        zXingScannerView.stopCamera();
        zXingScannerView.stopCameraPreview();
        subjectID = result.getText();
        String url = getString(R.string.enrol_url);
        addNewStudent(getActivity(), url, subjectID, studentID);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if(requestCode == 1011 && resultCode == RESULT_OK){
            String subjectid = result.getExtras().getString("subjectid");
            String url = getString(R.string.enrol_url);
            addNewStudent(getActivity(), url, subjectid, studentID);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void addNewStudent(final Context context, String url, final String subjectID, final String studentID) {  //context: what context called this
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);
        ProgressDialog progressDialog;

        //Send data
        try {
            progressDialog = new ProgressDialog(getActivity());
            if (!progressDialog.isShowing())
                progressDialog.setMessage("Sync with server...");
            progressDialog.setTitle(getString(R.string.processing));
            progressDialog.setMessage(getString(R.string.pleasewait));
            progressDialog.show();

            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success==1) {
                                    Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();   //CRASH
                                    Intent intent = new Intent(getActivity(), studentSubjectActionActivity.class);
                                    intent.putExtra("clickedsubject", subjectID);
                                    startActivity(intent);
                                }else if(success==0){
                                    Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("subjectid", subjectID);
                    params.put("studentid", studentID);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

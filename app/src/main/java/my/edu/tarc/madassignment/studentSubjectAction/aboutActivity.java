package my.edu.tarc.madassignment.studentSubjectAction;

/**
 * Created by ASUS on 18/11/2017.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import my.edu.tarc.madassignment.R;

public class aboutActivity extends Fragment{
    private static final int RESULT_OK = 0;
    private  String subjectid, studentid;
    private TextView tname, temail;
    private String name, email;
    private Button unenrol;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        tname = (TextView)rootView.findViewById(R.id.textViewteacherName);
        temail = (TextView)rootView.findViewById(R.id.textViewteacherEmail);
        unenrol = (Button)rootView.findViewById(R.id.UnenrollButton);
        Button StudentQRCodeButton = (Button)rootView.findViewById(R.id.StudentQRCodeButton);
        String selectedSubject = getActivity().getIntent().getExtras().getString("clickedsubject");
        subjectid = selectedSubject;
        studentid = getActivity().getIntent().getExtras().getString("stud_id");

        unenrol.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm Unenrol?");
                //final EditText input = new EditText(getActivity());
                //input.setInputType(InputType.TYPE_CLASS_TEXT);
                //builder.setView(input);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        unenrol(getActivity().getApplicationContext(), "http://tarucclassroom.000webhostapp.com/unenrol.php");
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();


            }
        });

        StudentQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), displayQRCodeActivity.class);
                intent.putExtra("subjectid", subjectid);
                startActivity(intent);


            }
        });
        if(!isConnected()){
            Toast.makeText(getActivity().getApplicationContext(), "No Network Access", Toast.LENGTH_LONG).show();
        }
        getTeacherInfo(getActivity().getApplicationContext(), "http://tarucclassroom.000webhostapp.com/select_teacher_info.php");


        return rootView;
    }
    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    private void getTeacherInfo(Context context, String url){
        RequestQueue queue = Volley.newRequestQueue(context);

        /*if (!pDialog.isShowing())
            pDialog.setMessage("Syn with server...");
        pDialog.show();*/

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray myArray = new JSONArray(response);

                            for (int i = 0; i < myArray.length(); i++) {
                                JSONObject infoResponse = myArray.getJSONObject(i);
                                name = infoResponse.getString("teacher_name");
                                email = infoResponse.getString("teacher_email");
                            }
                            tname.setText(name);
                            temail.setText(email);
                            /*if (pDialog.isShowing())
                                pDialog.dismiss();*/
                        }catch (Exception e) {
                            Toast.makeText(getContext(), "About Screen Error 2: " + e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "About Screen Error 3 : "+error.getMessage()+"   "+subjectid, Toast.LENGTH_LONG).show();
                        //if(pDialog.isShowing())
                        //  pDialog.dismiss();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("user_id", "1001");
                params.put("subjectid", subjectid);
                return params;
            }
        };

        //jsonObjectRequest.setTag(TAG);

        queue.add(jsonObjectRequest);
    }

    private void unenrol(final Context context, String url){
        RequestQueue queue = Volley.newRequestQueue(context);

        try{
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try{
                                jsonObject = new JSONObject(response);
                                //subjectid = jsonObject.getString("subjectid");
                                int success = jsonObject.getInt("success");
                                String message  = jsonObject.getString("message");
                                if(success == 0){
                                    Toast.makeText(getContext(), "Unenrol  "+ message, Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                    //Intent intent = new Intent();
                                    //getActivity().setResult(RESULT_OK,intent);
                                    getActivity().finish();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                    },

                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Toast.makeText(getContext(), "Unenrol Error. "+ error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("subject_id", subjectid);
                    params.put("student_id", studentid);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        }catch (Exception e){
            e.printStackTrace();

        }
    }

}

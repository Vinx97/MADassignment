package my.edu.tarc.madassignment.teacherMenu;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import my.edu.tarc.madassignment.R;

public class AddSubjectActivity extends AppCompatActivity {
    EditText editTextSN;
    private String userid;
    private String subjectid;
    private int subid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        editTextSN = (EditText) findViewById(R.id.editTextSN);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            userid = extras.getString("id");

        } else {
            userid = (String) savedInstanceState.getSerializable("id");
        }
        //Toast.makeText(getApplicationContext(),userid,Toast.LENGTH_LONG).show();

    }

    public void addSubject(View v) {
        String subjectName;

        subjectName = editTextSN.getText().toString();

        if(subjectName.isEmpty()){
            editTextSN.setError("Please enter the subject name!");
            return;
        }

        try{
            addSubject(this, "http://tarucclassroom.000webhostapp.com/insert_subject.php", subjectName);
            //subjectid = String.format(String.valueOf(subid));
            //subjectid = String.format("%d", subid);
            ;
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
    public void addSubject(final Context context, String url, final String subjectName){
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
                                subjectid = jsonObject.getString("subjectid");
                                //Toast.makeText(getApplicationContext(), subjectid, Toast.LENGTH_LONG).show();
                                int success = jsonObject.getInt("success");
                                String message  = jsonObject.getString("message");
                                if(success == 0){
                                    Toast.makeText(getApplicationContext(), "Add subject"+ message, Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    addTeacherSubject(context, "http://tarucclassroom.000webhostapp.com/insert_teachersubject.php");
                                    //finish();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                            }
                        },

                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Toast.makeText(getApplicationContext(), "Error. "+ error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("SUBJECT_NAME", subjectName);
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
    public void addTeacherSubject(Context context, String url){
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
                                int success = jsonObject.getInt("success");
                                String message  = jsonObject.getString("message");
                                if(success == 0){
                                    Toast.makeText(getApplicationContext(), "add teacher subject"+message, Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                    },

                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Toast.makeText(getApplicationContext(), "Error. "+ error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("subjectid", subjectid);
                    params.put("teacherid", userid);
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

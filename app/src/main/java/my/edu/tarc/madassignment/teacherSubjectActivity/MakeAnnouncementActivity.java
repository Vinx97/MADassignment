package my.edu.tarc.madassignment.teacherSubjectActivity;

import android.annotation.SuppressLint;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import my.edu.tarc.madassignment.R;

public class MakeAnnouncementActivity extends AppCompatActivity {
    EditText editTextAnnouncement;
    Calendar c = Calendar.getInstance();
    private String subjectId;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = df.format(c.getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_announcement);

        editTextAnnouncement = (EditText) findViewById(R.id.editTextAnnouncement);
        //teacherSubjectActionActivity main = new teacherSubjectActionActivity();
        //subjectid = main.getSubjectId();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            subjectId = extras.getString("clickedsubject");

        } else {
            subjectId = (String) savedInstanceState.getSerializable("clickedsubject");
        }
    }

    public void postAnnouncement(View v){
        String content;
        String date;

        content = editTextAnnouncement.getText().toString();
        date = formattedDate;
        if(content.isEmpty()){
            editTextAnnouncement.setError("Please type in the announcement!");
            return;
        }
        try{
            makeServiceCall(this, "http://tarucclassroom.000webhostapp.com/insert_announcement.php", content, date,subjectId);

        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error 1 : "+ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void makeServiceCall(Context context, String url, final String content, final String date, final String subid){
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
                                String message = jsonObject.getString("message");

                                if(success == 0){
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    finish();

                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error 2: "+error.toString()+"   "+subjectId, Toast.LENGTH_LONG).show();

                        }
                    }) {

                @Override
                protected Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<>();
                    params.put("content", content);
                    params.put("date", date);
                    params.put("subjectid", subid);
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

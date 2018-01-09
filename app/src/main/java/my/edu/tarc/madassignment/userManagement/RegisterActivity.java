package my.edu.tarc.madassignment.userManagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
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

import my.edu.tarc.madassignment.MainActivity;
import my.edu.tarc.madassignment.R;
import my.edu.tarc.madassignment.entities.register;


public class RegisterActivity extends AppCompatActivity {
    EditText editTextName, editTextEmail, editTextPass;
    CheckBox tchcheckbox, stdcheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = (EditText) findViewById(R.id.getnametxt);
        editTextEmail = (EditText) findViewById(R.id.getemailtxt);
        editTextPass = (EditText) findViewById(R.id.getpasstxt);

        tchcheckbox = (CheckBox)findViewById(R.id.tchcheckBox);
        stdcheckbox = (CheckBox)findViewById(R.id.stdcheckbox);
    }

    public void saveRecord(View v) {
        register reg = new register();
        String name, email, pass;
        String type = new String();

        name = editTextName.getText().toString();
        if (name.isEmpty()) {
            editTextName.setError("Please enter Full Name");
            return;
        }
        email = editTextEmail.getText().toString();
        if (email.isEmpty()) {
            editTextEmail.setError("Please enter Email");
            return;
        }
        pass = editTextPass.getText().toString();
        if (pass.isEmpty()) {
            editTextPass.setError("Please enter Password");
            return;
        }
        if(tchcheckbox.isChecked()){
            type = tchcheckbox.getText().toString();
        }else{
            type = stdcheckbox.getText().toString();
        }


        reg.setEmail(email);
        reg.setPass(pass);
        reg.setName(name);
        reg.setType(type);

        try {
            makeServiceCall(this, getString(R.string.insert_register_url), reg);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public void makeServiceCall(Context context, String url, final register reg) {
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success == 0) {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", reg.getEmail());
                    params.put("password", reg.getPass());
                    params.put("Fullname", reg.getName());
                    params.put("type", reg.getType());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void BackToMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }

}

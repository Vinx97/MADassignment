package my.edu.tarc.madassignment.userManagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import my.edu.tarc.madassignment.MainActivity;
import my.edu.tarc.madassignment.R;

public class ResetPassActivity extends AppCompatActivity {
    EditText editTextPass, editTextEmail;
    Button reset;
    String Password, Email;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        editTextEmail = (EditText) findViewById(R.id.resetemail);
        editTextPass = (EditText) findViewById(R.id.resetpass);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Password = editTextPass.getText().toString();
                if (Password.isEmpty()) {
                    editTextPass.setError("Please enter Password");
                    return;
                }
                Email = editTextEmail.getText().toString();
                if (Email.isEmpty()) {
                    editTextEmail.setError("Please enter Email");
                    return;
                }
                Background b = new Background();
                b.execute(Password, Email);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    class Background extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String password = params[0];
            String email = params[1];
            String data = "";
            int tmp;

            try {
                URL url = new URL("https://tarucclassroom.000webhostapp.com/reset_password.php");
                String urlparams = "password=" + password + "&email=" + email;

                HttpURLConnection hurl = (HttpURLConnection) url.openConnection();
                hurl.setDoOutput(true);
                OutputStream os = hurl.getOutputStream();
                os.write(urlparams.getBytes());
                os.flush();
                os.close();

                InputStream is = hurl.getInputStream();
                while ((tmp = is.read()) != -1) {
                    data += (char) tmp;
                }
                is.close();
                hurl.disconnect();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return data;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("")) {
                s = "Data not updated";
            } else {
                Toast.makeText(ResetPassActivity.this, "Data Updated Successfully !", Toast.LENGTH_SHORT).show();

            }
        }
    }
    public void loginbtn (View v){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }
}
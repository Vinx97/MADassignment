package my.edu.tarc.madassignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getActionBar().setTitle("Homepage");

    }

    public void teacherButton(View view){
        Intent intent = new Intent(this, teacherMenuActivity.class);
        startActivity(intent);
    }

    public void studentButton(View view){
        Intent intent = new Intent(this, studentMenuActivity.class);
        startActivity(intent);
    }
}

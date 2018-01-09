package my.edu.tarc.madassignment.teacherMenu;

/**
 * Created by ASUS on 19/11/2017.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import my.edu.tarc.madassignment.R;

public class createSubjectActivity extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_subject, container, false);


        return rootView;
    }
}

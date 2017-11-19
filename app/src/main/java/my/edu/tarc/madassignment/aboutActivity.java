package my.edu.tarc.madassignment;

/**
 * Created by ASUS on 18/11/2017.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class aboutActivity extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        return rootView;
    }

    public void qrButton(View view){
        Intent intent = new Intent(getActivity(), QRCodeActivity.class);
        startActivity(intent);
    }
}

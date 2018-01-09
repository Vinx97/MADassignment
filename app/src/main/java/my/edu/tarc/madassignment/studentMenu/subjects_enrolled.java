package my.edu.tarc.madassignment.studentMenu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.tarc.madassignment.R;
import my.edu.tarc.madassignment.entities.Subject;
import my.edu.tarc.madassignment.studentSubjectAction.studentSubjectActionActivity;

import static android.app.Activity.RESULT_OK;

public class subjects_enrolled extends Fragment{

    List<Subject> enrolledList;
    ListView subjectListView;
    private ProgressDialog pDialog;
    RequestQueue queue;
    private SwipeRefreshLayout srl;
    private String userid;

    //String subjects[] = new String[]{"Computer System Architecture", "Fucking Android", "Web App Development", "Useless Modelling"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_subject_enrolled, container, false);

        userid = ((studentMenuActivity) getActivity()).getUserid();

        subjectListView= (ListView) rootView.findViewById(R.id.subjectLV);
        pDialog = new ProgressDialog(getActivity());
        enrolledList = new ArrayList<>();
        srl = (SwipeRefreshLayout) rootView.findViewById(R.id.srl);

        if (!isConnected()) {
            Toast.makeText(getActivity().getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }

        getEnrolled(getActivity().getApplicationContext(), getString(R.string.select_enrolled_url));
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getEnrolled(getActivity().getApplicationContext(), getString(R.string.select_enrolled_url));
            }
        });
        return rootView;

    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    private void getEnrolled(Context context, String url){
        queue = Volley.newRequestQueue(context);

        if (!pDialog.isShowing())
            pDialog.setMessage("Sync with server...");
        pDialog.show();


        StringRequest getEnrolledRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray myArray = new JSONArray(response);
                    enrolledList.clear();
                    for (int i = 0; i < myArray.length(); i++) {
                        JSONObject enrolledResponse = myArray.getJSONObject(i);
                        String subject_id = enrolledResponse.getString("subject_id");
                        String subject_name = enrolledResponse.getString("subject_name");
                        Subject subject = new Subject(subject_id, subject_name);
                        enrolledList.add(subject);

                    }
                    ArrayAdapter<Subject> adapter = new ArrayAdapter<Subject>(getActivity(), android.R.layout.simple_list_item_1, enrolledList);
                    subjectListView.setAdapter(adapter);
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    srl.setRefreshing(false);
                    subjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Subject clickedSubject = (Subject) subjectListView.getAdapter().getItem(position);   // get clicked object
                            Intent intent = new Intent(getActivity(), studentSubjectActionActivity.class);
                            intent.putExtra("clickedsubject", clickedSubject.getId());
                            intent.putExtra("stud_id", userid);
                            startActivity(intent);
                        }
                    });

                }
                catch(JSONException e){
                    srl.setRefreshing(false);
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        srl.setRefreshing(false);
                        Toast.makeText(getActivity().getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("userid", userid);
                return params;
            }
        };

        queue.add(getEnrolledRequest);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        if(requestCode==1){
            //if(resultCode==RESULT_OK){

            getEnrolled(getActivity().getApplicationContext(), getString(R.string.select_enrolled_url));
            //f}
        }
    }

}

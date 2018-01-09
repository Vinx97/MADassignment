package my.edu.tarc.madassignment.teacherMenu;

/**
 * Created by ASUS on 19/11/2017.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import my.edu.tarc.madassignment.teacherSubjectActivity.teacherSubjectActionActivity;

public class mySubjectActivity extends Fragment{
    List<Subject> enrolledList;
    ListView subjectListView;
    private ProgressDialog pDialog;
    RequestQueue queue;
    //String [] passSubjectId = new String[1000];
    private SwipeRefreshLayout srl;
    private String userid;
    private int attempt=1;

    //String subjects[] = new String[]{"Computer System Architecture", "Fucking Android", "Web App Development", "Useless Modelling"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_my_subject, container, false);

        subjectListView= (ListView) rootView.findViewById(R.id.subjectLV2);
        pDialog = new ProgressDialog(getActivity());
        enrolledList = new ArrayList<>();

        srl = (SwipeRefreshLayout) rootView.findViewById(R.id.srl);

        userid = ((teacherMenuActivity) getActivity()).getUserid();

        if (!isConnected()) {
            Toast.makeText(getActivity().getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }

        getSubjectCreated(getActivity().getApplicationContext(), getString(R.string.select_created_url));

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSubjectCreated(getActivity().getApplicationContext(), getString(R.string.select_created_url));
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

    private void getSubjectCreated(Context context, String url){
        queue = Volley.newRequestQueue(context);

        if(attempt==1) {
            if (!pDialog.isShowing())
                pDialog.setMessage("Sync with server...");
            pDialog.show();
        }

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
                        //passSubjectId[i]=subject_id;

                    }
                    ArrayAdapter<Subject> adapter = new ArrayAdapter<Subject>(getActivity(), android.R.layout.simple_list_item_1, enrolledList);
                    subjectListView.setAdapter(adapter);
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    srl.setRefreshing(false);
                    attempt++;
                    subjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Subject clickedSubject = (Subject) subjectListView.getAdapter().getItem(position);   // get clicked object
                            //String clickedSubject = passSubjectId[position];
                            Intent intent = new Intent(getActivity(), teacherSubjectActionActivity.class);
                            intent.putExtra("clickedsubject", clickedSubject);
                            startActivity(intent);
                        }
                    });

                }
                catch(JSONException e){
                    e.printStackTrace();
                    srl.setRefreshing(false);
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
                //params.put("user_id", "1001");
                params.put("userid", userid);
                return params;
            }
        };

        queue.add(getEnrolledRequest);
    }

}


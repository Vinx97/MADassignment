package my.edu.tarc.madassignment.studentSubjectAction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.tarc.madassignment.R;
import my.edu.tarc.madassignment.entities.Announcement;

/**
 * Created by ASUS on 18/11/2017.
 */

public class announcementActivity extends Fragment{


    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private announcement_rvadapter rvadapter;
    private List<Announcement> announcementList = new ArrayList<Announcement>();
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private SwipeRefreshLayout srl;
    private String subjectid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_announcement, container, false);

        String selectedSubject = getActivity().getIntent().getExtras().getString("clickedsubject");
        subjectid = selectedSubject;
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
        srl = (SwipeRefreshLayout) rootView.findViewById(R.id.srl);
        //recyclerView.setHasFixedSize(true);


        if(!isConnected()){
            Toast.makeText(getActivity().getApplicationContext(), "No Network Access", Toast.LENGTH_LONG).show();
        }
        downloadAnnouncement(getActivity().getApplicationContext(), "http://tarucclassroom.000webhostapp.com/select_announcement.php");

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadAnnouncement(getActivity().getApplicationContext(), "http://tarucclassroom.000webhostapp.com/select_announcement.php");
            }
        });

        return rootView;
    }
    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void downloadAnnouncement(Context context, String url){
        queue = Volley.newRequestQueue(context);

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
                            announcementList.clear();
                            for (int i = 0; i < myArray.length(); i++) {
                                JSONObject announcementResponse = myArray.getJSONObject(i);
                                String id = announcementResponse.getString("id");
                                String content = announcementResponse.getString("content");
                                String date = announcementResponse.getString("date");
                                Announcement announcement = new Announcement(id, content, date);
                                announcementList.add(announcement);

                            }
                            loadAnnouncement();
                            /*if (pDialog.isShowing())
                                pDialog.dismiss();*/
                        }catch (Exception e) {
                            Toast.makeText(getContext(), "Announcement Error 2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            srl.setRefreshing(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Announcement Error 3 : "+error.getMessage()+"   "+subjectid, Toast.LENGTH_LONG).show();
                        //if(pDialog.isShowing())
                        //  pDialog.dismiss();
                        srl.setRefreshing(false);
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
    private void loadAnnouncement(){
        llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        rvadapter = new announcement_rvadapter(getActivity(), announcementList);
        recyclerView.setAdapter(rvadapter);
        srl.setRefreshing(false);
    }


}
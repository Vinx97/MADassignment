package my.edu.tarc.madassignment.teacherSubjectActivity;

/**
 * Created by ASUS on 19/11/2017.
 */
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.tarc.madassignment.R;
import my.edu.tarc.madassignment.entities.Announcement;
import my.edu.tarc.madassignment.entities.Subject;
import my.edu.tarc.madassignment.studentSubjectAction.RecyclerItemClickListener;
import my.edu.tarc.madassignment.studentSubjectAction.announcement_rvadapter;

public class postAnnouncementActivity extends Fragment{

    private LinearLayoutManager llm;
    private announcement_rvadapter rvadapter;
    private List<Announcement> announcementList = new ArrayList<Announcement>();
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private RecyclerView recyclerView;
    float dX;
    float dY;
    int lastAction;
    private SwipeRefreshLayout srl;
    private String subjectid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_post_announcement, container, false);

        //((teacherSubjectActionActivity) getActivity()).showFloatingActionButton();

        Subject selectedSubject = (Subject)getActivity().getIntent().getSerializableExtra("clickedsubject");
        subjectid=selectedSubject.getId();
        //subjectid = ((teacherSubjectActionActivity) getActivity()).getSubjectId();
        //subjectid = ((teacherSubjectActionActivity) ge)

        srl = (SwipeRefreshLayout) rootView.findViewById(R.id.srl);
        //FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);

        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MakeAnnouncementActivity.class);
                startActivity(intent);
            }
        });*/
        //fab.setOnTouchListener(this);

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadAnnouncement(getActivity().getApplicationContext(), "http://tarucclassroom.000webhostapp.com/select_announcement.php");
            }
        });

        recyclerView = (RecyclerView)rootView.findViewById(R.id.reView);
        //recyclerView.setHasFixedSize(true);


        if(!isConnected()){
            Toast.makeText(getActivity().getApplicationContext(), "No Network Access", Toast.LENGTH_LONG).show();
        }
        downloadAnnouncement(getActivity().getApplicationContext(), "http://tarucclassroom.000webhostapp.com/select_announcement.php");

        llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        rvadapter = new announcement_rvadapter(getActivity(), announcementList);
        recyclerView.setAdapter(rvadapter);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
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
            protected Map<String, String> getParams() throws AuthFailureError{
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
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                    }

                    @Override public void onLongItemClick(View view, int position) {

                        announcement_rvadapter ar = (announcement_rvadapter) recyclerView.getAdapter();
                        final Announcement announcement = (Announcement)ar.getItem(position);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Are you sure to delete this message?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAnnouncement(getActivity(), getString(R.string.delete_announcement_url), announcement.getId());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();

                    }
                })
        );
        srl.setRefreshing(false);
    }

    public void deleteAnnouncement(Context context, String url, final String announcementid) {
        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;

                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");

                                if (success == 0) {
                                    Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                }
                                downloadAnnouncement(getActivity().getApplicationContext(), "http://tarucclassroom.000webhostapp.com/select_announcement.php");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error 2: " + error.toString(), Toast.LENGTH_LONG).show();

                        }
                    }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", announcementid);
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

}
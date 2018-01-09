package my.edu.tarc.madassignment.studentSubjectAction;

/**
 * Created by ASUS on 18/11/2017.
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
import my.edu.tarc.madassignment.entities.FileCategory;
import my.edu.tarc.madassignment.entities.Subject;

public class documentActivity extends Fragment{

    ListView fileCategoryListView;
    ProgressDialog progressDialog;
    List<FileCategory> fileCategoryList;
    RequestQueue queue;
    private SwipeRefreshLayout srl;
    String subject_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_document, container, false);

        fileCategoryListView= (ListView) rootView.findViewById(R.id.fileCategoryListView);
        progressDialog = new ProgressDialog(getActivity());
        fileCategoryList = new ArrayList<>();
        srl = (SwipeRefreshLayout) rootView.findViewById(R.id.srl);

        String selectedSubject = getActivity().getIntent().getExtras().getString("clickedsubject");
        subject_id = selectedSubject;

        if (!isConnected()) {
            Toast.makeText(getActivity().getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }

        getFileCategory(getActivity().getApplicationContext(), getString(R.string.select_file_category_url));

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFileCategory(getActivity().getApplicationContext(), getString(R.string.select_file_category_url));
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

    private void getFileCategory(Context context, String url){
        queue = Volley.newRequestQueue(context);

        if (!progressDialog.isShowing())
            progressDialog.setMessage("Sync with server...");
        progressDialog.show();


        StringRequest getFileCategoryRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray myArray = new JSONArray(response);
                    fileCategoryList.clear();
                    for (int i = 0; i < myArray.length(); i++) {
                        JSONObject fileCategoryResponse = myArray.getJSONObject(i);
                        String subject_id = fileCategoryResponse.getString("subject_id");
                        String category_name = fileCategoryResponse.getString("category_name");
                        FileCategory fileCategory = new FileCategory(subject_id, category_name);
                        fileCategoryList.add(fileCategory);

                    }
                    ArrayAdapter<FileCategory> adapter = new ArrayAdapter<FileCategory>(getActivity(), android.R.layout.simple_list_item_1, fileCategoryList);
                    fileCategoryListView.setAdapter(adapter);
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    srl.setRefreshing(false);
                    fileCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            FileCategory clickedFileCategory = (FileCategory) fileCategoryListView.getAdapter().getItem(position);   // get clicked object
                            Intent intent = new Intent(getActivity(), filesActivity.class);
                            intent.putExtra("clickedFileCategory", clickedFileCategory);
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
                params.put("subjectid", subject_id);
                return params;
            }
        };

        queue.add(getFileCategoryRequest);
    }

}


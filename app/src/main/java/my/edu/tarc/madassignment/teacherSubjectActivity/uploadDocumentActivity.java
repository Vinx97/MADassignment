package my.edu.tarc.madassignment.teacherSubjectActivity;

/**
 * Created by ASUS on 19/11/2017.
 */
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.tarc.madassignment.R;
import my.edu.tarc.madassignment.entities.FileCategory;
import my.edu.tarc.madassignment.entities.Subject;
import my.edu.tarc.madassignment.entities.SubjectFile;
import my.edu.tarc.madassignment.studentSubjectAction.filesActivity;

public class uploadDocumentActivity extends Fragment{
    ListView fileCategoryListView;
    ProgressDialog progressDialog;
    List<FileCategory> fileCategoryList;
    RequestQueue queue;
    //FloatingActionButton fab;
    asyncdelete ad;

    String subject_id;
    private SwipeRefreshLayout srl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_upload_document, container, false);

        Subject selectedSubject = (Subject)getActivity().getIntent().getSerializableExtra("clickedsubject");
        subject_id = selectedSubject.getId();
        srl = (SwipeRefreshLayout) rootView.findViewById(R.id.srl);
        fileCategoryListView= (ListView) rootView.findViewById(R.id.teacherFileCategoryListView);
        progressDialog = new ProgressDialog(getActivity());
        fileCategoryList = new ArrayList<>();
        /*fab = (FloatingActionButton)rootView.findViewById(R.id.addCategoryFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("New category");
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String categoryName = input.getText().toString();
                        String url = getString(R.string.insert_category_url);
                        FileCategory fileCategory = new FileCategory(subject_id, categoryName);
                        updateDatabase(getActivity(), url, fileCategory);
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
        });*/

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFileCategory(getActivity().getApplicationContext(), getString(R.string.select_file_category_url));
            }
        });
        if (!isConnected()) {
            Toast.makeText(getActivity().getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }

        getFileCategory(getActivity().getApplicationContext(), getString(R.string.select_file_category_url));

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
                            Intent intent = new Intent(getActivity(), teacherFilesActivity.class);
                            intent.putExtra("clickedFileCategory", clickedFileCategory);
                            startActivity(intent);
                        }
                    });
                    fileCategoryListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                            final FileCategory clickedFileCategory = (FileCategory) fileCategoryListView.getAdapter().getItem(position);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Are you sure to delete " + clickedFileCategory.getFile_category() + "?");
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ad = new asyncdelete(getActivity(), clickedFileCategory);
                                    ad.execute();
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();

                            return true;
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

    private class asyncdelete extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        FileCategory fileCategory;
        private Context context;
        //File file = null;

        public asyncdelete(Context context, FileCategory fileCategory){
            this.context = context;
            this.fileCategory = fileCategory;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(getString(R.string.deleting));
            progressDialog.setMessage(getString(R.string.pleasewait));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            if(!progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            updateDatabase(context,getString(R.string.delete_file_category_url), fileCategory);
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            FTPClient ftpClient = new FTPClient();

            String host = "files.000webhost.com";
            String user = "tarucclassroom";
            String password = "1o1ip0pp";

            try {
                ftpClient.connect(host);
                ftpClient.enterLocalPassiveMode();
                ftpClient.login(user, password);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            try {
                String dir = "/uploads/" + fileCategory.getSubject_id() + "/" + fileCategory.getFile_category();
                FTPFile[] subFiles = ftpClient.listFiles(dir);

                if(subFiles !=null && subFiles.length > 0){
                    for (FTPFile file : subFiles){  //Enhanced for loop
                        String currentFileName = file.getName();
                        ftpClient.deleteFile(dir + "/" + currentFileName);
                    }
                }
                ftpClient.removeDirectory(dir);
            }
            catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }
    }

    public void updateDatabase(Context context, String url, final FileCategory fileCategory) {  //context: what context called this
        //mPostCommentResponse.requestStarted();
        RequestQueue queue = Volley.newRequestQueue(context);
        //Send data
        try {
            StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response);
                        int success = jsonObject.getInt("success");
                        String message = jsonObject.getString("message");
                        if (success==0) {
                            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("subjectid", fileCategory.getSubject_id());
                    params.put("filecategory", fileCategory.getFile_category());
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

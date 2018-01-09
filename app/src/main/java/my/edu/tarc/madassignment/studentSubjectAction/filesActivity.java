package my.edu.tarc.madassignment.studentSubjectAction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.tarc.madassignment.R;
import my.edu.tarc.madassignment.entities.FileCategory;
import my.edu.tarc.madassignment.entities.Subject;
import my.edu.tarc.madassignment.entities.SubjectFile;
import my.edu.tarc.madassignment.teacherSubjectActivity.teacherFilesActivity;

public class filesActivity extends AppCompatActivity {

    ListView fileListView;
    ProgressDialog progressDialog;
    List<SubjectFile> fileList;
    RequestQueue queue;
    asyncdownload ad;

    String subject_id;
    String file_category;
    private SwipeRefreshLayout srl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        fileListView= (ListView)findViewById(R.id.fileListView);
        progressDialog = new ProgressDialog(this);
        fileList = new ArrayList<>();
        srl = (SwipeRefreshLayout) findViewById(R.id.srl);
        FileCategory fileCategory = (FileCategory) getIntent().getSerializableExtra("clickedFileCategory");
        subject_id = fileCategory.getSubject_id();
        file_category = fileCategory.getFile_category();
        Toast.makeText(this.getApplicationContext(), subject_id + " " + file_category, Toast.LENGTH_LONG).show();

        if (!isConnected()) {
            Toast.makeText(this.getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFile(getApplicationContext(), getString(R.string.select_subject_file_url));
            }
        });

        getFile(this.getApplicationContext(), getString(R.string.select_subject_file_url));

    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

     private void getFile(final Context context, String url){
        queue = Volley.newRequestQueue(context);

        if (!progressDialog.isShowing())
            progressDialog.setMessage("Sync with server...");
        progressDialog.show();


        StringRequest getFileRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray myArray = new JSONArray(response);
                    fileList.clear();
                    for (int i = 0; i < myArray.length(); i++) {
                        JSONObject fileResponse = myArray.getJSONObject(i);
                        String subject_id = fileResponse.getString("subject_id");
                        String file_name = fileResponse.getString("file_name");
                        String file_path = fileResponse.getString("file_path");
                        String file_category = fileResponse.getString("file_category");
                        String upload_datetime = fileResponse.getString("upload_datetime");

                        SubjectFile subjectFile = new SubjectFile(subject_id, file_name, file_path, file_category, upload_datetime);
                        fileList.add(subjectFile);

                    }
                    ArrayAdapter<SubjectFile> adapter = new ArrayAdapter<SubjectFile>(filesActivity.this, android.R.layout.simple_list_item_1, fileList);
                    fileListView.setAdapter(adapter);
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    srl.setRefreshing(false);
                    fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            SubjectFile selectedSubjectFile = (SubjectFile) fileListView.getAdapter().getItem(position);   // get clicked object
                            String filePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/TARUC Classroom/" + selectedSubjectFile.getSubject_id() + "/" + selectedSubjectFile.getFile_category()+ "/" + selectedSubjectFile.getFile_name();
                            File file=new File(filePath);
                            if(!file.exists()){
                                ad = new asyncdownload(filesActivity.this, selectedSubjectFile);
                                ad.execute();
                            }
                            else{
                                int dotposition = selectedSubjectFile.getFile_name().lastIndexOf(".");
                                String ext = selectedSubjectFile.getFile_name().substring(dotposition+1, selectedSubjectFile.getFile_name().length());
                                showFile(file, ext);
                            }
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
                        Toast.makeText(context, "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("subjectid", subject_id);
                params.put("filecategory", file_category);
                return params;
            }
        };

        queue.add(getFileRequest);
    }

    private class asyncdownload extends AsyncTask<String, String, String>{
        ProgressDialog progressDialog;
        SubjectFile subjectFile;
        private Context context;
        //File file = null;

        protected  void onProgressUpdate(Integer... values){
            progressDialog.setProgress((int)(values[0]/(float)values[1]*100));
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setProgress(Integer.parseInt(values[0]));
        }

        public asyncdownload(Context context, SubjectFile subjectFile){
            this.context = context;
            this.subjectFile = subjectFile;
        }

         @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Downloading");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ad.cancel(true);
                }
            });
            if(!progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();

            String filePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/TARUC Classroom/" + subjectFile.getSubject_id() + "/" + subjectFile.getFile_category()+ "/" + subjectFile.getFile_name();
            File file=new File(filePath);
            int dotposition = subjectFile.getFile_name().lastIndexOf(".");
            String ext = subjectFile.getFile_name().substring(dotposition+1, subjectFile.getFile_name().length());
            showFile(file, ext);
        }

        @Override
        protected String doInBackground(String... strings) {
            FTPClient ftpClient = new FTPClient();



            String host = "files.000webhost.com";
            String user = "tarucclassroom";
            String password = "1o1ip0pp";
            //file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/TARUC Classroom/" +subjectFile.getFile_name());

            try {
                ftpClient.connect(host);
                ftpClient.enterLocalPassiveMode();
                ftpClient.login(user, password);
            }
            catch(IOException e){
                e.printStackTrace();
            }

            String downloadPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/TARUC Classroom/" + subjectFile.getSubject_id() + "/" + subjectFile.getFile_category();
            File file=new File(downloadPath);
            if(!file.exists()){
                file.mkdirs();
            }

            final OutputStream outputStream;

            try{
                outputStream = new BufferedOutputStream(new FileOutputStream(file + "/" + subjectFile.getFile_name()));
                try {
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                    ftpClient.retrieveFile(subjectFile.getFile_path(), outputStream);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                finally {
                    if(outputStream != null){
                        try{
                            outputStream.close();
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }

            }
            catch(FileNotFoundException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void showFile(File file, String extension){
        String type="application/";
        if((extension.equals("pdf") || extension.equals("docx") || extension.equals("doc") || extension.equals("ppt") || extension.equals("pptx"))&& android.os.Build.VERSION.SDK_INT < 24){
            type+=extension;
            PackageManager packageManager = getPackageManager();
            Intent testIntent = new Intent(Intent.ACTION_VIEW);
            testIntent.setType(type);
            List list = packageManager.queryIntentActivities(testIntent, packageManager.MATCH_DEFAULT_ONLY);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, type);
            startActivity(intent);
        }
        else if((extension.equals("pdf") || extension.equals("docx") || extension.equals("doc") || extension.equals("ppt") || extension.equals("pptx"))&& android.os.Build.VERSION.SDK_INT >=24){
            type+=extension;
            PackageManager packageManager = getPackageManager();
            Intent testIntent = new Intent(Intent.ACTION_VIEW);
            testIntent.setType(type);
            List list = packageManager.queryIntentActivities(testIntent, packageManager.MATCH_DEFAULT_ONLY);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(getApplication(), getApplication().getApplicationContext().getPackageName() + ".my.package.name.provider", file);
            intent.setDataAndType(uri, type);
            startActivity(intent);

        }
        else
            Toast.makeText(getApplicationContext(), getString(R.string.fileformatnotsupported), Toast.LENGTH_LONG).show();

    }
}

package my.edu.tarc.madassignment.teacherSubjectActivity;

import android.app.AlertDialog;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
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
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.tarc.madassignment.R;
import my.edu.tarc.madassignment.entities.FileCategory;
import my.edu.tarc.madassignment.entities.SubjectFile;
import my.edu.tarc.madassignment.studentSubjectAction.filesActivity;

public class teacherFilesActivity extends AppCompatActivity {

    ListView fileListView;
    ProgressDialog progressDialog;
    List<SubjectFile> fileList;
    RequestQueue queue;
    asyncdownload ad;
    asyncupload au;
    asyncdelete ade;

    FloatingActionButton fileUpoadFAB;
    String subjectID;
    String fileCategory;
    String filePath;
    String fileName;
    private SwipeRefreshLayout srl;
    //private String subjectID = "1000";
    //private String fileCategory = "Tutorial";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_files);

        fileUpoadFAB = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        fileListView= (ListView)findViewById(R.id.teacherFileListView);
        progressDialog = new ProgressDialog(this);
        fileList = new ArrayList<>();

        FileCategory file_category = (FileCategory) getIntent().getSerializableExtra("clickedFileCategory");
        subjectID = file_category.getSubject_id();
        fileCategory = file_category.getFile_category();
        srl = (SwipeRefreshLayout) findViewById(R.id.srl);
        Toast.makeText(this.getApplicationContext(), subjectID + " " + file_category, Toast.LENGTH_LONG).show();


        fileUpoadFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(teacherFilesActivity.this)
                        .withRequestCode(1010)
                        .start();
            }
        });
        if (!isConnected()) {
            Toast.makeText(this.getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }

        getFile(this.getApplicationContext(), getString(R.string.select_subject_file_url));

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFile(getApplicationContext(), getString(R.string.select_subject_file_url));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1010 && resultCode == RESULT_OK) {
            filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            File selectedFile = new File(filePath);  //to get name
            fileName = selectedFile.getName();
            String uploadDir = "/uploads/" + subjectID + "/" + fileCategory + "/" + fileName;
            au = new asyncupload(this);
            au.execute();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    public void uploadFileOnclick(View v){
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1010)
                .start();

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
                    ArrayAdapter<SubjectFile> adapter = new ArrayAdapter<SubjectFile>(teacherFilesActivity.this, android.R.layout.simple_list_item_1, fileList);
                    fileListView.setAdapter(adapter);
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    srl.setRefreshing(false);
                    fileListView.setLongClickable(true);
                    fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            SubjectFile selectedSubjectFile = (SubjectFile) fileListView.getAdapter().getItem(position);   // get clicked object
                            String filePath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/TARUC Classroom/" + selectedSubjectFile.getSubject_id() + "/" + selectedSubjectFile.getFile_category()+ "/" + selectedSubjectFile.getFile_name();
                            File file=new File(filePath);
                            if(!file.exists()){
                                ad = new asyncdownload(teacherFilesActivity.this, selectedSubjectFile);
                                ad.execute();
                            }
                            else{
                                int dotposition = selectedSubjectFile.getFile_name().lastIndexOf(".");
                                String ext = selectedSubjectFile.getFile_name().substring(dotposition+1, selectedSubjectFile.getFile_name().length());
                                showFile(file, ext);
                            }
                        }
                    });

                    fileListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                            final SubjectFile selectedSubjectFile = (SubjectFile) fileListView.getAdapter().getItem(position);

                            AlertDialog.Builder builder = new AlertDialog.Builder(teacherFilesActivity.this);
                            builder.setTitle("Are you sure to delete " + selectedSubjectFile.getFile_name() + "?");
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ade = new asyncdelete(teacherFilesActivity.this, selectedSubjectFile);
                                    ade.execute();
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
                        Toast.makeText(context, "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("subjectid", subjectID);
                params.put("filecategory", fileCategory);
                return params;
            }
        };

        queue.add(getFileRequest);
    }

    private class asyncdelete extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        SubjectFile subjectFile;
        private Context context;
        //File file = null;

        public asyncdelete(Context context, SubjectFile subjectFile){
            this.context = context;
            this.subjectFile = subjectFile;
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
            updateDatabase(context,getString(R.string.delete_file_url), subjectFile);
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
                ftpClient.deleteFile(subjectFile.getFile_path());
            }
            catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }
    }


    private class asyncdownload extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        SubjectFile subjectFile;
        private Context context;
        //File file = null;

        protected  void onProgressUpdate(Integer... values){
        }

        @Override
        protected void onProgressUpdate(String... values) {
        }

        public asyncdownload(Context context, SubjectFile subjectFile){
            this.context = context;
            this.subjectFile = subjectFile;
        }

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(getString(R.string.downloading));
            progressDialog.setMessage(getString(R.string.pleasewait));
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

            try {
                ftpClient.connect(host);
                ftpClient.enterLocalPassiveMode();
                ftpClient.login(user, password);
            }
            catch(IOException e){
                e.printStackTrace();
            }

            //create download directory
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

    private class asyncupload extends  AsyncTask<String, String, String>{

        private String resp;
        private boolean success = false;
        ProgressDialog progressDialog;
        String uploadDir = "/uploads/";
        String tempDir = "/uploads/";
        private Context context;
        boolean fileExist = false;


        public asyncupload(Context context){
            this.context = context;
        }


        @Override
        protected String doInBackground(String... params) {

            FTPClient ftp = new FTPClient();

            int reply;
            String host = "files.000webhost.com";
            String user = "tarucclassroom";
            String password = "1o1ip0pp";

            try {
                ftp.connect(host);
                ftp.enterLocalPassiveMode();
                ftp.login(user, password);
                ftp.setFileType(FTP.BINARY_FILE_TYPE);

                int returnCode=0;
                InputStream inputStream = ftp.retrieveFileStream(tempDir + subjectID + "/" + fileCategory + "/" +fileName);
                returnCode = ftp.getReplyCode();
                if(inputStream == null || returnCode == 550){
                    fileExist = false;
                }
                else{
                    fileExist = true;
                }

                if(fileExist == false) {
                    uploadDir += subjectID;
                    boolean checkDirExist = ftp.changeWorkingDirectory(uploadDir);
                    if (!checkDirExist) {
                        ftp.makeDirectory(uploadDir);
                    }
                    uploadDir += "/" + fileCategory;
                    checkDirExist = ftp.changeWorkingDirectory(uploadDir);
                    if (!checkDirExist) {
                        ftp.makeDirectory(uploadDir);
                    }
                    uploadDir += "/";
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }

            if(fileExist == false) {
                try {
                    InputStream inputStream = new FileInputStream(new File(filePath));
                    try {
                        success = ftp.storeFile(uploadDir + fileName, inputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (ftp.isConnected()) {
                try {
                    ftp.logout();
                    ftp.disconnect();
                } catch (IOException f) {
                    // do nothing as file is already saved to server
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            if(success && fileExist == false) {
                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentTime = sdf.format(dt);

                SubjectFile subjectFile = new SubjectFile(subjectID, fileName, uploadDir + fileName, fileCategory, currentTime);

                try {
                    updateDatabase(context, getString(R.string.upload_file_url), subjectFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                uploadDir = "/uploads/";  //reset path
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            else if(fileExist = true){
                Toast.makeText(context, getString(R.string.upload_file_already_exist_error), Toast.LENGTH_LONG).show();
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }

        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(getString(R.string.uploading));
            progressDialog.setMessage(getString(R.string.pleasewait));
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    au.cancel(true);
                }
            });
            if(!progressDialog.isShowing())
                progressDialog.show();
        }

    }
                                                                                              //upload file info to db
    public void updateDatabase(Context context, String url, final SubjectFile subjectFile) {  //context: what context called this
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
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("subjectid", subjectFile.getSubject_id());
                    params.put("filename", subjectFile.getFile_name());
                    params.put("filepath", subjectFile.getFile_path());
                    params.put("filecategory", subjectFile.getFile_category());
                    params.put("uploaddatetime", subjectFile.getUpload_datetime());
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

    public void showFile(File file, String extension){    //OPEN DOWNLOADED FILE
        String type="application/";
        if((extension.equals("pdf") || extension.equals("docx") || extension.equals("doc") || extension.equals("ppt") || extension.equals("pptx"))&& android.os.Build.VERSION.SDK_INT < 24){
            if(extension.equals("docx")){
                type+="msword";
            }
            else if(extension.equals("pptx")){
                type+="vnd.ms-powerpoint";
            }
            else {
                type += extension;
            }
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
            if(extension.equals("doc") || extension.equals("docx")){
                type+="msword";
            }
            else if(extension.equals("pptx") || extension.equals("pptx")){
                //type+="vnd.openxmlformats-officedocument.wordprocessingml.document";
                type+="vnd.ms-powerpoint";
            }
            else {
                type += extension;
            }
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

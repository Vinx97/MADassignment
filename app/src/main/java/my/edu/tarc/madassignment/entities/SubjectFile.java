package my.edu.tarc.madassignment.entities;


import java.io.Serializable;

/**
 * Created by ASUS on 29/12/2017.
 */

public class SubjectFile implements Serializable{

    private String subject_id;
    private String file_name;
    private String file_path;
    private String file_category;
    private String upload_datetime;

    public SubjectFile(String subject_id, String file_name, String file_path, String file_category, String  upload_datetime) {
        this.subject_id = subject_id;
        this.file_name = file_name;
        this.file_path = file_path;
        this.file_category = file_category;
        this. upload_datetime =  upload_datetime;
    }

    public String getUpload_datetime() {
        return upload_datetime;
    }

    public void setUpload_datetime(String upload_datetime) {
        this.upload_datetime = upload_datetime;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public String getFile_category() {
        return file_category;
    }

    public void setFile_category(String file_category) {
        this.file_category = file_category;
    }

    @Override
    public String toString() {
        return file_name;
    }
}
